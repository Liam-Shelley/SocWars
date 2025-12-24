package com.soc.networking.helper;

import com.soc.game.manager.bedwars.TeamStats;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.DyeColor;

import java.util.Map;
import java.util.UUID;

public record PerPlayerBedwarsInfo(UUID player, DyeColor team, boolean hasBed) {
    public static final PacketCodec<RegistryByteBuf, PerPlayerBedwarsInfo> PACKET_CODEC = PacketCodec.tuple(com.soc.networking.PacketCodecs.UUID, PerPlayerBedwarsInfo::player, DyeColor.PACKET_CODEC, PerPlayerBedwarsInfo::team, PacketCodecs.BOOLEAN, PerPlayerBedwarsInfo::hasBed, PerPlayerBedwarsInfo::new);

    public PerPlayerBedwarsInfo(Map.Entry<DyeColor, UUID> entry, TeamStats teamStats) {
        this(entry.getValue(), entry.getKey(), teamStats.hasBed());
    }
}
