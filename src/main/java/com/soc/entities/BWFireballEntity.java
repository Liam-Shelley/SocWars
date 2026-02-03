package com.soc.entities;

import com.soc.entities.util.ExplodeFunction;
import com.soc.util.SphereExplosion;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LazyEntityReference;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.world.explosion.ExplosionBehavior;

import static com.soc.lib.SocWarsLib.damageSource;

public class BWFireballEntity extends FireballEntity {
    private final float explosionPower;
    private final ExplodeFunction explodeFunction;

    {
        this.accelerationPower = 0f;
    }

    public BWFireballEntity(EntityType<BWFireballEntity> type, World world, float explosionPower, ExplodeFunction explodeFunction) {
        super(type, world);

        this.explosionPower = explosionPower;
        this.explodeFunction = explodeFunction;
    }

    public BWFireballEntity(EntityType<BWFireballEntity> type, World world, LivingEntity owner, Vec3d velocity, float explosionPower, ExplodeFunction explodeFunction) {
        this(type, world, explosionPower, explodeFunction);
        this.setVelocity(velocity);
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
            final Entity owner = LazyEntityReference.resolve(super.owner, serverWorld, Entity.class);
            this.explodeFunction.explode(this, serverWorld, this.getPos(), this.explosionPower, owner);
        }
        this.discard();
    }

    public static void fireballExplosion(Entity self, ServerWorld serverWorld, Vec3d pos, float explosionPower, Entity owner) {
        SphereExplosion.explode(serverWorld, pos, explosionPower, 1.1f, 1f, true, owner, null);
        SphereExplosion.fireExplosion(serverWorld, BlockPos.ofFloored(pos), explosionPower, 0.2f);
    }

    public static void snailExplosion(Entity self, ServerWorld serverWorld, Vec3d pos, float explosionPower, Entity owner) {
        serverWorld.createExplosion(self, damageSource(serverWorld, DamageTypes.EXPLOSION, owner), new ExplosionBehavior() {
            @Override
            public float calculateDamage(Explosion explosion, Entity entity, float amount) {
                return super.calculateDamage(explosion, entity, amount) * 0.09f;
            }
        }, pos.x, pos.y, pos.z, explosionPower, true, World.ExplosionSourceType.BLOCK);
    }

    public static void waterballExplosion(Entity self, ServerWorld serverWorld, Vec3d pos, float explosionPower, Entity owner) {
        serverWorld.setBlockState(BlockPos.ofFloored(pos), Blocks.WATER.getDefaultState());
    }

    @Override
    public boolean isOnFire() {
        return this.explosionPower > 0f;
    }

    public boolean shouldSpawnParticles() {
        return this.explosionPower > 0f;
    }
}

