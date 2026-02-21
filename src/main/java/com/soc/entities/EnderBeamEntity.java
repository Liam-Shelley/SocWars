package com.soc.entities;

import com.soc.entities.util.ModEntities;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EnderBeamEntity extends EnderPearlEntity {
    public EnderBeamEntity(EntityType<? extends EnderBeamEntity> entityType, World world) {
        super(entityType, world);
    }

    public EnderBeamEntity(World world, @Nullable LivingEntity owner) {
        this(ModEntities.ENDER_BEAM, world);
        this.setOwner(owner);
    }

    @Override
    protected double getGravity() {
        return 0;
    }
}

