package com.soc.events;

import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

@FunctionalInterface
public interface OnBlockPlaced {
    ActionResult onItemPickup(ServerPlayerEntity player, BlockPos pos, ItemUsageContext context);
}
