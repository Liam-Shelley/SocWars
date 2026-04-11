package com.soc.game.map;

import com.soc.items.components.ModComponents;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ResourceGenerator {
    protected static final AtomicInteger GENERATOR_ID_COUNTER = new AtomicInteger();

    protected final World world;
    protected final BlockPos pos;
    protected final ItemStack item;

    protected int generationTime;
    protected int remainingTime;

    private int maxCount = 16;
    private int currentCount = 0;

    private final int id = GENERATOR_ID_COUNTER.getAndIncrement();

    public ResourceGenerator(final ItemStack item, final World world, final BlockPos pos, final boolean splitsDrops, final int generationTime) {
        this.world = world;
        this.pos = pos;
        this.item = item.copy();

        this.item.set(ModComponents.RESOURCE_COUNTED, Unit.INSTANCE);
        if (splitsDrops) this.item.set(ModComponents.RESOURCE_SPLIT, Unit.INSTANCE);
        this.item.set(ModComponents.GENERATOR_REFERENCE, id);

        this.generationTime = generationTime;
        this.remainingTime = this.generationTime;
    }

    private void generate() {
        final int countToSpawn = Math.clamp(this.maxCount - this.currentCount, 0, this.item.getCount());

        if (countToSpawn > 0) {
            this.currentCount += this.item.getCount();
            final ItemEntity entity = new ItemEntity(this.world, this.pos.getX() + 0.5d, this.pos.getY() + 1, this.pos.getZ() + 0.5d, this.item.copyWithCount(countToSpawn));
            entity.setVelocity(Vec3d.ZERO);
            entity.setNeverDespawn();

            this.world.spawnEntity(entity);
        }
    }

    public void tick() {
        if (this.remainingTime > 0) {
            this.remainingTime--;
            return;
        }

        this.remainingTime = this.generationTime;

        if (this.generationTime > 0) this.generate(); //Makes sure that generationTime > 0 so that a gen with generationTime = 0 doesn't constantly spawn items
    }

    public void setStats(GeneratorStats stats) {
        this.generationTime = stats.generationTime();
        this.item.setCount(stats.count());
        this.maxCount = stats.maxCount();
    }

    public void setStats(int generationTime, int count, int maxCount) {
        this.generationTime = generationTime;
        this.item.setCount(count);
        this.maxCount = maxCount;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public int getId() {
        return this.id;
    }

    public void checkItemCap() {
        this.currentCount = this.world
                .getEntitiesByType(TypeFilter.instanceOf(ItemEntity.class), new Box(this.pos.getX() - 5, this.pos.getY() - 5, this.pos.getZ() - 5, this.pos.getX() + 5, this.pos.getY() + 5, this.pos.getZ() + 5), itemEntity -> itemEntity.getStack().get(ModComponents.RESOURCE_COUNTED) != null && itemEntity.getStack().isOf(this.item.getItem()))
                .stream()
                .map(itemEntity -> itemEntity.getStack().getCount())
                .reduce(0, Integer::sum);
    }
}
