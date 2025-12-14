package com.soc.game.manager;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.soc.database.stats.BedwarsTable;
import com.soc.database.stats.SkywarsTable;
import com.soc.game.manager.bedwars.PlayerStats;
import com.soc.game.manager.bedwars.TeamStats;
import com.soc.game.map.*;
import com.soc.items.components.ModComponents;
import com.soc.networking.s2c.ShopDataPayload;
import com.soc.resourcedata.containers.BedwarsData;
import com.soc.resourcedata.deserialisation.ResourceGeneratorUpgrade;
import com.soc.resourcedata.listeners.GameData;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Unit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.soc.game.map.AbstractGameMap.getRandomPlayerStack;
import static com.soc.lib.SocWarsLib.getPlayerAttacker;
import static com.soc.lib.SocWarsLib.romanNumerals;

public class BedwarsGameManager extends AbstractGameManager {
    protected static final Item[] RESOURCES = { Items.IRON_INGOT, Items.GOLD_INGOT, Items.DIAMOND, Items.EMERALD };

    private final Map<ServerPlayerEntity, PlayerStats> playerStatsMap;
    private final Map<DyeColor, TeamStats> teamStatsMap;
    private final BedwarsShopContents shopContents;

    protected BedwarsGameManager(ServerWorld world, Set<ServerPlayerEntity> players, @NotNull SpreadRules spreadRules, int gameId) {
        super(world, players, spreadRules, gameId);
        this.playerStatsMap = players.stream().collect(Collectors.toMap(Function.identity(), PlayerStats::new));
        this.teamStatsMap = super.teams.keySet().stream().collect(Collectors.toMap(Function.identity(), key -> new TeamStats()));
        this.shopContents = new BedwarsShopContents();
    }

    @Override
    protected BedwarsGameMap getMap() {
        return (BedwarsGameMap) super.map;
    }

    @Override
    protected AbstractGameMap buildMap() {
        Optional<BedwarsGameMap> map = AbstractGameMap.loadRandomMap(super.world, super.generateCentrePosition(), BedwarsGameMap::fromNbt, BedwarsGameMap.FILE_EXTENSION);

        if (map.isEmpty()) throw new IllegalStateException("No Bedwars map found");
        return map.get();
    }

    @Override
    public Multimap<DyeColor, ServerPlayerEntity> buildTeams(Set<ServerPlayerEntity> players, SpreadRules spreadRules) {
        //Probably rewrite this at some point it's a bit gross

        final Stack<ServerPlayerEntity> playerStack = getRandomPlayerStack(players);

        final Set<DyeColor> teamColours = this.getMap().getTeamColours();
        final int numTeams = Math.min(spreadRules.numTeams(), teamColours.size());

        final ImmutableMultimap.Builder<DyeColor, ServerPlayerEntity> builder = ImmutableMultimap.builder();

        final ArrayList<DyeColor> teamsUnlimited = new ArrayList<>(teamColours);
        Collections.shuffle(teamsUnlimited);
        final List<DyeColor> teams = teamsUnlimited.stream().limit(numTeams).toList();

        for (int i = 0; i < players.size(); i++) {
            builder.put(teams.get(i % numTeams), playerStack.pop());
        }

        return builder.build();
    }

    @Override
    protected @Nullable EventQueue<BedwarsGameManager> buildEventQueue() {
        final EventQueue<BedwarsGameManager> queue = new EventQueue<>();

        {
            final BedwarsData bedwarsData = GameData.INSTANCE.getBedwarsData();
            for (int i = 0; i < bedwarsData.getDiamondGeneratorUpgrades().size(); i++) {
                final ResourceGeneratorUpgrade upgrade = bedwarsData.getDiamondGeneratorUpgrades().get(i);
                queue.addEvent(upgrade.time(), manager -> manager.upgradeDiamondGens(upgrade.getStats()), Text.translatable("events.bedwars.diamond.tier", romanNumerals(i)));
            }
            for (int i = 0; i < bedwarsData.getEmeraldGeneratorUpgrades().size(); i++) {
                final ResourceGeneratorUpgrade upgrade = bedwarsData.getEmeraldGeneratorUpgrades().get(i);
                queue.addEvent(upgrade.time(), manager -> manager.upgradeEmeraldGens(upgrade.getStats()), Text.translatable("events.bedwars.emerald.tier", romanNumerals(i)));
            }
        }

        return queue;
    }

    @Override
    protected Function<ServerPlayerEntity, BedwarsTable> dbTableBuilder() {
        return BedwarsTable::new;
    }

    @Override
    public boolean onPlayerDeath(ServerPlayerEntity player, DamageSource source, float amount) {
        super.onPlayerDeath(player, source, amount);
        if (source.isOf(DamageTypes.OUT_OF_WORLD)) ((SkywarsTable)this.dbTables.get(player)).fallInVoid();

        PrescheduledEvents.playCountdown(() -> super.respawnPlayer(player), this, 5, 20, SoundEvents.BLOCK_FUNGUS_STEP, player);

        this.playerStatsMap.get(player).onDeath();
        getPlayerAttacker(player).ifPresentOrElse(attacker -> giveResourcesToPlayer(player, (ServerPlayerEntity) attacker), () -> dropResources(player));

        return true;
    }

    protected static void giveResourcesToPlayer(ServerPlayerEntity giver, ServerPlayerEntity receiver) {
        for (Item resource : RESOURCES) {
            //final ItemStack stack = new ItemStack(resource, count);
            //stack.set(ModComponents.RESOURCE_COUNTED, Unit.INSTANCE);
            //receiver.giveItemStack(stack);


            final int count = giver.getInventory().count(resource);
            int stacks = count >> 6;
            int remainder = count % 64;

            do {
                final ItemStack stack = new ItemStack(resource, remainder);
                stack.set(ModComponents.RESOURCE_COUNTED, Unit.INSTANCE);
                receiver.giveItemStack(stack);

                remainder = 64;
            } while (stacks-- > 0);

        }
        //Inventories.remove(giver.getInventory(), predStack -> Arrays.asList(RESOURCES).contains(predStack.getItem()), 40 * 64, false);
    }

    protected static void dropResources(ServerPlayerEntity player) {
        for (Item resource : RESOURCES) {
            player.getInventory().forEach(stack -> {
                if (stack.isOf(resource)) player.dropStack(player.getWorld(), stack);
            });
        }
    }

    @Override
    public void onItemPickup(ServerPlayerEntity player, ItemStack stack) {
        if (stack.get(ModComponents.RESOURCE_COUNTED) != null) {
            stack.remove(ModComponents.RESOURCE_COUNTED);
            ((BedwarsTable)this.dbTables.get(player)).collectItem(stack);
        }
        final BedwarsGameMap map = this.getMap();
        if (stack.get(ModComponents.RESOURCE_SPLIT) != null && map.isWithinSplitRange(player)) {
            stack.remove(ModComponents.RESOURCE_SPLIT);
            this.getPlayers().stream().filter(map::isWithinSplitRange).filter(player::isTeammate).forEach(otherPlayer -> otherPlayer.giveOrDropStack(stack));
        }
    }

    public BedwarsShopContents getShopContents() {
        return this.shopContents;
    }

    @Nullable
    public static BedwarsGameManager getBedwarsGameManager(PlayerEntity player) {
        return GamesManager.getInstance().getGame(player).map(a -> a instanceof BedwarsGameManager bedwarsGameManager ? bedwarsGameManager : null).orElse(null);
    }

    public static boolean sendShopData(PlayerEntity player, OptionalInt syncId) {
        final BedwarsGameManager manager = getBedwarsGameManager(player);

        if (syncId.isEmpty() || !(player instanceof ServerPlayerEntity) || manager == null) return false;

        ServerPlayNetworking.send((ServerPlayerEntity)player, new ShopDataPayload(manager.getShopContents(), syncId.getAsInt()));
        return true;
    }

    public void upgradeDiamondGens(GeneratorStats stats) {
        this.getMap().upgradeDiamondGens(stats);
    }

    public void upgradeEmeraldGens(GeneratorStats stats) {

    }
}
