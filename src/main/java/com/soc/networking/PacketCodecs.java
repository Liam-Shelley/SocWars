package com.soc.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public interface PacketCodecs {
    PacketCodec<RegistryByteBuf, UUID> UUID = PacketCodec.tuple(net.minecraft.network.codec.PacketCodecs.STRING, java.util.UUID::toString, java.util.UUID::fromString);
    PacketCodec<ByteBuf, RegistryKey<World>> WORLD_KEY = RegistryKey.createPacketCodec(RegistryKeys.WORLD);
    PacketCodec<ByteBuf, BlockPos> BLOCK_POS = net.minecraft.network.codec.PacketCodecs.LONG.xmap(BlockPos::fromLong, BlockPos::asLong);
}
