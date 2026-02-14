package com.soc.entities;

import com.soc.entities.util.ModEntities;
import com.soc.util.SphereExplosion;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

public class RedShellEntity extends Entity implements Ownable {
    private Entity target;
    @Nullable private Direction direction = null;
    private boolean overshoot = false;

    private LazyEntityReference<Entity> owner = null;

    public RedShellEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    public RedShellEntity(World world, Vec3d pos, @Nullable Entity owner) {
        this(ModEntities.RED_SHELL, world);
        this.setPosition(pos);
        this.setOwner(owner);
    }

    private void setOwner(@Nullable Entity owner) {
        this.owner = owner == null ? null : new LazyEntityReference<>(owner);
    }

    @Override
    protected void initDataTracker(DataTracker.Builder builder) {}

    @Override
    public boolean damage(ServerWorld world, DamageSource source, float amount) {
        return false;
    }

    @Override
    protected void readCustomData(ReadView view) {
        this.owner = LazyEntityReference.fromData(view, "owner");
    }

    @Override
    protected void writeCustomData(WriteView view) {
        LazyEntityReference.writeData(this.owner, view, "owner");
    }

    @Override
    public void tick() {
        if (this.getWorld().isClient) return;

        this.target = this.getWorld().getClosestPlayer(this.getX(), this.getY(), this.getZ(), 250d, entity -> !entity.isTeammate(this.getOwner()));
        if (this.target == null) return;

        if (this.getWorld().getTime() % 8 == 0 && this.getWorld().random.nextFloat() < 0.75f) {
            this.pickAxis(this.direction == null ? List.of() : List.of(this.direction.getOpposite()), true);
        }

        double maximumMovement = 1d;
        if (this.direction != null) {
            this.setVelocity(this.direction.getDoubleVector().multiply(0.8d));
            this.velocityModified = true;
            this.velocityDirty = true;

            if (this.getWorld().getBlockState(BlockPos.ofFloored(this.getPos().offset(this.direction, 0.2d))).blocksMovement()) {
                this.pickAxis(List.of(this.direction), false);
            } else {
                final double axisDistance = Math.abs(this.getAxisDistance(this.direction.getAxis()));
                if (axisDistance < this.getVelocity().length() && !this.overshoot) {
                    maximumMovement = Math.min(axisDistance, 1d);
                    this.pickAxis(List.of(this.direction), true);
                }
            }
        }

        if (this.getPos().isInRange(this.target.getPos(), 1.5d)) this.explode();

        this.tickBlockCollision();
        this.move(MovementType.SELF, this.getVelocity().multiply(maximumMovement));
    }

    private void explode() {
        SphereExplosion.explode(this.getWorld(), this.getPos(), 3f, 1f, 5f, 2f, true, null, null);
        this.discard();
    }

    private void pickAxis(List<Direction> except, boolean align) {
        this.setVelocity(Vec3d.ZERO);
        this.velocityModified = true;
        this.velocityDirty = true;

        this.overshoot = !align;

        final Predicate<Direction> predicate = align ? direction -> !except.contains(direction) && this.directionIsAligned(direction) : direction -> !except.contains(direction);
        this.direction = Arrays.stream(Direction.values()).filter(predicate).min(Comparator.comparingInt(a -> this.getWorld().random.nextInt())).orElse(null);
    }

    private boolean directionIsAligned(Direction direction) {
        final double axisDistance = getAxisDistance(direction.getAxis());
        return axisDistance < 0d ^ direction.getDirection() == Direction.AxisDirection.POSITIVE;
    }

    private double getAxisDistance(Direction.Axis axis) {
        return this.target.getPos().getComponentAlongAxis(axis) - this.getPos().getComponentAlongAxis(axis);
    }

    @Override
    public @Nullable Entity getOwner() {
        return this.owner == null ? null : this.owner.resolve(this.getWorld(), Entity.class);
    }
}
