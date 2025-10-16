package com.soc.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.world.World;

public class EnderBeamEntity extends EnderPearlEntity {

    public EnderBeamEntity(EntityType<? extends EnderBeamEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected double getGravity() {
        return 0;
    }
}

