package com.soc.game.manager;

import com.google.common.collect.*;
import com.soc.SocWars;
import com.soc.database.Database;
import com.soc.database.stats.BaseGameTable;
import com.soc.database.stats.BaseTable;
import com.soc.database.stats.CombatTable;
import com.soc.game.map.AbstractGameMap;
import com.soc.game.map.SpreadRules;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
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
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.soc.lib.SocWarsLib.*;

public abstract class AbstractGameManager {
    public static final int KILLZONE_Y_OFFSET = -25;

    private final int gameId;

    protected final AbstractGameMap map;
    protected final ServerWorld world;
    protected final Multimap<DyeColor, ServerPlayerEntity> teams;
    protected final List<ServerPlayerEntity> spectators;
    protected final Map<DyeColor, Team> scoreboardTeams;
    protected final @Nullable EventQueue eventQueue;

    protected final Map<ServerPlayerEntity, BaseGameTable> dbTables;
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

        this.dbTables = players.stream().collect(Collectors.toMap(key -> key, this.dbTableBuilder()));
        this.killHeight = this.getMap().getCentrePos().getY() + KILLZONE_Y_OFFSET;
    }

    protected abstract AbstractGameMap getMap();
    protected abstract AbstractGameMap buildMap();
    public abstract Multimap<DyeColor, ServerPlayerEntity> buildTeams(Set<ServerPlayerEntity> players, SpreadRules spreadRules);
    public final Map<DyeColor, Team> buildScoreboardTeams() {
        return this.teams.entries().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> this.addTeamFromColour(entry.getKey())));
    }
    protected abstract @Nullable EventQueue buildEventQueue();
    protected abstract Function<ServerPlayerEntity, ? extends BaseGameTable> dbTableBuilder();

    public void startGame() {
        this.removePlayersVelocity();

        final AbstractGameMap map = this.getMap();
        map.placeMap();
        map.spawnCages(true);
        map.spreadPlayers(this.teams);

        this.assignPlayersToTeams();
        this.setGameMode(GameMode.ADVENTURE);
        this.healPlayers();
        this.clearPlayerInventories();
        this.removePlayersAttributes();

        PrescheduledEvents.playCountdown(() -> {
            map.spawnCages(false);
            this.setGameMode(GameMode.SURVIVAL);
        }, this, 5, 20, 50, SoundEvents.BLOCK_NOTE_BLOCK_GUITAR.value(), null);
    }

    public void endGame(boolean immediate) {
        this.removeTeams();
        this.getMap().destroyMap();
        this.sendPlayersToLobby();

        Database.getStatement().ifPresent(statement -> this.dbTables.values().forEach(table -> {
            SocWars.LOGGER.info("Saving db table for {}", this.gameId);
            table.updateSql(statement);
        }));

        GamesManager.getInstance().endGame(this.gameId);
    }

    public boolean onPlayerDeath(ServerPlayerEntity player, DamageSource source, float amount) {
        final BaseTable targetTable = this.dbTables.get(player);
        if (!(targetTable instanceof CombatTable)) return true;

        ((CombatTable)targetTable).grantDeath();
        getPlayerAttacker(player).ifPresent(killer -> ((CombatTable)this.dbTables.get(killer)).grantKill());

        this.healPlayer(player);
        resetScale(player);

        player.getWorld().playSound(null, player.getBlockPos(), SoundEvents.ENTITY_PLAYER_DEATH, SoundCategory.PLAYERS, 1, 1);
        player.getWorld().playSound(null, BlockPos.ofFloored(this.getMap().getRespawnSpectatorPos()), SoundEvents.ENTITY_PLAYER_DEATH, SoundCategory.PLAYERS, 1, 1);

        return true;
    }

    public boolean onPlayerDamage(ServerPlayerEntity player, DamageSource source, float amount) {
        final BaseTable targetTable = this.dbTables.get(player);
        if (!(targetTable instanceof CombatTable)) return true;

        final int cappedDamage = (int)Math.min(player.getHealth(), amount);

        ((CombatTable)targetTable).takeDamage(cappedDamage);

        final CombatTable attackerTable = ((CombatTable)this.dbTables.get(source.getAttacker())); //More Map#get abuse
        if (attackerTable != null) attackerTable.dealDamage(cappedDamage);

        return true;
    }

    public void onChestOpened(ServerPlayerEntity player, BlockPos pos) {}

    public void onItemPickup(ServerPlayerEntity player, ItemStack stack) {}

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
        return teams.values();
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
        this.teams.forEach((team, player) -> world.getScoreboard().addScoreHolderToTeam(player.getNameForScoreboard(), this.scoreboardTeams.get(team)));
    }

    private void updateEventQueue() {
        if (this.eventQueue == null) return;

        final Collection<Pair<Consumer<AbstractGameManager>, String>> events = this.eventQueue.tryPopEvents(this.time);
        events.forEach(event -> {
            event.getLeft().accept(this);
        });
    }

    public final @Nullable Collection<Text> getUpcomingEvents() {
        return this.eventQueue == null ? null : this.eventQueue.peekEventsText(this.time);
    }

    public final int getGameId() {
        return this.gameId;
    }

    public final BlockPos generateCentrePosition() {
        final BlockPos initial = new BlockPos(20000, 0, 20000);
        return initial.add(0, 0, 500 * this.gameId);
    }

    public final DyeColor getTeam(ServerPlayerEntity player) {
        return this.teams.entries().stream().filter(entry -> entry.getValue() == player).findFirst().map(Map.Entry::getKey).orElse(null);
    }

    public final BlockPos getSpawnPosition(ServerPlayerEntity player) {
        return this.getMap().getSpawnPosition(this.getTeam(player));
    }

    protected void broadcast(Text text, boolean overlay) {
        this.getPlayers().forEach(player -> player.sendMessage(text, overlay));
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

    protected void clearTitle() {
        this.getPlayers().forEach(player -> player.networkHandler.sendPacket(new ClearTitleS2CPacket(false)));
    }

    protected void broadcastSound(SoundEvent sound) {
        this.getPlayers().forEach(player -> player.playSoundToPlayer(sound, SoundCategory.PLAYERS, 1f, 1f));
    }

    protected void setGameMode(GameMode gameMode) {
        this.getPlayers().forEach(player -> player.changeGameMode(gameMode));
    }

    protected static void allowFlight(ServerPlayerEntity player, boolean allow) {
        player.getAbilities().allowFlying = allow;
    }

    protected void allowFlight(boolean allow) {
        this.getPlayers().forEach(player -> player.getAbilities().allowFlying = allow);
    }

    protected void makePlayerSpectator(ServerPlayerEntity player) {
        final Vec3d pos = this.getMap().getRespawnSpectatorPos();
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
        final Vec3d pos = this.world.getSpawnPos().toCenterPos();

        this.getPlayers().forEach(player -> player.requestTeleport(pos.x + this.world.random.nextFloat() * 3f, pos.y, pos.z + this.world.random.nextFloat() * 3f));
        this.setGameMode(GameMode.ADVENTURE);
        this.healPlayers();
        this.clearPlayerInventories();
    }

    protected final void healPlayers() {
        this.getPlayers().forEach(this::healPlayer);
    }

    protected final void healPlayer(ServerPlayerEntity player) {
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

    protected final void clearPlayerInventories() {
        this.getPlayers().forEach(player -> player.getInventory().clear());
    }

    protected final void removePlayersAttributes() {
        this.getPlayers().forEach(this::removePlayerAttributes);
    }

    protected final void removePlayerAttributes(ServerPlayerEntity player) {
        player.getAttributes().getAttributesToSend().forEach(instance -> instance.getModifiers().forEach(instance::removeModifier));
    }

    protected void tickKillzone() {
        this.getPlayers().forEach(player -> {
            @Nullable final PlayerEntity attacker = getPlayerAttacker(player).orElse(null);
            if (player.getY() < this.killHeight) this.onPlayerDeath(player, damageSource(world, DamageTypes.OUT_OF_WORLD, attacker), 100000f);
        });
    }

    public void joinAsSpectator(ServerPlayerEntity player) {
        this.spectators.add(player);

        final Vec3d pos = this.getMap().getRespawnSpectatorPos();
        player.requestTeleport(pos.x, pos.y, pos.z);


    }

    public void
}
