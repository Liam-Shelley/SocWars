package com.soc.networking.helper;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.util.DyeColor;

import java.util.Map;
import java.util.UUID;

public record TeamPlayerPair(DyeColor team, UUID player) {
    public static final PacketCodec<RegistryByteBuf, TeamPlayerPair> PACKET_CODEC = PacketCodec.tuple(DyeColor.PACKET_CODEC, TeamPlayerPair::team, com.soc.networking.PacketCodecs.UUID, TeamPlayerPair::player, TeamPlayerPair::new);

    public TeamPlayerPair(Map.Entry<DyeColor, ? extends PlayerEntity> entry) {
        this(entry.getKey(), entry.getValue().getUuid());
    }
}
