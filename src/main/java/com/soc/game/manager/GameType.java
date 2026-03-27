package com.soc.game.manager;

import com.soc.game.map.BedwarsGameMap;
import com.soc.game.map.HideAndSeekGameMap;
import com.soc.game.map.SkywarsGameMap;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.Text;
import net.minecraft.util.StringIdentifiable;
import org.apache.commons.lang3.StringUtils;

public enum GameType implements QueueProgress, StringIdentifiable {
    SKYWARS(1, 8, "skywars", SkywarsGameMap.FILE_EXTENSION),
    BEDWARS(1, 16, "bedwars", BedwarsGameMap.FILE_EXTENSION),
    PROP_HUNT(2, 8, "prop_hunt", "phmap"),
    HIDE_AND_SEEK(1, 8, "hide_and_seek", HideAndSeekGameMap.FILE_EXTENSION);

    public static final PacketCodec<RegistryByteBuf, GameType> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, GameType::ordinal, GameType::fromOrdinal);
    private final int minPlayers;
    private final int maxPlayers;
    private final String name;
    private final String fileExtension;

    GameType(int minPlayers, int maxPlayers, String name, String fileExtension) {
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        this.name = name;
        this.fileExtension = fileExtension;
    }

    public GameType fromNatural(String string) {
        return GameType.valueOf(string.replace(' ', '_').toUpperCase());
    }

    public String toNatural() {
        return StringUtils.capitalize(this.toString());
    }

    public int minPlayers() {
        return this.minPlayers;
    }

    public int maxPlayers() {
        return this.maxPlayers;
    }

    @Override
    public float getQueueProgress(int playerCount) {
        if (playerCount < this.minPlayers) return 0;

        float offset = (float) this.maxPlayers / (8 * this.minPlayers);
        float rawProgress = (playerCount - this.minPlayers + offset) / (this.maxPlayers - this.minPlayers + offset);
        return (float) Math.pow(rawProgress, 2.5f);
    }

    public Text getVariantName() {
        return Text.translatable("game_type." + this.toString().toLowerCase());
    }

    public static GameType fromOrdinal(int ordinal) {
        final GameType[] values = GameType.values();
        return values[ordinal < values.length ? ordinal : 0];
    }

    public String getFileExtension() {
        return this.fileExtension;
    }

    @Override
    public String asString() {
        return this.name;
    }
}