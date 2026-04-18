package com.soc.game.manager;

import com.google.common.collect.Multimap;
import com.soc.database.stats.SkywarsTable;
import com.soc.game.map.AbstractGameMap;
import com.soc.game.map.SkywarsGameMap;
import com.soc.game.map.SpreadRules;
import com.soc.lib.Events;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.item.ItemStack;
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
import static com.soc.lib.SocWarsLib.*;

public class SkywarsGameManager extends AbstractGameManager<SkywarsGameMap, SkywarsTable, SkywarsGameManager> {
    private final Settings settings;
    private final Map<UUID, PlayerStats> playerMap;

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
        this.playerMap = players.stream().collect(Collectors.toMap(Entity::getUuid,key -> new PlayerStats()));
    }

    @Override
    protected SkywarsGameMap buildMap() {
        final Optional<SkywarsGameMap> map = AbstractGameMap.loadRandomMap(super.world, super.generateCentrePosition(), SkywarsGameMap::fromNbt, SkywarsGameMap.FILE_EXTENSION);

        if (map.isEmpty()) throw new IllegalStateException("No Skywars map found");
        return map.get();
    }

    @Override
    public void startGame() {
        super.startGame();
        this.map.placeLootChests();
    }

    @Override
    public void endGame(boolean immediate) {
        this.playerMap.forEach((uuid, stats) -> {
            final Text message;
            final SoundEvent sound;
            final SkywarsTable dbTable = this.getDbTable(uuid);
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

    @Override
    public Multimap<DyeColor, UUID> buildTeams(Set<ServerPlayerEntity> players, @Nullable SpreadRules spreadRules) {
        final Stack<UUID> playerStack = getRandomPlayerStack(players);

        final List<DyeColor> teamColoursList = new ArrayList<>(super.map.getTeamColours());
        Collections.shuffle(teamColoursList);

        return multimapFromCollections(teamColoursList, playerStack);
    }

    @Override
    protected EventQueue<SkywarsGameManager> buildEventQueue() {
        return super.buildEventQueue().addEvent(20 * 60 * 20, manager -> manager.endGame(false), Text.translatable("events.game.end"));
    }

    @Override
    protected Function<UUID, SkywarsTable> dbTableBuilder() {
        return SkywarsTable::new;
    }

    @Override
    public boolean onPlayerDeath(ServerPlayerEntity player, DamageSource source, float amount) {
        super.onPlayerDeath(player, source, amount);

        this.playerMap.get(player.getUuid()).kill();

        final boolean canRespawn = this.canRespawn(player);
        this.broadcastDeath(player, source, !canRespawn);

        if (this.getAlivePlayers().size() < (super.getPlayers().size() > 1 ? 2 : 1)) {
            this.endGame(false);
            return false;
        }

        if (canRespawn) {
            PrescheduledEvents.playCountdown(() -> this.respawnPlayer(player), this, 3, 20, SoundEvents.BLOCK_NOTE_BLOCK_GUITAR.value(), true, player);
        } else {
            player.networkHandler.sendPacket(new TitleS2CPacket(Text.translatable("game.skywars.eliminate")));
        }

        return false;
    }

    @Override
    protected void respawnPlayer(ServerPlayerEntity player) {
        super.respawnPlayer(player);
        player.giveItemStack(new ItemStack(woolItemFromColour(this.getTeam(player.getUuid())), 32));
    }

    @Override
    protected boolean canRespawn(ServerPlayerEntity player) {
        return this.playerMap.get(player.getUuid()).isAlive();
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
    public boolean onChestOpened(ServerPlayerEntity player, BlockPos pos) {
        super.map.getLootChest(pos).ifPresent(chest -> {
            if (chest.open()) {
                this.getDbTable(player).openChest(chest.getTier());
            }
        });

        return true;
    }

    private List<UUID> getAlivePlayers() {
        return this.playerMap.entrySet().stream().filter(entry -> entry.getValue().isAlive()).map(Map.Entry::getKey).toList();
    }
}
