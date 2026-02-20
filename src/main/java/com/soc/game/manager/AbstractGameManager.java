package com.soc.game.manager;

import com.google.common.collect.*;
import com.soc.SocWars;
import com.soc.database.Database;
import com.soc.database.stats.BaseTable;
import com.soc.database.stats.CombatTable;
import com.soc.game.map.AbstractGameMap;
import com.soc.game.map.SpreadRules;
import com.soc.lib.Coroutines;
import com.soc.lib.Events;
import com.soc.networking.s2c.UpdateHotbarPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.s2c.play.ClearTitleS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.MustBeInvokedByOverriders;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.soc.lib.SocWarsLib.*;

public abstract class AbstractGameManager<MAP extends AbstractGameMap, TABLE extends BaseTable, EVENT extends AbstractGameManager<?, ?, ?>> {
    public static final int KILLZONE_Y_OFFSET = -25;

    private final int gameId;

    protected final MAP map;
    protected final ServerWorld world;
    protected final Multimap<DyeColor, UUID> teams;
    protected final List<UUID> spectators;
    protected final Map<DyeColor, Team> scoreboardTeams;
    protected final @Nullable EventQueue<EVENT> eventQueue;

    protected final Map<UUID, TABLE> dbTables;
    protected final int killHeight;

    protected int time;

    protected AbstractGameManager(ServerWorld world, Set<ServerPlayerEntity> players, SpreadRules spreadRules, int gameId) {
        this.gameId = gameId;
        this.world = world;
        this.spectators = new ArrayList<>();
        this.map = this.buildMap();
        this.teams = this.buildTeams(players, spreadRules);
        this.scoreboardTeams = this.buildScoreboardTeams();
        this.eventQueue = this.buildEventQueue();

        this.dbTables = players.stream().map(ServerPlayerEntity::getUuid).collect(Collectors.toMap(Function.identity(), this.dbTableBuilder()));
        this.killHeight = this.map.getCentrePos().getY() + KILLZONE_Y_OFFSET;
    }

    protected abstract MAP buildMap();
    protected abstract Multimap<DyeColor, UUID> buildTeams(Set<ServerPlayerEntity> players, SpreadRules spreadRules);
    private Map<DyeColor, Team> buildScoreboardTeams() {
        return this.teams.keySet().stream().collect(Collectors.toMap(Function.identity(), this::addTeamFromColour));
    }
    protected abstract @Nullable EventQueue<EVENT> buildEventQueue();

    protected abstract Function<UUID, TABLE> dbTableBuilder();
    protected final TABLE getDbTable(Entity player) {
        return player == null ? null : this.dbTables.get(player.getUuid());
    }

    protected abstract void sendJoinGamePayload(ServerPlayerEntity player);
    protected abstract void sendLeaveGamePayload(ServerPlayerEntity player);

    protected void sendPayloadToPlayers(CustomPayload payload) {
        this.getPlayers().forEach(player -> ServerPlayNetworking.send(player, payload));
    }

    protected void sendPayloadToPlayers(DyeColor team, CustomPayload payload) {
        this.getPlayers(team).forEach(player -> ServerPlayNetworking.send(player, payload));
    }

    @MustBeInvokedByOverriders
    public void startGame() {
        final MAP map = this.map;
        map.placeMap();
        map.spawnCages(true);
        map.spreadPlayers(this.teams);

        this.removePlayersVelocity();

        this.assignPlayersToTeams();
        this.setGameMode(GameMode.ADVENTURE);
        this.healPlayers();
        this.clearPlayerInventoriesAndEnderChests();
        this.removePlayersAttributes();

        PrescheduledEvents.playCountdown(() -> {
            map.spawnCages(false);
            this.setGameMode(GameMode.SURVIVAL);
        }, this, 5, 20, 50, SoundEvents.BLOCK_NOTE_BLOCK_GUITAR.value(), null);

        this.getPlayers().forEach(this::sendJoinGamePayload);
    }

    @MustBeInvokedByOverriders
    public void endGame(boolean immediate) {
        this.removeTeams();
        this.map.destroyMap(immediate);
        this.setGameMode(GameMode.SPECTATOR);

        if (immediate) {
            this.sendPlayersToLobby();
        } else {
            Events.getInstance().scheduleEvent(this::sendPlayersToLobby, 20 * 10);
        }


        Database.getStatement().ifPresent(statement -> this.dbTables.values().forEach(table -> {
            SocWars.LOGGER.info("Saving db table for {}", this.gameId);
            table.updateSql(statement);
        }));

        GamesManager.getInstance().endGame(this.gameId);

        this.getPlayers().forEach(this::sendLeaveGamePayload);
    }

    public boolean onPlayerDeath(ServerPlayerEntity player, DamageSource source, float amount) {
        this.trackDeathStats(player, source);

        healPlayer(player);
        resetScale(player);

        this.makePlayerSpectator(player);

        player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ENTITY_PLAYER_DEATH, SoundCategory.PLAYERS, 1, 1);
        player.getWorld().playSound(null, BlockPos.ofFloored(this.map.getRespawnSpectatorPos()), SoundEvents.ENTITY_PLAYER_DEATH, SoundCategory.PLAYERS, 1, 1);

        return true;
    }

    protected abstract boolean canRespawn(ServerPlayerEntity player);

    protected abstract void trackDeathStats(ServerPlayerEntity player, DamageSource source);

    public boolean onPlayerDamage(ServerPlayerEntity player, DamageSource source, float amount) {
        final BaseTable targetTable = this.getDbTable(player);
        if (!(targetTable instanceof CombatTable)) return true;

        final int cappedDamage = (int)Math.min(player.getHealth(), amount);

        ((CombatTable)targetTable).takeDamage(cappedDamage);

        final CombatTable attackerTable = ((CombatTable)this.getDbTable(source.getAttacker())); //More Map#get abuse
        if (attackerTable != null) attackerTable.dealDamage(cappedDamage);

        return true;
    }

    public void onChestOpened(ServerPlayerEntity player, BlockPos pos) {}

    public void onItemPickup(ServerPlayerEntity player, ItemStack stack) {}

    public boolean onBedBroken(ServerPlayerEntity player, BlockPos pos) {
        return true;
    }

    public boolean onBlockBroken(ServerPlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        return this.isBlockUnprotected(player, pos);
    }

    public ActionResult onBlockPlaced(ServerPlayerEntity player, BlockPos pos, ItemUsageContext context) {
        final boolean allow = this.isBlockUnprotected(player, pos);
        if (allow) {
            if (!pos.isWithinDistance(this.map.getCentrePos(), this.map.size * 1.35f)) {
                this.world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.BLOCK_NOTE_BLOCK_PLING.value(), SoundCategory.MASTER);
                player.sendMessage(Text.translatable("game.warning.placed_out_of_bounds"));
                player.networkHandler.sendPacket(new TitleS2CPacket(Text.translatable("game.warning.placed_out_of_bounds.title")));

                Events.getInstance().scheduleEvent(() -> this.world.setBlockState(pos, Blocks.AIR.getDefaultState()), 50);
            }
            return ActionResult.PASS;
        } else {
            ServerPlayNetworking.send(player, new UpdateHotbarPayload(player.playerScreenHandler.syncId, player.playerScreenHandler.getRevision(), player.getInventory()));
            return ActionResult.FAIL;
        }
    }

    public void onPlayerJoin(ServerPlayerEntity player) {
        this.sendJoinGamePayload(player);
    }

    public boolean onCraftingTableOpened(ServerPlayerEntity player, BlockPos pos) {
        return true;
    }

    public boolean onFurnaceOpened(ServerPlayerEntity player, BlockPos pos) {
        return true;
    }

    @MustBeInvokedByOverriders
    public void tick() {
        this.time++;
        this.map.tick();
        this.updateEventQueue();
        this.tickKillzone();

        this.fillPlayerHunger();
    }

    public final void removeTeams() {
        final Scoreboard scoreboard = world.getScoreboard();
        this.scoreboardTeams.values().forEach(scoreboard::removeTeam);
    }

    public final Collection<ServerPlayerEntity> getPlayers() {
        return mapUuidsToPlayers(this.world, this.teams.values());
    }

    public final Collection<ServerPlayerEntity> getPlayers(DyeColor team) {
        return mapUuidsToPlayers(this.world, this.teams.get(team));
    }

    public final Collection<ServerPlayerEntity> getSpectators() {
        return mapUuidsToPlayers(this.world, this.spectators);
    }

    protected final void playersForEach(BiConsumer<DyeColor, ServerPlayerEntity> biConsumer) {
        this.teams.forEach((team, uuid) -> biConsumer.accept(team, (ServerPlayerEntity)this.world.getPlayerByUuid(uuid)));
    }

    public static List<ServerPlayerEntity> mapUuidsToPlayers(World world, Collection<UUID> players) {
        return players.stream().map(player -> (ServerPlayerEntity)world.getPlayerByUuid(player)).filter(Objects::nonNull).toList();
    }

    protected final Team addTeamFromColour(DyeColor colour) {
        final Team team = this.world.getScoreboard().addTeam(this.getTeamName(colour));
        team.setColor(formattingColourFromDye(colour));
        team.setDisplayName(Text.of(StringUtils.capitalize(colour.toString())));
        team.setFriendlyFireAllowed(false);
        team.setCollisionRule(AbstractTeam.CollisionRule.PUSH_OTHER_TEAMS);

        return team;
    }

    protected final boolean teamAlreadyExists(DyeColor colour) {
        return this.world.getScoreboard().getTeam(this.getTeamName(colour)) != null;
    }

    protected final String getTeamName(DyeColor colour) {
        return String.format("%s_%s", this.gameId, colour.toString());
    }

    public final List<Team> addTeamsFromColours(Set<DyeColor> colours) {
        return colours.stream().map(this::addTeamFromColour).toList();
    }

    private void assignPlayersToTeams() {
        this.playersForEach((team, player) -> world.getScoreboard().addScoreHolderToTeam(player.getNameForScoreboard(), this.scoreboardTeams.get(team)));
    }

    @SuppressWarnings("unchecked")
    private void updateEventQueue() {
        if (this.eventQueue == null) return;

        this.eventQueue.tryPopAndRunEvents(time, (EVENT) this);
    }

    public final @Nullable Collection<Text> getUpcomingEvents() {
        return this.eventQueue == null ? null : this.eventQueue.peekEventsNames(this.time);
    }

    public final int getGameId() {
        return this.gameId;
    }

    public final BlockPos generateCentrePosition() {
        final BlockPos initial = new BlockPos(20000, 0, 20000);
        return initial.add(0, 0, 1000 * this.gameId);
    }

    public final DyeColor getTeam(ServerPlayerEntity player) {
        return this.getTeam(player.getUuid());
    }

    public final DyeColor getTeam(UUID uuid) {
        return this.teams.entries().stream().filter(entry -> entry.getValue().equals(uuid)).findAny().map(Map.Entry::getKey).orElse(null);
    }

    public final BlockPos getSpawnPosition(ServerPlayerEntity player) {
        return this.map.getSpawnPosition(this.getTeam(player));
    }

    protected void broadcast(Text text, final boolean overlay) {
        this.getPlayers().forEach(player -> player.sendMessage(text, overlay));
    }

    protected void broadcast(DyeColor team, Text text, final boolean overlay) {
        this.getPlayers(team).forEach(player -> player.sendMessage(text, overlay));
    }

    protected void broadcastDeath(ServerPlayerEntity player, DamageSource source, boolean isFinal) {
        final MutableText message = (source.getAttacker() != null ?
                Text.translatable("game.player.kill", player.getDisplayName(), source.getAttacker().getDisplayName()) :
                switch (source.getType()) {
                    default -> player.getDamageTracker().getDeathMessage().copy();
                }
        );


        final MutableText text = source.getAttacker() == null ?
                player.getDamageTracker().getDeathMessage().copy() :
                Text.translatable("game.player.kill", player.getDisplayName(), source.getAttacker().getDisplayName()
        );

        if (isFinal) text.append(Text.translatable("game.death.final"));

        this.broadcast(text, false);
    }

    protected void broadcastTitle(Text text) {
        this.getPlayers().forEach(player -> player.networkHandler.sendPacket(new TitleS2CPacket(text)));
    }

    protected void broadcastTitle(DyeColor team, Text text) {
        this.getPlayers(team).forEach(player -> player.networkHandler.sendPacket(new TitleS2CPacket(text)));
    }

    protected void clearTitle() {
        this.getPlayers().forEach(player -> player.networkHandler.sendPacket(new ClearTitleS2CPacket(false)));
    }

    protected void broadcastSound(SoundEvent sound) {
        this.getPlayers().forEach(player -> player.playSoundToPlayer(sound, SoundCategory.PLAYERS, 1f, 1f));
    }

    protected void broadcastSound(DyeColor team, SoundEvent sound) {
        this.getPlayers(team).forEach(player -> player.playSoundToPlayer(sound, SoundCategory.PLAYERS, 1f, 1f));
    }

    protected void setGameMode(GameMode gameMode) {
        this.getPlayers().forEach(player -> player.changeGameMode(gameMode));
    }

    protected static void allowFlight(ServerPlayerEntity player, boolean allow) {
        player.getAbilities().allowFlying = allow;
    }

    protected void allowFlight(final boolean allow) {
        this.getPlayers().forEach(player -> player.getAbilities().allowFlying = allow);
    }

    protected void makePlayerSpectator(ServerPlayerEntity player) {
        final Vec3d pos = this.map.getRespawnSpectatorPos();
        player.requestTeleport(pos.x, pos.y, pos.z);
        player.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(player.getId(), Vec3d.ZERO));

        player.changeGameMode(GameMode.SPECTATOR);
    }

    protected void respawnPlayer(ServerPlayerEntity player) {
        final Vec3d pos = this.getSpawnPosition(player).toCenterPos();
        player.requestTeleport(pos.x, pos.y, pos.z);
        player.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(player.getId(), Vec3d.ZERO));

        player.changeGameMode(GameMode.SURVIVAL);

        player.getHungerManager().setFoodLevel(20);
        player.getHungerManager().setSaturationLevel(5f);
    }

    protected final void sendPlayersToLobby() {
        this.getPlayers().forEach(this::sendPlayerToLobby);
        this.getSpectators().forEach(this::sendPlayerToLobby);
    }

    protected final void sendPlayerToLobby(ServerPlayerEntity player) {
        final Vec3d pos = this.world.getSpawnPos().toCenterPos();
        player.requestTeleport(pos.x + this.world.random.nextFloat() * 3f, pos.y, pos.z + this.world.random.nextFloat() * 3f);

        player.changeGameMode(GameMode.ADVENTURE);
        healPlayer(player);
        player.getInventory().clear();
    }

    protected final void healPlayers() {
        this.getPlayers().forEach(AbstractGameManager::healPlayer);
    }

    protected static void healPlayer(ServerPlayerEntity player) {
        player.clearStatusEffects();
        player.setHealth(player.getMaxHealth());
    }

    protected final void fillPlayerHunger() {
        this.getPlayers().forEach(player -> {
            player.getHungerManager().setFoodLevel(20);
            player.getHungerManager().setSaturationLevel(20);
        });
    }

    protected static void removePlayerVelocity(ServerPlayerEntity player) {
        player.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(player.getId(), Vec3d.ZERO));
    }

    protected final void removePlayersVelocity() {
        this.getPlayers().forEach(AbstractGameManager::removePlayerVelocity);
    }

    protected final void clearPlayerInventoriesAndEnderChests() {
        this.getPlayers().forEach(player -> {
            player.getInventory().clear();
            player.getEnderChestInventory().clear();
        });
    }

    protected final void removePlayersAttributes() {
        this.getPlayers().forEach(AbstractGameManager::removePlayerAttributes);
    }

    protected static void removePlayerAttributes(ServerPlayerEntity player) {
        player.getAttributes().getAttributesToSend().forEach(instance -> instance.getModifiers().forEach(instance::removeModifier));
    }

    protected void tickKillzone() {
        this.getPlayers().forEach(player -> {
            @Nullable final PlayerEntity attacker = getPlayerAttacker(player).orElse(null);
            if (player.getY() < this.killHeight) this.onPlayerDeath(player, damageSource(world, DamageTypes.OUT_OF_WORLD, attacker), 100000f);
        });
    }

    public void joinAsSpectator(ServerPlayerEntity player) {
        this.spectators.add(player.getUuid());

        final Vec3d pos = this.map.getRespawnSpectatorPos();
        player.requestTeleport(pos.x, pos.y, pos.z);

        player.changeGameMode(GameMode.SPECTATOR);
        healPlayer(player);
        player.getInventory().clear();
        removePlayerAttributes(player);

        this.sendJoinGamePayload(player);
    }

    public void leaveAsSpectator(ServerPlayerEntity player) {
        this.sendPlayerToLobby(player);
        this.sendLeaveGamePayload(player);
    }

    public final boolean isBlockProtected(BlockPos pos) {
        return this.map.isBlockProtected(pos);
    }

    public final boolean isBlockProtected(ServerPlayerEntity player, BlockPos pos) {
        return this.map.isBlockProtected(pos) && player.getGameMode() == GameMode.SURVIVAL;
    }

    public final boolean isBlockUnprotected(BlockPos pos) {
        return !this.map.isBlockProtected(pos);
    }

    public final boolean isBlockUnprotected(ServerPlayerEntity player, BlockPos pos) {
        return !this.map.isBlockProtected(pos) || player.getGameMode() != GameMode.SURVIVAL;
    }

    protected static List<Pair<Text, Integer>> getNTopKillers(Map<UUID, ? extends CombatTable> dbTables, World world, int n) {
        try {
            return dbTables.entrySet().stream().sorted(Comparator.comparingInt(entry -> entry.getValue().getKills())).limit(n).map(entry -> Pair.of(world.getPlayerByUuid(entry.getKey()).getDisplayName(), entry.getValue().getKills())).toList();
        } catch (Exception ignored) {
            SocWars.LOGGER.warn("Failed to retrieve top killers as a player was null");
            return List.of();
        }
    }

    public static Predicate<BlockPos> getBlockDamagePredicate(World world, boolean blockDamage, @Nullable Entity causingEntity) {
        final Optional<AbstractGameManager<?, ?, ?>> managerOptional = causingEntity == null ? Optional.empty() : GamesManager.getInstance().getGame(causingEntity);

        final Predicate<BlockPos> damage;
        if (!blockDamage) {
            damage = pos -> false;
        } else if (managerOptional.isPresent()) {
            final AbstractGameManager<?, ?, ?> manager = managerOptional.get();
            damage = manager::isBlockUnprotected;
        } else {
            final boolean def = world instanceof ServerWorld serverWorld && serverWorld.getGameRules().getBoolean(GameRules.TNT_EXPLODES);
            damage = pos -> def;
        }
        return damage;
    }
}