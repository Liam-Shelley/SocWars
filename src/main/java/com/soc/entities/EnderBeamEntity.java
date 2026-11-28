package com.soc.entities;

import com.soc.entities.util.ModEntities;
import com.soc.entities.util.ModifyDrag;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EnderBeamEntity extends EnderPearlEntity implements ModifyDrag {
    public EnderBeamEntity(EntityType<? extends EnderBeamEntity> entityType, World world) {
        super(entityType, world);
    }

    public EnderBeamEntity(World world, @Nullable LivingEntity owner) {
        this(ModEntities.ENDER_BEAM, world);
        this.setOwner(owner);
    }

    public EnderBeamEntity(LivingEntity owner) {
        this(ModEntities.ENDER_BEAM, owner.getWorld());
        this.setOwner(owner);
    }

    @Override
    protected double getGravity() {
        return 0d;
    }

    @Override
    public float getDrag() {
        return this.isTouchingWater() ? 0.9f : 1f;
    }
}

