package com.soc.networking.helper;

import com.soc.networking.PacketCodecs;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Objects;

public record BlockLocation(RegistryKey<World> world, BlockPos pos) {
    public static final PacketCodec<RegistryByteBuf, BlockLocation> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.WORLD_KEY, BlockLocation::world,
            PacketCodecs.BLOCK_POS, BlockLocation::pos,
            BlockLocation::new
    );

    public BlockLocation(BlockEntity blockEntity) {
        this(Objects.requireNonNull(blockEntity.getWorld()).getRegistryKey(), blockEntity.getPos());
    }
}
