package com.soc.networking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;

import java.util.UUID;

public interface PacketCodecs {
    PacketCodec<RegistryByteBuf, UUID> UUID = PacketCodec.tuple(net.minecraft.network.codec.PacketCodecs.STRING, java.util.UUID::toString, java.util.UUID::fromString);
}
