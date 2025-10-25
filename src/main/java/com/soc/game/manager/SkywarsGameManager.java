package com.soc.game.manager;

import com.google.common.collect.ImmutableMultimap;
import com.soc.game.map.AbstractGameMap;
import com.soc.game.map.SkywarsGameMap;
import com.soc.game.map.SpreadRules;
import com.soc.lib.Events;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static com.soc.game.map.AbstractGameMap.getRandomPlayerStack;
import static com.soc.lib.SocWarsLib.multimapFromCollections;

public class SkywarsGameManager extends AbstractGameManager {
    private final Settings settings;
    private final Map<ServerPlayerEntity, PlayerStats> playerMap;

    public static class Settings {
        public static final Settings DEFAULT = new Settings(5);

        private final int lives;

        public Settings(int lives) {
            this.lives = lives;
        }
    }

    private class PlayerStats {
        private int lives;

        public PlayerStats() {
            this.lives = SkywarsGameManager.this.settings.lives;
        }

        public boolean kill() {
            return --lives > 0;
        }

        public boolean isAlive() {
            return lives > 0;
        }
    }

    protected SkywarsGameManager(
            ServerWorld world,
            Set<ServerPlayerEntity> players,
            @Nullable SpreadRules spreadRules,
            int gameId,
            Settings settings
    ) {
        super(world, players, spreadRules, gameId);
        this.settings = settings;
        this.playerMap = players.stream().collect(Collectors.toMap(key -> key, key -> new PlayerStats()));
    }

    @Override
    protected SkywarsGameMap getMap() {
        return (SkywarsGameMap) super.map;
    }

    @Override
    protected AbstractGameMap buildMap() {
        Optional<SkywarsGameMap> map = SkywarsGameMap.loadRandomMap(super.world, super.generateCentrePosition());

        if (map.isEmpty()) throw new IllegalStateException("No SkyWars map found");
        return map.get();
    }

    @Override
    public ImmutableMultimap<DyeColor, ServerPlayerEntity> buildTeams(Set<ServerPlayerEntity> players, @Nullable SpreadRules spreadRules) {
        final Stack<ServerPlayerEntity> playerStack = getRandomPlayerStack(players);

        final Set<DyeColor> teamColoursList = this.getMap().getTeamColours();

        return multimapFromCollections(teamColoursList, playerStack);
    }

    @Override
    protected @Nullable EventQueue buildEventQueue() {
        return null;
    }

    @Override
    public boolean onPlayerDeath(ServerPlayerEntity player, DamageSource source, float amount) {
        final boolean canRespawn = this.playerMap.get(player).kill();

        player.setHealth(player.getMaxHealth());
        super.makePlayerSpectator(player);

        if (canRespawn) {
            PrescheduledEvents.playCountdown(() -> super.respawnPlayer(player), this, 3, 20, SoundEvents.BLOCK_NOTE_BLOCK_GUITAR.value());
        } else {
            player.networkHandler.sendPacket(new TitleS2CPacket(Text.translatable("skywars.game.lose")));
        }

        if (this.numAlivePlayers() < 1) {
            Events.getInstance().scheduleEvent(this::endGame, 5 * 20);
        }

        return false;
    }

    private int numAlivePlayers() {
        return (int)this.playerMap.values().stream().filter(PlayerStats::isAlive).count();
    }
}
