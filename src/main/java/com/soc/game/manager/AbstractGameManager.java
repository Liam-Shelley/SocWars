package com.soc.game.manager;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.soc.game.map.AbstractGameMap;
import com.soc.game.map.SpreadRules;
import com.soc.lib.Events;
import net.minecraft.block.Blocks;
import net.minecraft.entity.damage.DamageSource;
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
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.soc.lib.SocWarsLib.formattingColourFromDye;

public abstract class AbstractGameManager {
    public static final ImmutableSet<DyeColor> FOUR_TEAMS_COLOURS = ImmutableSet.copyOf(new DyeColor[]{DyeColor.RED, DyeColor.YELLOW, DyeColor.GREEN, DyeColor.LIGHT_BLUE});
    public static final ImmutableSet<DyeColor> EIGHT_TEAMS_COLOURS = ImmutableSet.copyOf(new DyeColor[]{DyeColor.RED, DyeColor.ORANGE, DyeColor.YELLOW, DyeColor.GREEN, DyeColor.LIGHT_BLUE, DyeColor.BLUE, DyeColor.PURPLE, DyeColor.MAGENTA});

    protected final AbstractGameMap map;
    protected final ServerWorld world;
    protected final ImmutableMultimap<DyeColor, ServerPlayerEntity> teams;
    protected final ImmutableMap<DyeColor, Team> scoreboardTeams;
    protected final @Nullable EventQueue eventQueue;

    private final int gameId;

    protected int time;

    protected AbstractGameManager(ServerWorld world, Set<ServerPlayerEntity> players, SpreadRules spreadRules, int gameId) {
        this.world = world;
        this.map = this.buildMap();
        this.teams = this.buildTeams(players, spreadRules);
        this.scoreboardTeams = this.buildScoreboardTeams();
        this.eventQueue = this.buildEventQueue();
        this.gameId = gameId;
    }

    protected abstract AbstractGameMap getMap();
    protected abstract AbstractGameMap buildMap();
    public abstract ImmutableMultimap<DyeColor, ServerPlayerEntity> buildTeams(Set<ServerPlayerEntity> players, SpreadRules spreadRules);
    public final ImmutableMap<DyeColor, Team> buildScoreboardTeams() {
        ImmutableMap.Builder<DyeColor, Team> builder = ImmutableMap.builder();

        this.teams.keySet().forEach(colour -> builder.put(colour, addTeamFromColour(colour)));

        return builder.build();
    }
    protected abstract @Nullable EventQueue buildEventQueue();

    public void startGame() {
        final AbstractGameMap map = this.getMap();
        map.placeMap();
        map.spawnCages(true);
        map.spreadPlayers(this.teams);
        this.setGameMode(GameMode.ADVENTURE);

        PrescheduledEvents.playCountdown(() -> {
            map.spawnCages(false);
            this.setGameMode(GameMode.SURVIVAL);
        }, this, 5, 20, 50, SoundEvents.BLOCK_NOTE_BLOCK_GUITAR.value());
    }

    public void endGame() {
        this.removeTeams();
        this.getMap().destroyMap();
        GamesManager.getInstance().endGame(this.gameId);
    }

    public boolean onPlayerDeath(ServerPlayerEntity player, DamageSource source, float amount) {
        return true;
    }

    public void tick() {
        time++;
        this.map.tick();
        this.updateEventQueue();
    }

    public final void removeTeams() {
        final Scoreboard scoreboard = world.getScoreboard();
        scoreboardTeams.values().forEach(scoreboard::removeTeam);
    }

    public final ImmutableCollection<ServerPlayerEntity> getPlayers() {
        return teams.values();
    }

    public final Team addTeamFromColour(DyeColor colour) {
        final Team team = this.world.getScoreboard().addTeam(this.gameId + "_" + colour.toString());
        team.setColor(formattingColourFromDye(colour));
        team.setDisplayName(Text.of(StringUtils.capitalize(colour.toString())));
        team.setFriendlyFireAllowed(false);
        team.setCollisionRule(AbstractTeam.CollisionRule.PUSH_OTHER_TEAMS);

        return team;
    }

    public final ArrayList<Team> addTeamsFromColours(Set<DyeColor> colours) {
        final ArrayList<Team> teams = new ArrayList<>();
        colours.forEach(colour -> teams.add(this.addTeamFromColour(colour)));

        return teams;
    }

    private void updateEventQueue() {
        if (this.eventQueue == null) return;

        final Collection<Pair<Consumer<AbstractGameManager>, String>> events = this.eventQueue.tryPopEvents(this.time);
        events.forEach(event -> {
            event.getLeft().accept(this);
        });
    }

    public final Collection<Text> getUpcomingEvents() {
        return this.eventQueue.peekEventsText(this.time);
    }

    public final int getGameId() {
        return this.gameId;
    }

    public final BlockPos generateCentrePosition() {
        final BlockPos initial = new BlockPos(20000, 0, 20000);
        return initial.add(0, 0, 500 * this.gameId);
    }

    public final DyeColor getTeam(ServerPlayerEntity player) {
        return this.teams.inverse().get(player).stream().findFirst().orElse(null);
    }

    public final BlockPos getSpawnPosition(ServerPlayerEntity player) {
        return this.getMap().getSpawnPosition(this.getTeam(player));
    }

    protected void broadcast(Text text, boolean overlay) {
        this.getPlayers().forEach(player -> player.sendMessage(text, overlay));
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
    }
}
