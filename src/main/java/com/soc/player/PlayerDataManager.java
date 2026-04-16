package com.soc.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.soc.networking.s2c.PlayerDataPayload;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.ScoreHolder;
import net.minecraft.scoreboard.ScoreboardObjective;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {
    public static void initialise() {
        ServerPlayerEvents.JOIN.register(entity -> ServerPlayNetworking.send(entity, new PlayerDataPayload(PlayerDataManager.getPlayerData(entity.getUuid()))));
    }

    public static final Codec<PlayerDataManager> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(Codec.STRING.xmap(UUID::fromString, UUID::toString), PlayerData.CODEC).fieldOf("map").forGetter(PlayerDataManager::getPlayerDataMap)
    ).apply(instance, PlayerDataManager::new));

    public static final PlayerDataManager INSTANCE = new PlayerDataManager();

    private final Map<UUID, PlayerData> playerDataMap;

    private PlayerDataManager(Map<UUID, PlayerData> playerDataMap) {
        this.playerDataMap = playerDataMap;
    }

    private PlayerDataManager() {
        this.playerDataMap = new HashMap<>();
    }

    public Map<UUID, PlayerData> getPlayerDataMap() { return playerDataMap; }

    public static PlayerData getPlayerData(UUID uuid) {
        return INSTANCE.playerDataMap.computeIfAbsent(uuid, uuid2 -> new PlayerData());
    }
    public static PlayerData getPlayerData(PlayerEntity player) {
        return getPlayerData(player.getUuid());
    }

    public void setPlayerData(UUID uuid, PlayerData playerData) {
        this.playerDataMap.put(uuid, playerData);
    }

    public void setPlayerData(PlayerEntity player, PlayerData playerData) {
        setPlayerData(player.getUuid(), playerData);
    }

    public static boolean collectDoubloons(PlayerEntity player, int doubloons) {
        final ScoreboardObjective objective = player.getScoreboard().getNullableObjective("Doubloons");
        if (objective == null) return false;

        player.getScoreboard().getOrCreateScore(ScoreHolder.fromProfile(player.getGameProfile()), objective).incrementScore(doubloons);
        return true;
    }
}
