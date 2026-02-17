package com.soc.entities;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PocketSandEntity extends ThrownEntity {
    public PocketSandEntity(EntityType<PocketSandEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if (entityHitResult.getEntity() instanceof LivingEntity livingEntity) {
            livingEntity.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 2 * 20, 0, false, false));
        }
    }

    @Override
    public void tick() {
        super.tick();

        if (this.age > 5) {
            final Vec3d velocity = this.getVelocity();
            this.getWorld().addParticleClient(ParticleTypes.WHITE_SMOKE, this.getX(), this.getY(), this.getZ(), velocity.x, velocity.y, velocity.z);
            this.discard();
        }
    }
}
