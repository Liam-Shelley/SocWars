package com.soc.game.manager;

import com.google.common.collect.Multimap;
import com.soc.database.stats.HideAndSeekTable;
import com.soc.game.map.AbstractGameMap;
import com.soc.game.map.HideAndSeekGameMap;
import com.soc.game.map.SkywarsGameMap;
import com.soc.game.map.SpreadRules;
import com.soc.lib.Events;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.soc.game.map.AbstractGameMap.getRandomPlayerStack;
import static com.soc.lib.SocWarsLib.*;

public class HideAndSeekGameManager extends AbstractGameManager<HideAndSeekGameMap, HideAndSeekTable, HideAndSeekGameManager> {
    private final Map<ServerPlayerEntity, PlayerStats> playerMap;

    private static class PlayerStats {
        public boolean isAlive() {
            return true;
        }

        public void find() {
        }
    }

    protected HideAndSeekGameManager(
            ServerWorld world,
            Set<ServerPlayerEntity> players,
            @Nullable SpreadRules spreadRules,
            int gameId
    ) {
        super(world, players, spreadRules, gameId);
        this.playerMap = players.stream().collect(Collectors.toMap(Function.identity(), key -> new PlayerStats()));
    }

    @Override
    protected HideAndSeekGameMap buildMap() {
        final Optional<HideAndSeekGameMap> map = AbstractGameMap.loadRandomMap(super.world, super.generateCentrePosition(), HideAndSeekGameMap::fromNbt, SkywarsGameMap.FILE_EXTENSION);

        if (map.isEmpty()) throw new IllegalStateException("No Hide and Seek map found");
        return map.get();
    }

    @Override
    public void startGame() {
        super.startGame();
    }

    @Override
    public void endGame(boolean immediate) {
        this.playerMap.forEach((player, stats) -> {
            final Text message;
            final SoundEvent sound;
            final HideAndSeekTable dbTable = this.getDbTable(player);
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
    protected Function<UUID, HideAndSeekTable> dbTableBuilder() {
        return HideAndSeekTable::new;
    }

    @Override
    protected void respawnPlayer(ServerPlayerEntity player) {
        super.respawnPlayer(player);
        player.giveItemStack(new ItemStack(woolItemFromColour(this.getTeam(player.getUuid())), 16));
    }

    @Override
    protected boolean canRespawn(ServerPlayerEntity player) {
        return this.playerMap.get(player).isAlive();
    }

    /*
    @Override
    protected void trackDeathStats(ServerPlayerEntity player, DamageSource source) {
        final HideAndSeekTable targetTable = this.getDbTable(player);

        targetTable.grantFound();
        getPlayerAttacker(player).ifPresent(killer -> {
            final HideAndSeekTable killerTable = this.getDbTable(killer);
            if (killerTable != null) killerTable.grantFind();
        });
    }
     */

    private List<ServerPlayerEntity> getAlivePlayers() {
        return this.playerMap.entrySet().stream().filter(entry -> entry.getValue().isAlive()).map(Map.Entry::getKey).toList();
    }
}
