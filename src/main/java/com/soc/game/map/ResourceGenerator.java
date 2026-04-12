package com.soc.game.map;

import com.soc.items.components.ModComponents;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.Unit;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ResourceGenerator {
    protected static final AtomicInteger GENERATOR_ID_COUNTER = new AtomicInteger();

    protected final World world;
    protected final BlockPos pos;
    protected final Item item;
    protected int count;
    protected final boolean splitsDrops;

    protected int generationTime;
    protected int remainingTime;

    private int maxCount = 16;
    private int currentCount = 0;

    private final int id = GENERATOR_ID_COUNTER.getAndIncrement();

    public ResourceGenerator(Item item, int count, World world, BlockPos pos, boolean splitsDrops, int generationTime) {
        this.world = world;
        this.pos = pos;
        this.item = item;
        this.count = count;
        this.splitsDrops = splitsDrops;

        this.generationTime = generationTime;
        this.remainingTime = this.generationTime;
    }

    private void generate() {
        final int countToSpawn = Math.clamp(this.maxCount - this.currentCount, 0, this.count);

        if (countToSpawn > 0) {
            this.currentCount += countToSpawn;
            final ItemEntity entity = new ItemEntity(this.world, this.pos.getX() + 0.5d, this.pos.getY() + 1, this.pos.getZ() + 0.5d, this.getItemStack(countToSpawn));
            entity.setVelocity(Vec3d.ZERO);
            entity.setNeverDespawn();

            this.world.spawnEntity(entity);
        }
    }

    private ItemStack getItemStack(int countToSpawn) {
        final ItemStack itemStack = new ItemStack(this.item, countToSpawn);

        itemStack.set(ModComponents.RESOURCE_COUNTED, Unit.INSTANCE);
        if (this.splitsDrops) itemStack.set(ModComponents.RESOURCE_SPLIT, Unit.INSTANCE);
        itemStack.set(ModComponents.GENERATOR_REFERENCE, id);

        return itemStack;
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
        this.count = stats.count();
        this.maxCount = stats.maxCount();
    }

    public void setStats(int generationTime, int count, int maxCount) {
        this.generationTime = generationTime;
        this.count = count;
        this.maxCount = maxCount;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public int getId() {
        return this.id;
    }

    public void checkItemCap() {
        if (this.currentCount == 0) return; //Hopefully a worthy optimisation that won't break everything

        this.currentCount = this.world
                .getEntitiesByType(TypeFilter.instanceOf(ItemEntity.class), new Box(this.pos.getX() - 5, this.pos.getY() - 5, this.pos.getZ() - 5, this.pos.getX() + 5, this.pos.getY() + 5, this.pos.getZ() + 5), itemEntity -> itemEntity.getStack().get(ModComponents.RESOURCE_COUNTED) != null && itemEntity.getStack().isOf(this.item))
                .stream()
                .map(itemEntity -> itemEntity.getStack().getCount())
                .reduce(0, Integer::sum);
    }
}
