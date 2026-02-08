package com.soc.entities;

import com.soc.entities.util.ExplodeFunction;
import com.soc.lib.Events;
import com.soc.util.SphereExplosion;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
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
        if (this.getWorld() instanceof ServerWorld world) {
            final Entity owner = LazyEntityReference.resolve(super.owner, world, Entity.class);
            this.explodeFunction.explode(world, this.getPos(), this.explosionPower, owner);
        }
        this.discard();
    }

    public static void fireballExplosion(ServerWorld world, Vec3d pos, float explosionPower, Entity owner) {
        SphereExplosion.explode(world, pos, explosionPower, 1.1f, 1f, true, owner, null);
        SphereExplosion.fireExplosion(world, BlockPos.ofFloored(pos), explosionPower, 0.2f);
    }

    public static void snailExplosion(ServerWorld world, Vec3d pos, float explosionPower, Entity owner) {
        world.createExplosion(owner, damageSource(world, DamageTypes.EXPLOSION, owner), new ExplosionBehavior() {
            @Override
            public float calculateDamage(Explosion explosion, Entity entity, float amount) {
                return super.calculateDamage(explosion, entity, amount) * 0.09f;
            }
        }, pos.x, pos.y, pos.z, explosionPower, true, World.ExplosionSourceType.BLOCK);
    }

    public static void waterballExplosion(ServerWorld world, Vec3d pos, float explosionPower, Entity owner) {
        world.setBlockState(BlockPos.ofFloored(pos), Blocks.WATER.getDefaultState());
    }

    public static void lightningOrbExplosion(ServerWorld world, Vec3d pos, float explosionPower, Entity owner) {
        final Events events = Events.getInstance();
        for (int i = 0; i < 20; i++) {
            final Entity entity = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
            entity.setPosition(pos);
            events.scheduleEvent(() -> world.spawnEntity(entity), i * 3);
        }
    }

    @Override
    public boolean isOnFire() {
        return this.explosionPower > 0f;
    }

    public boolean shouldSpawnParticles() {
        return this.explosionPower > 0f;
    }
}

