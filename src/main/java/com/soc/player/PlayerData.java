package com.soc.player;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.soc.game.GameKit;
import com.soc.game.manager.GameType;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.soc.lib.SocWarsLib.ifNotNull;

public class PlayerData {
    //Maybe I should just replace this with a normal tuple codec. --> What is now the present me says yes that was a good idea it was much easier thank you.
    public static final PacketCodec<ByteBuf, PlayerData> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.collection(ArrayList::new, PacketCodecs.BOOLEAN), PlayerData::getCollectibles,
            //Not bothering to sync kits to client yet. May do at some point ¯\_(ツ)_/¯
            PlayerData::new
    );

    public static final Codec<PlayerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.list(Codec.BOOL).fieldOf("collectibles").orElse(new ArrayList<>()).forGetter(PlayerData::getCollectibles),
            Codec.unboundedMap(GameType.CODEC, GameKit.CODEC).fieldOf("equipped_kits").orElse(new HashMap<>()).forGetter(playerData -> playerData.equippedKits)
    ).apply(instance, PlayerData::new));

    public static @Nullable PlayerData CLIENT_INSTANCE = null;

    private final List<Boolean> collectibles;
    private final Map<GameType, GameKit> equippedKits;

    public PlayerData(List<Boolean> collectibles, Map<GameType, GameKit> equippedKits) {
        this.collectibles = new ArrayList<>(collectibles);
        this.equippedKits = equippedKits;
    }

    public PlayerData(List<Boolean> collectibles) {
        this(collectibles, new HashMap<>());
    }

    public PlayerData() {
        this(new ArrayList<>(), new HashMap<>());
    }

    public boolean collectCollectible(int id) {
        if (id < 0) return false;

        while (id >= this.collectibles.size()) this.collectibles.add(false);

        return this.collectibles.set(id, true);
    }

    public void resetCollectible(int id) {
        if (id < 0 || id >= this.collectibles.size()) return;

        this.collectibles.set(id, false);
    }

    public boolean hasCollectible(int collectible) {
        return collectible >= 0 && collectible < this.collectibles.size() && this.collectibles.get(collectible);
    }

    public List<Boolean> getCollectibles() {
        return this.collectibles;
    }

    public static boolean hasCollectibleClient(int id) {
        return CLIENT_INSTANCE != null && CLIENT_INSTANCE.hasCollectible(id);
    }

    public void tryApplyKit(GameType gameType, ServerPlayerEntity player) {
        ifNotNull(this.equippedKits.get(gameType), kit -> kit.apply(player));
    }

    public void setKits(GameKit kit, GameType[] gameTypes) {
        for (GameType gameType : gameTypes) {
            this.equippedKits.put(gameType, kit);
        }
    }
}
