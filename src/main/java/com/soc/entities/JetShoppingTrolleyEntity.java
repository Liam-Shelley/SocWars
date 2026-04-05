package com.soc.entities;

import com.soc.entities.util.AllowsDismount;
import com.soc.entities.util.ModEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import static com.soc.lib.SocWarsLib.ifNotNull;
import static com.soc.lib.SocWarsLib.randomCentredVec3d;

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

        final World world = this.getWorld();
        final Random random = world.random;

        if (!world.isClient && world.getTime() % 10 == 0 && random.nextFloat() < 0.4f) {
            this.setAngles(random.nextFloat() * 360f, random.nextFloat() * 120f - 60f);

            this.setVelocity(this.getRotationVector());
            this.velocityDirty = true;
            this.velocityModified = true;
        }

        final float yawDeg = this.getYaw() * 0.017453292f;
        final Vec3d centreEnginePosition = this.getPos().add(1.3d * Math.sin(yawDeg), 0.9d, -1.3d * Math.cos(yawDeg));

        for (float f : new float[] {-90f, 90f}) {
            final float xOffset = 0.4f * (float)Math.sin(yawDeg + f);
            final float zOffset = -0.4f * (float)Math.cos(yawDeg + f);

            world.addParticleClient(ParticleTypes.LARGE_SMOKE, centreEnginePosition.x + xOffset, centreEnginePosition.y, centreEnginePosition.z + zOffset, -0.4 * this.getVelocity().x, -0.4 * this.getVelocity().y, -0.4 * this.getVelocity().z);
        }


        if (this.age >= 7 * 20) {
            this.discard();
        }
    }

    @Override
    protected void onBlockCollision(BlockState state) {
        this.getWorld().getServer().getPlayerManager().broadcast(Text.of("bonk"), false);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {}

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        return false;
    }

    @Override
    protected void readCustomData(ReadView view) {}

    @Override
    protected void writeCustomData(WriteView view) {}

    @Override
    public boolean allowsDismount() {
        return false;
    }
}
