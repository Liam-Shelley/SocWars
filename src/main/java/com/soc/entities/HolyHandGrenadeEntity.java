package com.soc.entities;

import com.mojang.serialization.Codec;
import com.soc.util.SphereExplosion;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.world.World;

public class HolyHandGrenadeEntity extends ThrownEntity {
    private final float detonationTime;

    private float detonationTimer = -1f;

    public HolyHandGrenadeEntity(EntityType<? extends ThrownEntity> type, World world, float detonationTime) {
        super(type, world);
        this.detonationTime = detonationTime;
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {

    }

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        return false;
    }

    @Override
    protected void readCustomData(ReadView view) {
        this.detonationTimer = view.read("detonation_timer", Codec.FLOAT).orElse(-1f);
    }

    @Override
    protected void writeCustomData(WriteView view) {
        view.put("detonation_timer", Codec.FLOAT, this.detonationTimer);
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        if (entityHitResult.getEntity().isTeammate(this.getOwner())) return;

        super.onEntityHit(entityHitResult);
        this.trigger();
    }

    @Override
    protected void onBlockHit(BlockHitResult blockHitResult) {
        super.onBlockHit(blockHitResult);
        this.trigger();
    }

    private void trigger() {
        if (this.detonationTimer < -0.1f) {
            this.detonationTimer = 0f;
        }
    }

    public float getDetonationTimer() {
        return Math.max(0f, this.detonationTimer);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.detonationTimer > -0.1f) {
            this.detonationTimer += 0.05f;
            if (this.detonationTimer > this.detonationTime) {
                this.detonate();
            }
        }
    }

    private void detonate() {
        SphereExplosion.explode(this.getWorld(), this.getPos(), 8f, 3f, 1f, false, this.getOwner(), null);
        this.discard();
    }
}
