package com.soc.entities.util;

import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

@FunctionalInterface
public interface ExplodeFunction {
    void explode(Entity self, ServerWorld serverWorld, Vec3d pos, float explosionPower, Entity owner);
}
