package com.soc.entities;

import com.soc.entities.util.ExplodeFunction;
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

import static com.soc.entities.util.ModEntities.BW_FIREBALL;

public class BWFireballEntity extends FireballEntity {
    private final float explosionPower;
    private final ExplodeFunction explodeFunction;

    {
        this.accelerationPower = 0f;
    }

    public BWFireballEntity(EntityType<? extends BWFireballEntity> entityType, World world) {
        super(entityType, world);
        this.explosionPower = 0;
        this.explodeFunction = null;
    }

    public BWFireballEntity(World world, LivingEntity owner, Vec3d velocity, int explosionPower, ExplodeFunction explodeFunction) {
        super(BW_FIREBALL, world);
        this.setVelocity(velocity);
        this.setOwner(owner);

        this.explosionPower = explosionPower;
        this.explodeFunction = explodeFunction;
    }

    public BWFireballEntity(World world, LivingEntity owner, Vec3d velocity, int explosionPower) {
        this(world, owner, velocity, explosionPower, BWFireballEntity::sphereExplode);
    }

    @Override
    public float getDrag() {
        return 1f;
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        if (hitResult.getType() == HitResult.Type.ENTITY && ((EntityHitResult)hitResult).getEntity() == this.getOwner()) return;
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            final Entity owner = LazyEntityReference.resolve(super.owner, serverWorld, Entity.class);
            this.explodeFunction.explode(this, serverWorld, this.getPos(), this.explosionPower, owner);
        }
        this.discard();
    }

    private static void sphereExplode(Entity self, ServerWorld serverWorld, Vec3d pos, float explosionPower, Entity owner) {
        SphereExplosion.explode(serverWorld, pos, explosionPower, 1.25f, 1f, true, owner instanceof LivingEntity ? (LivingEntity)owner : null, null);
    }
}

