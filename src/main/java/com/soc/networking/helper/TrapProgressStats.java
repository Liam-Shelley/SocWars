package com.soc.networking.helper;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record TrapProgressStats(long nextTrapTime, int trapDuration, long nextAbilityTime, int abilityDuration) {
    public static final PacketCodec<RegistryByteBuf, TrapProgressStats> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.LONG, TrapProgressStats::nextTrapTime,
            PacketCodecs.INTEGER, TrapProgressStats::trapDuration,
            PacketCodecs.LONG, TrapProgressStats::nextAbilityTime,
            PacketCodecs.INTEGER, TrapProgressStats::abilityDuration,
            TrapProgressStats::new
    );
}
