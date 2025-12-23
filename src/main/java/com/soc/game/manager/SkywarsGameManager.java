package com.soc.game.manager;

import com.google.common.collect.Multimap;
import com.soc.database.stats.SkywarsTable;
import com.soc.game.map.AbstractGameMap;
import com.soc.game.map.SkywarsGameMap;
import com.soc.game.map.SpreadRules;
import com.soc.lib.Events;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.soc.game.map.AbstractGameMap.getRandomPlayerStack;
import static com.soc.lib.SocWarsLib.getPlayerAttacker;
import static com.soc.lib.SocWarsLib.multimapFromCollections;

public class SkywarsGameManager extends AbstractGameManager<SkywarsGameMap, SkywarsTable, SkywarsGameManager> {
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
    protected SkywarsGameMap buildMap() {
        Optional<SkywarsGameMap> map = AbstractGameMap.loadRandomMap(super.world, super.generateCentrePosition(), SkywarsGameMap::fromNbt, SkywarsGameMap.FILE_EXTENSION);

        if (map.isEmpty()) throw new IllegalStateException("No Skywars map found");
        return map.get();
    }

    @Override
    public void startGame() {
        super.startGame();
        super.map.placeLootChests();
    }

    @Override
    public void endGame(boolean immediate) {
        this.playerMap.forEach((player, stats) -> {
            final Text message;
            final SoundEvent sound;
            final SkywarsTable dbTable = this.getDbTable(player);
            if (stats.isAlive()) {
                message = Text.translatable("game.skywars.win");
                sound = SoundEvents.ENTITY_PLAYER_LEVELUP;
                dbTable.win();
            } else {
                message = Text.translatable("game.skywars.lose");
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

    @Override
    public Multimap<DyeColor, UUID> buildTeams(Set<ServerPlayerEntity> players, @Nullable SpreadRules spreadRules) {
        final Stack<UUID> playerStack = getRandomPlayerStack(players.stream().map(ServerPlayerEntity::getUuid).toList());

        final List<DyeColor> teamColoursList = new ArrayList<>(super.map.getTeamColours());
        Collections.shuffle(teamColoursList);

        return multimapFromCollections(teamColoursList, playerStack);
    }

    @Override
    protected @Nullable EventQueue<SkywarsGameManager> buildEventQueue() {
        return null;
    }

    @Override
    protected Function<UUID, SkywarsTable> dbTableBuilder() {
        return SkywarsTable::new;
    }

    @Override
    protected void sendJoinGamePayload(ServerPlayerEntity player) {

    }

    @Override
    protected void sendLeaveGamePayload(ServerPlayerEntity player) {

    }

    @Override
    public boolean onPlayerDeath(ServerPlayerEntity player, DamageSource source, float amount) {
        super.onPlayerDeath(player, source, amount);

        this.playerMap.get(player).kill();

        final boolean canRespawn = this.canRespawn(player);
        this.broadcastDeath(player, source, !canRespawn);

        if (this.getAlivePlayers().size() < (super.getPlayers().size() > 1 ? 2 : 1)) {
            this.endGame(false);
            return false;
        }

        if (canRespawn) {
            PrescheduledEvents.playCountdown(() -> super.respawnPlayer(player), this, 3, 20, SoundEvents.BLOCK_NOTE_BLOCK_GUITAR.value(), player);
        } else {
            player.networkHandler.sendPacket(new TitleS2CPacket(Text.translatable("game.skywars.eliminate")));
        }

        return false;
    }

    @Override
    protected boolean canRespawn(ServerPlayerEntity player) {
        return this.playerMap.get(player).isAlive();
    }

    @Override
    protected void trackDeathStats(ServerPlayerEntity player, DamageSource source) {
        if (source.isOf(DamageTypes.OUT_OF_WORLD)) (this.getDbTable(player)).fallInVoid();

        final SkywarsTable targetTable = this.getDbTable(player);

        targetTable.grantDeath();
        getPlayerAttacker(player).ifPresent(killer -> {
            final SkywarsTable killerTable = this.getDbTable(killer);
            if (killerTable != null) killerTable.grantKill();
        });
    }

    @Override
    public void onChestOpened(ServerPlayerEntity player, BlockPos pos) {
        super.map.getLootChest(pos).ifPresent(chest -> {
            if (chest.open()) {
                this.getDbTable(player).openChest(chest.getTier());
            }
        });
    }

    private List<ServerPlayerEntity> getAlivePlayers() {
        return this.playerMap.entrySet().stream().filter(entry -> entry.getValue().isAlive()).map(Map.Entry::getKey).toList();
    }
}
