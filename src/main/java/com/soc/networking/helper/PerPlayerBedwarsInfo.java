package com.soc.networking.helper;

import com.soc.game.manager.bedwars.PlayerStats;
import com.soc.game.manager.bedwars.TeamStats;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.DyeColor;

import java.util.Map;
import java.util.UUID;

public record PerPlayerBedwarsInfo(UUID player, boolean isAlive) {
    public static final PacketCodec<RegistryByteBuf, PerPlayerBedwarsInfo> PACKET_CODEC = PacketCodec.tuple(com.soc.networking.PacketCodecs.UUID, PerPlayerBedwarsInfo::player, PacketCodecs.BOOLEAN, PerPlayerBedwarsInfo::isAlive, PerPlayerBedwarsInfo::new);

    public PerPlayerBedwarsInfo(PlayerStats stats) {
        this(
                stats.getPlayer(),
                stats.isAlive()
        );
    }
}
