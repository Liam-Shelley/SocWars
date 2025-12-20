package com.soc.events;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

@FunctionalInterface
public interface OnBedBroken {
    boolean onBedBreak(ServerPlayerEntity player, BlockPos pos);
}