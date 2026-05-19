package com.soc.networking;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.storage.NbtReadView;
import net.minecraft.storage.NbtWriteView;
import net.minecraft.util.ErrorReporter;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

public interface PacketCodecs {
    PacketCodec<ByteBuf, UUID> UUID = net.minecraft.network.codec.PacketCodecs.STRING.xmap(java.util.UUID::fromString, java.util.UUID::toString);
    PacketCodec<ByteBuf, RegistryKey<World>> WORLD_KEY = RegistryKey.createPacketCodec(RegistryKeys.WORLD);
    PacketCodec<ByteBuf, BlockPos> BLOCK_POS = net.minecraft.network.codec.PacketCodecs.LONG.xmap(BlockPos::fromLong, BlockPos::asLong);
    PacketCodec<ByteBuf, BlockState> BLOCK_STATE = net.minecraft.network.codec.PacketCodecs.entryOf(Block.STATE_IDS);

//    PacketCodec<ByteBuf, BlockState> BLOCK_STATE = net.minecraft.network.codec.PacketCodecs.NBT_COMPOUND.xmap(nbt -> {
//        return NbtReadView.create(ErrorReporter.EMPTY, null, nbt).read("block_state", BlockState.CODEC).orElse(null);
//    }, blockState -> {
//        final NbtWriteView writeView = NbtWriteView.create(ErrorReporter.EMPTY, null);
//        writeView.put("block_state", BlockState.CODEC, blockState);
//
//        return writeView.getNbt();
//    });
//This is all bad and cursed I don't know why I wrote this and I don't even think it'll work
}
