package com.soc.events;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

@FunctionalInterface
public interface OnCraftingTableOpen {
    boolean onOpen(ServerPlayerEntity player, BlockPos pos);
}
