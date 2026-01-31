package com.soc.items.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;

@FunctionalInterface
public interface SpawnThrowableItemFunction {
    void spawn(ServerWorld serverWorld, PlayerEntity user);
}
