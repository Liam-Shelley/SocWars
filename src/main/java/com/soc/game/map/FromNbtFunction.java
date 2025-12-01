package com.soc.game.map;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public interface FromNbtFunction<T extends AbstractGameMap> {
     Optional<T> fromNbt(NbtCompound compound, ServerWorld world, BlockPos centrePos);
}
