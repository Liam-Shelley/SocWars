package com.soc.game.map;

import com.google.common.collect.ImmutableSet;
import com.soc.items.components.ModComponents;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.Set;

public class ResourceGenerator {
    protected final World world;
    protected final BlockPos pos;
    protected final ItemStack item;
    protected int generationTime;
    protected int remainingTime;

    public ResourceGenerator(final ItemStack item, final World world, final BlockPos pos, final boolean splitsDrops, final int generationTime) {
        this.world = world;
        this.pos = pos;
        this.item = item.copy();

        this.item.set(ModComponents.RESOURCE_COUNTED, Unit.INSTANCE);
        if (splitsDrops) this.item.set(ModComponents.RESOURCE_SPLIT, Unit.INSTANCE);

        this.generationTime = generationTime;
        this.remainingTime = this.generationTime;
    }

    public static Set<ResourceGenerator> resourceGenerators(final ItemStack item, final World world, final Set<BlockPos> positions, final boolean splitsDrops, final int generationTime) {
        return positions.stream().map(pos -> new ResourceGenerator(item, world, pos, splitsDrops, generationTime)).collect(ImmutableSet.toImmutableSet());
    }

    private void generate() {
        final ItemEntity entity = new ItemEntity(this.world, this.pos.getX() + 0.5d, this.pos.getY() + 1, this.pos.getZ() + 0.5d, this.item.copy());
        this.world.spawnEntity(entity);
        entity.setVelocity(Vec3d.ZERO);
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
    }

    public void setStats(int generationTime, int count) {
        this.generationTime = generationTime;
        this.item.setCount(count);
    }

    public BlockPos getPos() {
        return this.pos;
    }
}
