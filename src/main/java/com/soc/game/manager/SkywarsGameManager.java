package com.soc.game.manager;

import com.google.common.collect.ImmutableMultimap;
import com.soc.game.map.AbstractGameMap;
import com.soc.game.map.SkywarsGameMap;
import com.soc.game.map.SpreadRules;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static com.soc.game.map.AbstractGameMap.getRandomPlayerStack;
import static com.soc.lib.SocWarsLib.multimapFromCollections;

public class SkywarsGameManager extends AbstractGameManager {
    private final int numLives;
    private final Map<ServerPlayerEntity, PlayerStats> playerMap;

    private class PlayerStats {
        private int lives;

        public PlayerStats() {
            this.lives = SkywarsGameManager.this.numLives;
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
            int numLives
    ) {
        super(world, players, spreadRules, gameId);
        this.numLives = numLives;
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
    public void startGame() {
        super.startGame();
    }

    @Override
    public void endGame() {
        super.endGame();
    }

    @Override
    public boolean onPlayerDeath(ServerPlayerEntity entity, DamageSource source, float amount) {
        final boolean canRespawn = this.playerMap.get(entity).kill();

        entity.setHealth(entity.getMaxHealth());

        if (canRespawn) {
            final Vec3d pos = super.getSpawnPosition(entity).toCenterPos();
            entity.requestTeleport(pos.x, pos.y, pos.z);
        } else {
            final Vec3d pos = this.getMap().getCentrePos().up(30).toCenterPos();
            entity.requestTeleport(pos.x, pos.y, pos.z);

            entity.changeGameMode(GameMode.SPECTATOR);
        }

        if (this.numAlivePlayers() <= 0) {
            super.endGame();
        }

        return false;
    }

    private int numAlivePlayers() {
        return (int)this.playerMap.values().stream().filter(PlayerStats::isAlive).count();
    }
}
