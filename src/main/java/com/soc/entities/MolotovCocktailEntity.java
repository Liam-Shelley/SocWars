package com.soc.entities;

import com.soc.util.SphereExplosion;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;


public class MolotovCocktailEntity extends HandGrenadeEntity {
    public MolotovCocktailEntity(EntityType<? extends ThrownEntity> type, World world) {
        super(type, world, 0f);
    }

    public MolotovCocktailEntity(EntityType<? extends ThrownEntity> type, World world, Entity owner) {
        super(type, world, 0f, owner);
    }

    @Override
    public void tick() {
        super.tick();

        final World world = this.getWorld();
        final Random random = this.getRandom();
        final Vec3d velocity = this.getVelocity();

        world.addParticleClient(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 0.5d, this.getZ(), velocity.x + random.nextFloat() * 0.25d - 0.125d, velocity.y + 0.2d, velocity.z + random.nextFloat() * 0.25d - 0.125d);
    }

    @Override
    protected void detonate() {
        SphereExplosion.fireExplosion(this.getWorld(), this.getBlockPos(), 6f, 0.25f, this.getOwner());
    }
}
