package com.soc.game.manager;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.soc.database.stats.BedwarsTable;
import com.soc.game.manager.bedwars.BedwarsShopContents;
import com.soc.game.manager.bedwars.PlayerStats;
import com.soc.game.manager.bedwars.TeamStats;
import com.soc.game.map.*;
import com.soc.items.components.ModComponents;
import com.soc.lib.Events;
import com.soc.networking.helper.Teams;
import com.soc.networking.s2c.bedwars.ShopDataPayload;
import com.soc.networking.s2c.bedwars.JoinBedwarsPayload;
import com.soc.networking.s2c.bedwars.LeaveBedwarsPayload;
import com.soc.networking.s2c.bedwars.BedBreakPayload;
import com.soc.resourcedata.containers.BedwarsGeneratorDataContainer;
import com.soc.resourcedata.containers.BedwarsShopDataContainer;
import com.soc.resourcedata.deserialisation.ResourceGeneratorUpgrade;
import com.soc.util.Sounds;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.soc.game.map.AbstractGameMap.getRandomPlayerStack;
import static com.soc.lib.SocWarsLib.*;

public class BedwarsGameManager extends AbstractGameManager<BedwarsGameMap, BedwarsTable, BedwarsGameManager> {
    protected static final Item[] RESOURCES = { Items.IRON_INGOT, Items.GOLD_INGOT, Items.DIAMOND, Items.EMERALD };

    private final Map<UUID, PlayerStats> playerStatsMap;
    private final Map<DyeColor, TeamStats> teamStatsMap;

    protected BedwarsGameManager(ServerWorld world, Set<ServerPlayerEntity> players, @NotNull SpreadRules spreadRules, int gameId) {
        super(world, players, spreadRules, gameId);

        final long shopSeed = world.random.nextLong();
        this.playerStatsMap = players.stream().collect(Collectors.toMap(ServerPlayerEntity::getUuid, player -> new PlayerStats(player, shopSeed)));
        this.teamStatsMap = super.teams.keySet().stream().collect(Collectors.toMap(Function.identity(), team -> new TeamStats(team, super.teams.get(team).stream().map(this.playerStatsMap::get).collect(Collectors.toSet()))));
    }

    @Override
    protected BedwarsGameMap buildMap() {
        final Optional<BedwarsGameMap> map = AbstractGameMap.loadRandomMap(super.world, super.generateCentrePosition(), BedwarsGameMap::fromNbt, BedwarsGameMap.FILE_EXTENSION);

        if (map.isEmpty()) throw new IllegalStateException("No Bedwars map found");

        return map.get();
    }

    @Override
    public void startGame() {
        super.startGame();

        super.map.getBedPositions().forEach((team, pos) -> {
            if (!super.teams.containsKey(team)) {
                super.world.breakBlock(super.map.pos(pos).down(), false);
            }
        });
    }

    @Override
    public void endGame(boolean immediate) {
        this.eventQueue.cancelEvents();

        this.playerStatsForEach((player, stats) -> {
            final Text message;
            final SoundEvent sound;
            final BedwarsTable dbTable = this.getDbTable(player);
            if (this.teamStatsMap.get(super.getTeam(player)).isAlive()) {
                message = Text.translatable("game.bedwars.win");
                sound = SoundEvents.ENTITY_PLAYER_LEVELUP;
                dbTable.win();
            } else {
                message = Text.translatable("game.bedwars.lose");
                sound = SoundEvents.BLOCK_BELL_USE;
                dbTable.lose();
            }

            Events.getInstance().scheduleEvent(() -> {
                player.networkHandler.sendPacket(new TitleS2CPacket(message));

                player.playSoundToPlayer(sound, SoundCategory.PLAYERS, 1, 1);
            }, 10);
        });

        if (immediate) {
            super.endGame(true);
        } else {
            Events.getInstance().scheduleEvent(() -> super.endGame(false), 5 * 20);
        }
    }

    protected final PlayerStats getPlayerStats(ServerPlayerEntity player) {
        return this.playerStatsMap.get(player.getUuid());
    }

    protected final void playerStatsForEach(BiConsumer<ServerPlayerEntity, PlayerStats> biConsumer) {
        this.playerStatsMap.forEach((uuid, stats) -> biConsumer.accept((ServerPlayerEntity)super.world.getPlayerByUuid(uuid), stats));
    }

    @Override
    public Multimap<DyeColor, UUID> buildTeams(Set<ServerPlayerEntity> players, SpreadRules spreadRules) {
        //Probably rewrite this at some point it's a bit gross

        final Stack<UUID> playerStack = getRandomPlayerStack(players.stream().map(ServerPlayerEntity::getUuid).toList());

        final Set<DyeColor> teamColours = super.map.getTeamColours();
        final int numTeams = Math.min(spreadRules.numTeams(), teamColours.size());

        final ImmutableMultimap.Builder<DyeColor, UUID> builder = ImmutableMultimap.builder();

        final ArrayList<DyeColor> teamsUnlimited = new ArrayList<>(teamColours);
        Collections.shuffle(teamsUnlimited);
        final List<DyeColor> teams = teamsUnlimited.stream().limit(numTeams).toList();

        for (int i = 0; i < players.size(); i++) {
            builder.put(teams.get(i % numTeams), playerStack.pop());
        }

        return builder.build();


//        final ImmutableMultimap.Builder<DyeColor, ServerPlayerEntity> builder2 = ImmutableMultimap.builder();
//
//        final DyeColor firstTeam = teamColours.stream().findFirst().get();
//
//        players.forEach(player -> builder2.put(firstTeam, player));
//
//        return builder2.build();

    }

    @Override
    protected EventQueue<BedwarsGameManager> buildEventQueue() {
        final EventQueue<BedwarsGameManager> queue = new EventQueue<>();

        {
            final BedwarsGeneratorDataContainer bedwarsGeneratorDataContainer = BedwarsGeneratorDataContainer.INSTANCE;
            for (int i = 0; i < bedwarsGeneratorDataContainer.getDiamondGeneratorUpgrades().size(); i++) {
                final ResourceGeneratorUpgrade upgrade = bedwarsGeneratorDataContainer.getDiamondGeneratorUpgrades().get(i);
                queue.addEvent(upgrade.time(), manager -> manager.upgradeDiamondGens(upgrade.getStats()), Text.translatable("events.bedwars.diamond.tier", romanNumerals(i)));
            }
            for (int i = 0; i < bedwarsGeneratorDataContainer.getEmeraldGeneratorUpgrades().size(); i++) {
                final ResourceGeneratorUpgrade upgrade = bedwarsGeneratorDataContainer.getEmeraldGeneratorUpgrades().get(i);
                queue.addEvent(upgrade.time(), manager -> manager.upgradeEmeraldGens(upgrade.getStats()), Text.translatable("events.bedwars.emerald.tier", romanNumerals(i)));
            }
        }

        return queue;
    }

    @Override
    protected Function<UUID, BedwarsTable> dbTableBuilder() {
        return BedwarsTable::new;
    }

    @Override
    protected void sendJoinGamePayload(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, new JoinBedwarsPayload(super.getGameId(), new Teams(super.teams, this.teamStatsMap)));
    }

    @Override
    protected void sendLeaveGamePayload(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, new LeaveBedwarsPayload());
    }

    @Override
    public boolean onPlayerDeath(ServerPlayerEntity player, DamageSource source, float amount) {
        getPlayerAttacker(player).ifPresentOrElse(attacker -> giveResourcesToPlayer(player, (ServerPlayerEntity) attacker), () -> dropResources(player));

        final boolean canRespawn = this.canRespawn(player);
        this.getPlayerStats(player).onDeath(canRespawn, super.world);

        this.broadcastDeath(player, source, !canRespawn);

        super.onPlayerDeath(player, source, amount);

        if (this.getAliveTeams().size() < (super.teams.keySet().size() > 1 ? 2 : 1)) {
            this.endGame(false);
            return false;
        }

        if (canRespawn) {
            PrescheduledEvents.playCountdown(() -> super.respawnPlayer(player), this, 5, 20, SoundEvents.BLOCK_FUNGUS_STEP, player);
        } else {
            player.networkHandler.sendPacket(new TitleS2CPacket(Text.translatable("game.bedwars.eliminate")));
        }

        return false;
    }

    @Override
    protected boolean canRespawn(ServerPlayerEntity player) {
        return this.teamStatsMap.get(super.getTeam(player)).hasBed();
    }

    protected Set<ServerPlayerEntity> getAlivePlayers() {
        return this.playerStatsMap.values().stream().filter(PlayerStats::isAlive).map(playerStats -> (ServerPlayerEntity)this.world.getPlayerByUuid(playerStats.getPlayer())).collect(Collectors.toSet());
    }

    protected Set<DyeColor> getAliveTeams() {
        return this.teamStatsMap.values().stream().filter(TeamStats::isAlive).map(TeamStats::getTeam).collect(Collectors.toSet());
    }

    @Override
    protected void trackDeathStats(ServerPlayerEntity player, DamageSource source) {
        if (source.isOf(DamageTypes.OUT_OF_WORLD)) this.getDbTable(player).fallInVoid();

        final BedwarsTable targetTable = this.getDbTable(player);

        final boolean isFinal = !this.canRespawn(player);

        if (isFinal) {
            targetTable.grantFinalDeath();
        } else {
            targetTable.grantDeath();
        }

        getPlayerAttacker(player).ifPresent(killer -> {
            final BedwarsTable killerTable = this.getDbTable(killer);
            if (killerTable == null) return;

            if (isFinal) {
                targetTable.grantFinalKill();
            } else {
                targetTable.grantKill();
            }
        });
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
        giver.getInventory().clear();
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
            this.getDbTable(player).collectItem(stack);
        }
        final BedwarsGameMap map = super.map;
        if (stack.get(ModComponents.RESOURCE_SPLIT) != null && map.isWithinSplitRange(player)) {
            stack.remove(ModComponents.RESOURCE_SPLIT);
            this.getPlayers().stream().filter(map::isWithinSplitRange).filter(player::isTeammate).filter(pickUpPlayer -> pickUpPlayer != player).forEach(otherPlayer -> otherPlayer.giveOrDropStack(stack));
        }
    }

    @Override
    public boolean onBedBroken(ServerPlayerEntity player, BlockPos pos) {
        final Optional<DyeColor> bedTeamOptional = super.map.getBedPositions().entrySet().stream().filter(entry -> super.map.pos(entry.getValue()).isWithinDistance(pos, 2d)).findFirst().map(Map.Entry::getKey);

        return bedTeamOptional.map(bedTeam -> {
            if (bedTeam == super.getTeam(player) && player.getGameMode() == GameMode.SURVIVAL) return false;

            final boolean brokeBed = this.teamStatsMap.get(bedTeam).breakBed();
            if (brokeBed) {
                super.broadcast(Text.translatable("game.bedwars.bed_broken.chat", colouredTextFromColour(bedTeam), player.getStyledDisplayName()), false);
                super.broadcastTitle(bedTeam, Text.translatable("game.bedwars.bed_broken.title").formatted(Formatting.DARK_RED));
                super.broadcastSound(bedTeam, SoundEvents.ENTITY_WITHER_SPAWN);
                player.playSoundToPlayer(Sounds.AIR_HORN, SoundCategory.PLAYERS, 1, 1);

                super.getDbTable(player).grantBedBreak();
                super.getPlayers(bedTeam).stream().map(super::getDbTable).forEach(BedwarsTable::loseBed);

                super.sendPayloadToPlayers(new BedBreakPayload(bedTeam));
            }

            return true;
        }).orElse(true);
    }

    public BedwarsShopContents getShopContents(UUID player) {
        return this.playerStatsMap.get(player).getShopContents();
    }

    @Nullable
    public static BedwarsGameManager getBedwarsGameManager(PlayerEntity player) {
        return GamesManager.getInstance().getGame(player).map(manager -> manager instanceof BedwarsGameManager bedwarsGameManager ? bedwarsGameManager : null).orElse(null);
    }

    public static boolean sendShopData(ServerPlayerEntity player, OptionalInt syncId) {
        final BedwarsGameManager manager = getBedwarsGameManager(player);

        if (syncId.isEmpty() || !(player instanceof ServerPlayerEntity) || manager == null) return false;

        ServerPlayNetworking.send(player, new ShopDataPayload(manager.getShopContents(player.getUuid()), syncId.getAsInt()));
        return true;
    }

    public void upgradeDiamondGens(GeneratorStats stats) {
        super.map.upgradeDiamondGens(stats);
    }

    public void upgradeEmeraldGens(GeneratorStats stats) {
        super.map.upgradeEmeraldGens(stats);
    }
}
