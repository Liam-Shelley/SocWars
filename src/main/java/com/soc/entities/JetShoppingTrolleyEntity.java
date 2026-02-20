package com.soc.entities;

import com.soc.entities.util.AllowsDismount;
import com.soc.entities.util.ModEntities;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.world.World;

public class JetShoppingTrolleyEntity extends Entity implements AllowsDismount {
    public JetShoppingTrolleyEntity(EntityType<JetShoppingTrolleyEntity> entityType, World world) {
        super(entityType, world);
    }

    public JetShoppingTrolleyEntity(Entity user) {
        this(ModEntities.JET_SHOPPING_TROLLEY, user.getWorld());
        this.setPosition(user.getPos());
        this.setAngles(user.getYaw(), user.getPitch());
        user.startRiding(this);


        this.setVelocity(this.getRotationVector());
    }

    @Override
    public void tick() {
        super.tick();
        this.move(MovementType.SELF, this.getVelocity());
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

    }

    @Override
    protected void writeCustomData(WriteView view) {

    }

    @Override
    public boolean allowsDismount() {
        return false;
    }
}
