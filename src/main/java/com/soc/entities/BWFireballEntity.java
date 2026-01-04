package com.soc.entities;

import com.soc.util.SphereExplosion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LazyEntityReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import static com.soc.entities.util.ModEntities.BW_FIREBALL_TYPE;

public class BWFireballEntity extends FireballEntity {
    private final float explosionPower;

    {
        this.accelerationPower = 0f;
    }

    public BWFireballEntity(EntityType<? extends BWFireballEntity> entityType, World world) {
        super(entityType, world);
        this.explosionPower = 0;
    }

    public BWFireballEntity(World world, LivingEntity owner, Vec3d velocity, int explosionPower) {
        super(BW_FIREBALL_TYPE, world);
        this.setVelocity(velocity);
        this.explosionPower = explosionPower;
        this.setOwner(owner);
    }

    @Override
    public float getDrag() {
        return 1f;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.ENTITY && ((EntityHitResult)hitResult).getEntity() == this.getOwner()) return;
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            final Entity owner = LazyEntityReference.resolve(super.owner, this.getWorld(), Entity.class);
            SphereExplosion.explode(serverWorld, this.getPos(), this.explosionPower, 0.25f, 1f, owner instanceof LivingEntity ? (LivingEntity)owner : null);
            this.discard();
        }
    }
}

