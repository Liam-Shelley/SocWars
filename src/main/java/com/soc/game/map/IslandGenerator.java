package com.soc.game.map;

import com.soc.resourcedata.containers.BedwarsGeneratorDataContainer;
import com.soc.resourcedata.deserialisation.IslandGeneratorUpgrade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.commons.lang3.stream.Streams;

import java.util.List;
import java.util.stream.Stream;

public class IslandGenerator {
    private static final List<IslandGeneratorUpgrade> GENERATOR_UPGRADES = BedwarsGeneratorDataContainer.INSTANCE.getIslandGeneratorUpgrades();

    private int tier = 0;

    private final BlockPos pos;
    private final ResourceGenerator ironGenerator;
    private final ResourceGenerator goldGenerator;
    private final ResourceGenerator emeraldGenerator;

    public IslandGenerator(World world, BlockPos pos) {
        this.pos = pos;

        final IslandGeneratorUpgrade baseStats = GENERATOR_UPGRADES.getFirst();
        this.ironGenerator = new ResourceGenerator(new ItemStack(Items.IRON_INGOT, baseStats.ironCount()), world, pos, true, baseStats.ironTime());
        this.goldGenerator = new ResourceGenerator(new ItemStack(Items.GOLD_INGOT, baseStats.goldCount()), world, pos, true, baseStats.goldTime());
        this.emeraldGenerator = new ResourceGenerator(new ItemStack(Items.EMERALD, baseStats.emeraldCount()), world, pos, true, baseStats.emeraldTime());
    }

    public void tick() {
        this.ironGenerator.tick();
        this.goldGenerator.tick();
        this.emeraldGenerator.tick();
    }

    public boolean upgrade() { //TODO: Link this up for both manual and automatic upgrades
        if (GENERATOR_UPGRADES.size() <= this.tier + 1) return false;

        final IslandGeneratorUpgrade stats = GENERATOR_UPGRADES.get(++this.tier);

        this.ironGenerator.setStats(stats.ironTime(), stats.ironCount(), stats.ironMaxCount());
        this.goldGenerator.setStats(stats.goldTime(), stats.goldCount(), stats.goldMaxCount());
        this.emeraldGenerator.setStats(stats.emeraldTime(), stats.emeraldCount(), stats.emeraldMaxCount());

        return true;
    }

    public Stream<ResourceGenerator> getConsituentGenerators() {
        return Streams.of(this.ironGenerator, this.goldGenerator, this.emeraldGenerator);
    }

    public BlockPos getPos() {
        return this.pos;
    }
}
