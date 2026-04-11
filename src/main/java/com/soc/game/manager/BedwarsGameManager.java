package com.soc.game.manager;

import com.google.common.collect.*;
import com.soc.database.stats.BedwarsTable;
import com.soc.game.manager.bedwars.BedwarsShopContents;
import com.soc.game.manager.bedwars.PlayerStats;
import com.soc.game.manager.bedwars.ShopType;
import com.soc.game.manager.bedwars.TeamStats;
import com.soc.game.manager.bedwars.tickfunctions.AbstractTickFunction;
import com.soc.game.manager.bedwars.traps.AbstractAbility;
import com.soc.game.manager.bedwars.traps.AbstractTrap;
import com.soc.game.manager.bedwars.traps.TrapManager;
import com.soc.game.map.*;
import com.soc.items.components.ModComponents;
import com.soc.lib.Events;
import com.soc.networking.helper.Teams;
import com.soc.networking.s2c.bedwars.*;
import com.soc.resourcedata.containers.BedwarsGeneratorDataContainer;
import com.soc.resourcedata.deserialisation.ResourceGeneratorUpgrade;
import com.soc.util.Sounds;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.component.ComponentType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
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
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.apache.commons.lang3.function.TriConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.soc.game.manager.bedwars.traps.TrapManager.TRAP_DETECTION_RANGE;
import static com.soc.game.map.AbstractGameMap.getRandomPlayerStack;
import static com.soc.lib.SocWarsLib.*;
import static net.minecraft.item.Items.*;

public class BedwarsGameManager extends AbstractGameManager<BedwarsGameMap, BedwarsTable, BedwarsGameManager> implements TrapGame {
    protected static final Item[] RESOURCES = { IRON_INGOT, GOLD_INGOT, Items.DIAMOND, Items.EMERALD };
    protected static final Map<Item, Formatting> RESOURCE_TO_COLOUR_MAP = Map.of(
            IRON_INGOT, Formatting.GRAY,
            GOLD_INGOT, Formatting.GOLD,
            DIAMOND, Formatting.BLUE,
            EMERALD, Formatting.GREEN
    );
    private static final Map<ComponentType<?>, TriConsumer<ItemStack, ServerPlayerEntity, BedwarsGameManager>> ITEM_PICKUP_COMPONENT_FUNCTION_MAP = Map.of(
            ModComponents.RESOURCE_SPLIT, (stack, player, manager) -> {
                    stack.remove(ModComponents.RESOURCE_SPLIT);
                    if (manager.map.isWithinSplitRange(player)) {
                        manager.getPlayers().stream().filter(pickUpPlayer -> manager.map.isWithinSplitRange(pickUpPlayer) && pickUpPlayer.isTeammate(player) && pickUpPlayer != player).forEach(otherPlayer -> {
                            final ItemStack giveStack = stack.copy();
                            manager.onItemPickup(otherPlayer, giveStack);
                            otherPlayer.giveOrDropStack(giveStack);
                        });
                    }
            },
            ModComponents.RESOURCE_COUNTED, (stack, player, manager) -> {
                    stack.remove(ModComponents.RESOURCE_COUNTED);
                    manager.getDbTable(player).collectItem(stack);
            },
            ModComponents.GENERATOR_REFERENCE, (stack, player, manager) -> {
                manager.map.getResourceGenerator(stack.get(ModComponents.GENERATOR_REFERENCE)).ifPresent(ResourceGenerator::checkItemCap);
                stack.remove(ModComponents.GENERATOR_REFERENCE);
            }
    );

    private final Map<UUID, PlayerStats> playerStatsMap;
    private final Map<DyeColor, TeamStats> teamStatsMap;

    protected BedwarsGameManager(ServerWorld world, Set<ServerPlayerEntity> players, @NotNull SpreadRules spreadRules, int gameId) {
        super(world, players, spreadRules, gameId);

        final long shopSeed = world.random.nextLong();
        this.playerStatsMap = players.stream().collect(Collectors.toMap(ServerPlayerEntity::getUuid, player -> new PlayerStats(player, this.getTeam(player), shopSeed)));
        this.teamStatsMap = this.teams.keySet().stream().collect(Collectors.toMap(Function.identity(), team -> new TeamStats(team, this.teams.get(team).stream().map(this.playerStatsMap::get).collect(Collectors.toSet()), world, shopSeed, this.map.poss(this.map.getSpawnPositions(team)))));
    }

    @Override
    protected BedwarsGameMap buildMap() {
        final Optional<BedwarsGameMap> map = AbstractGameMap.loadRandomMap(this.world, this.generateCentrePosition(), BedwarsGameMap::fromNbt, BedwarsGameMap.FILE_EXTENSION);

        if (map.isEmpty()) throw new IllegalStateException("No Bedwars map found");

        return map.get();
    }

    @Override
    public void startGame() {
        super.startGame();

        this.map.getBedPositions().forEach((team, pos) -> {
            if (!this.teams.containsKey(team)) {
                this.world.breakBlock(this.map.pos(pos).down(), false);
            }
        });
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public void endGame(boolean immediate) {
        this.eventQueue.cancelEvents();

        this.playerStatsMap.forEach((uuid, stats) -> {
            final Text message;
            final SoundEvent sound;
            final BedwarsTable dbTable = this.dbTables.get(uuid);
            if (this.teamStatsMap.get(this.getTeam(uuid)).isAlive()) {
                message = Text.translatable("game.bedwars.win");
                sound = SoundEvents.ENTITY_PLAYER_LEVELUP;
                dbTable.win();
            } else {
                message = Text.translatable("game.bedwars.lose");
                sound = SoundEvents.BLOCK_BELL_USE;
                dbTable.lose();
            }

            Events.getInstance().scheduleEvent(() -> {
                if (this.world.getPlayerByUuid(uuid) instanceof ServerPlayerEntity player) {
                    player.networkHandler.sendPacket(new TitleS2CPacket(message));

                    player.playSoundToPlayer(sound, SoundCategory.PLAYERS, 1, 1);
                }
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

    @Override
    public Multimap<DyeColor, UUID> buildTeams(Set<ServerPlayerEntity> players, SpreadRules spreadRules) {
        //Probably rewrite this at some point it's a bit gross

        final Stack<UUID> playerStack = getRandomPlayerStack(players);

        final Set<DyeColor> teamColours = this.map.getTeamColours();
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

        final BedwarsGeneratorDataContainer bedwarsGeneratorDataContainer = BedwarsGeneratorDataContainer.INSTANCE;
        for (int i = 0; i < bedwarsGeneratorDataContainer.getDiamondGeneratorUpgrades().size(); i++) {
            final ResourceGeneratorUpgrade upgrade = bedwarsGeneratorDataContainer.getDiamondGeneratorUpgrades().get(i);
            queue.addEvent(upgrade.time(), manager -> manager.upgradeDiamondGens(upgrade.getStats()), Text.translatable("events.bedwars.diamond.tier", romanNumerals(i)));
        }
        for (int i = 0; i < bedwarsGeneratorDataContainer.getEmeraldGeneratorUpgrades().size(); i++) {
            final ResourceGeneratorUpgrade upgrade = bedwarsGeneratorDataContainer.getEmeraldGeneratorUpgrades().get(i);
            queue.addEvent(upgrade.time(), manager -> manager.upgradeEmeraldGens(upgrade.getStats()), Text.translatable("events.bedwars.emerald.tier", romanNumerals(i)));
        }

        return queue;
    }

    @Override
    protected Function<UUID, BedwarsTable> dbTableBuilder() {
        return BedwarsTable::new;
    }

    @Override
    protected void sendJoinGamePayload(ServerPlayerEntity player) {
        super.sendJoinGamePayload(player);
        ServerPlayNetworking.send(player, new JoinBedwarsPayload(this.getGameId(), new Teams(this.teams, this.teamStatsMap)));
    }

    @Override
    protected void sendLeaveGamePayload(ServerPlayerEntity player) {
        ServerPlayNetworking.send(player, new LeaveBedwarsPayload());
    }

    @Override
    public boolean onPlayerDeath(ServerPlayerEntity player, DamageSource source, float amount) {
        final boolean canRespawn = this.canRespawn(player);
        this.broadcastDeath(player, source, !canRespawn);

        getPlayerAttacker(player).ifPresentOrElse(attacker -> this.giveResourcesToPlayer(player, (ServerPlayerEntity) attacker), () -> dropResources(player));

        final PlayerStats playerStats = this.getPlayerStats(player);
        playerStats.onDeath(canRespawn, this.world);
        player.getInventory().clear();
        playerStats.returnToolsToSlots(this.world);

        super.onPlayerDeath(player, source, amount);

        if (this.getAliveTeams().size() < (this.teams.keySet().size() > 1 ? 2 : 1)) {
            this.endGame(false);
            return false;
        }

        if (canRespawn) {
            PrescheduledEvents.playCountdown(() -> this.respawnPlayer(player), this, 5, 20, SoundEvents.BLOCK_FUNGUS_STEP, player);
        } else {
            player.networkHandler.sendPacket(new TitleS2CPacket(Text.translatable("game.bedwars.eliminate")));
        }

        return false;
    }

    @Override
    protected boolean canRespawn(ServerPlayerEntity player) {
        return this.teamStatsMap.get(this.getTeam(player)).hasBed();
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

    protected void giveResourcesToPlayer(ServerPlayerEntity giver, ServerPlayerEntity receiver) {
        final BedwarsTable table = this.getDbTable(receiver);
        for (Item resource : RESOURCES) {
            final int count = giver.getInventory().count(resource);
            final ItemStack stack = new ItemStack(resource, count);

            table.collectItem(stack);

            stack.set(ModComponents.RESOURCE_COUNTED, Unit.INSTANCE);
            receiver.giveItemStack(stack);

            if (count > 0) receiver.sendMessage(Text.translatable("game.bedwars.receive_items", count, resource.getDefaultStack().toHoverableText().copy().formatted(RESOURCE_TO_COLOUR_MAP.get(resource))).formatted(Formatting.DARK_GREEN), false);
        }
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
        ITEM_PICKUP_COMPONENT_FUNCTION_MAP.forEach((component, function) -> {
            if (stack.get(component) != null) function.accept(stack, player, this);
        });
    }

    @Override
    public boolean onBedBroken(ServerPlayerEntity player, BlockPos pos) {
        final Optional<DyeColor> bedTeamOptional = this.map.getBedPositions().entrySet().stream().filter(entry -> super.map.pos(entry.getValue()).isWithinDistance(pos, 2d)).findFirst().map(Map.Entry::getKey);

        return bedTeamOptional.map(bedTeam -> {
            if (bedTeam == this.getTeam(player) && player.getGameMode() == GameMode.SURVIVAL) {
                player.sendMessage(Text.translatable("game.bedwars.broke_own_bed", colouredTextFromColour(bedTeam)), false);
                return false;
            }

            final boolean brokeBed = this.teamStatsMap.get(bedTeam).breakBed();
            if (brokeBed) {
                this.broadcast(Text.translatable("game.bedwars.bed_broken.chat", colouredTextFromColour(bedTeam), player.getStyledDisplayName()), false);
                this.broadcastTitle(bedTeam, Text.translatable("game.bedwars.bed_broken.title").formatted(Formatting.DARK_RED));
                this.broadcastSound(bedTeam, SoundEvents.ENTITY_WITHER_SPAWN);
                player.playSoundToPlayer(Sounds.AIR_HORN, SoundCategory.PLAYERS, 1, 1);

                this.getDbTable(player).grantBedBreak();
                this.getPlayers(bedTeam).stream().map(this::getDbTable).forEach(BedwarsTable::loseBed);

                this.sendPayloadToPlayers(new BedBreakPayload(bedTeam));
            }

            return true;
        }).orElse(true);
    }

    @Override
    public boolean onCraftingTableOpened(ServerPlayerEntity player, BlockPos pos) {
        return false;
    }

    @Override
    public boolean onFurnaceOpened(ServerPlayerEntity player, BlockPos pos) {
        return false;
    }

    public BedwarsShopContents getIndividualShopContents(UUID player) {
        return this.playerStatsMap.get(player).getShopContents();
    }

    public BedwarsShopContents getTeamShopContents(UUID player) {
        return this.teamStatsMap.get(this.getTeam(player)).getShopContents();
    }

    public int[] getTrapProgressStats(UUID player) {
        return this.teamStatsMap.get(this.getTeam(player)).getTrapProgressStats();
    }

    @Nullable
    public static BedwarsGameManager getBedwarsGameManager(PlayerEntity player) {
        return GamesManager.getInstance().getGame(player).map(manager -> manager instanceof BedwarsGameManager bedwarsGameManager ? bedwarsGameManager : null).orElse(null);
    }

    @SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "UnusedReturnValue"})
    public static boolean sendShopData(ServerPlayerEntity player, OptionalInt syncId, ShopType shopType) {
        final BedwarsGameManager manager = getBedwarsGameManager(player);

        if (syncId.isEmpty() || !(player instanceof ServerPlayerEntity) || manager == null) return false;

        switch (shopType) {
            case INDIVIDUAL -> ServerPlayNetworking.send(player, new BedwarsIndividualShopDataPayload(manager.getIndividualShopContents(player.getUuid()), syncId.getAsInt()));
            case TEAM -> ServerPlayNetworking.send(player, new BedwarsTeamShopDataPayload(manager.getTeamShopContents(player.getUuid()), syncId.getAsInt()));
        }

        return true;
    }

    public void upgradeDiamondGens(GeneratorStats stats) {
        this.map.upgradeDiamondGens(stats);
    }

    public void upgradeEmeraldGens(GeneratorStats stats) {
        this.map.upgradeEmeraldGens(stats);
    }

    @Override
    public void tick() {
        super.tick();
        this.checkTraps();

        this.teamStatsMap.values().forEach(stats -> stats.tick(this.time, this.world));
    }

    public void checkTraps() {
        this.teamStatsMap.forEach((team, stats) -> {
            if (!stats.hasActiveTrap()) return;

            final Vec3d pos = this.map.getBedPosition(team).toCenterPos();
            final Multimap<DyeColor, ServerPlayerEntity> enemiesInRange = this.getPlayers()
                    .stream()
                    .filter(player -> this.getTeam(player) != team && player.getPos().isInRange(pos, TRAP_DETECTION_RANGE))
                    .collect(Multimaps.toMultimap(this::getTeam, Function.identity(), HashMultimap::create));

            if(!enemiesInRange.isEmpty()) stats.onPlayerInTrapRange(pos, this, enemiesInRange);
        });
    }

    public boolean buyTrap(ServerPlayerEntity player, AbstractTrap trap) {
        return this.teamStatsMap.get(this.getTeam(player)).buyTrap(trap, this.world);
    }

    public boolean buyAbility(ServerPlayerEntity player, AbstractAbility ability) {
        return this.teamStatsMap.get(this.getTeam(player)).buyAbility(ability, this.world);
    }

    public void buyEnchantmentUpgrade(ServerPlayerEntity player, RegistryEntry<Enchantment> enchantment, int tier) {
        this.teamStatsMap.get(this.getTeam(player)).buyEnchantmentUpgrade(enchantment, this.world, tier);
    }

    public void buyTickFunctionUpgrade(ServerPlayerEntity player, AbstractTickFunction function, int tier) {
        this.teamStatsMap.get(this.getTeam(player)).buyTickFunctionUpgrade(function, tier);
    }

    @Override
    public TrapManager getTrapManager(DyeColor team) {
        return this.teamStatsMap.get(team).getTrapManager();
    }
}
