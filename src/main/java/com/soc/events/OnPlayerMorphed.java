package com.soc.events;

import net.minecraft.block.BlockState;
import net.minecraft.server.network.ServerPlayerEntity;

@FunctionalInterface
public interface OnPlayerMorphed {
    boolean onPlayerMorphed(ServerPlayerEntity player, BlockState morph);
}
