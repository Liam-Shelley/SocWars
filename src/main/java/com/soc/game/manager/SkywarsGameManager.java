package com.soc.game.manager;

import com.google.common.collect.ImmutableMultimap;
import com.soc.database.stats.BaseGameTable;
import com.soc.database.stats.SkywarsTable;
import com.soc.game.map.AbstractGameMap;
import com.soc.game.map.SkywarsGameMap;
import com.soc.game.map.SpreadRules;
import com.soc.lib.Events;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
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
    public void endGame() {
        Events.getInstance().scheduleEvent(() -> {
            this.playerMap.forEach((key, stats) -> {
                final Text message = stats.isAlive() ? Text.translatable("game.skywars.win") : Text.translatable("game.skywars.lose");
                key.networkHandler.sendPacket(new TitleS2CPacket(message));

                key.playSoundToPlayer(stats.isAlive() ? SoundEvents.ENTITY_PLAYER_LEVELUP : SoundEvents.BLOCK_BELL_USE, SoundCategory.PLAYERS, 1, 1);
            });
        }, 10);

        Events.getInstance().scheduleEvent(super::endGame, 5 * 20);
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
    protected Function<ServerPlayerEntity, SkywarsTable> dbTableBuilder() {
        return SkywarsTable::new;
    }

    @Override
    public boolean onPlayerDeath(ServerPlayerEntity player, DamageSource source, float amount) {
        final boolean canRespawn = this.playerMap.get(player).kill();

        player.setHealth(player.getMaxHealth());
        super.makePlayerSpectator(player);

        super.broadcastDeath(player, source, !canRespawn);

        if (this.getAlivePlayers().size() < (this.getPlayers().size() > 1 ? 2 : 1)) {
            this.endGame();
            return false;
        }

        if (canRespawn) {
            PrescheduledEvents.playCountdown(() -> super.respawnPlayer(player), this, 3, 20, SoundEvents.BLOCK_NOTE_BLOCK_GUITAR.value(), player);
        } else {
            player.networkHandler.sendPacket(new TitleS2CPacket(Text.translatable("game.skywars.eliminate")));
        }

        return false;
    }

    private List<ServerPlayerEntity> getAlivePlayers() {
        return this.playerMap.entrySet().stream().filter(entry -> entry.getValue().isAlive()).map(Map.Entry::getKey).toList();
    }
}
