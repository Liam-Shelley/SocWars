package com.soc.game.map;

import net.minecraft.util.math.BlockPos;

public interface SubtractPos<T> {
    T subtractPos(BlockPos pos);
}
