package com.soc.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.soc.events.ModEvents;
import com.soc.networking.s2c.PlayerDataPayload;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager extends PersistentState {
    public static void initialise() {
        ServerPlayerEvents.JOIN.register(entity -> ServerPlayNetworking.send(entity, new PlayerDataPayload(PlayerDataManager.getPlayerData(entity))));
        ModEvents.ON_COLLECTIBLE_BLOCK_REPLACED.register((id, world) -> getPersistentState(world).playerDataMap.values().forEach(playerData -> playerData.resetCollectible(id)));
    }

    public static final Codec<PlayerDataManager> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(Codec.STRING.xmap(UUID::fromString, UUID::toString), PlayerData.CODEC).fieldOf("player_data_map").forGetter(PlayerDataManager::getPlayerDataMap)
    ).apply(instance, PlayerDataManager::new));

    public static PersistentStateType<PlayerDataManager> STATE_TYPE = new PersistentStateType<>("player_data_manager", PlayerDataManager::new, CODEC, null);

    private final Map<UUID, PlayerData> playerDataMap;

    private PlayerDataManager(Map<UUID, PlayerData> playerDataMap) {
        this.playerDataMap = new HashMap<>(playerDataMap);
    }

    private PlayerDataManager() {
        this.playerDataMap = new HashMap<>();
    }

    public Map<UUID, PlayerData> getPlayerDataMap() { return this.playerDataMap; }

    public static PlayerData getPlayerData(ServerPlayerEntity player) {
        return getPersistentState(player.getWorld()).playerDataMap.computeIfAbsent(player.getUuid(), uuid2 -> new PlayerData());
    }

    public static void setPlayerData(ServerPlayerEntity player, PlayerData playerData) {
        getPersistentState(player.getWorld()).playerDataMap.put(player.getUuid(), playerData);
    }

    public static PlayerDataManager getPersistentState(ServerWorld serverWorld) {
        final PlayerDataManager state = serverWorld.getServer().getOverworld().getPersistentStateManager().getOrCreate(STATE_TYPE);
        state.markDirty();
        return state;
    }

    public static boolean collectDoubloons(PlayerEntity player, int doubloons) {
        final ScoreboardObjective objective = player.getScoreboard().getNullableObjective("Doubloons");
        if (objective == null) return false;

        player.getScoreboard().getOrCreateScore(ScoreHolder.fromProfile(player.getGameProfile()), objective).incrementScore(doubloons);
        return true;
    }
}
