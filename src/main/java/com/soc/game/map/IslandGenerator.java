package com.soc.game.map;

import com.soc.resourcedata.IslandGeneratorUpgrade;
import com.soc.resourcedata.ResourceManager;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class IslandGenerator {
    private static final List<IslandGeneratorUpgrade> GENERATOR_UPGRADES = ResourceManager.GAME_DATA.bedwarsData().getGeneratorUpgrades();

    private int tier = 0;

    private final BlockPos pos;
    private final ResourceGenerator ironGenerator;
    private final ResourceGenerator goldGenerator;
    private final ResourceGenerator emeraldGenerator;

    public IslandGenerator(World world, BlockPos pos) {
        this.pos = pos;

        final IslandGeneratorUpgrade baseStats = GENERATOR_UPGRADES.getFirst();
        this.ironGenerator = new ResourceGenerator(new ItemStack(Items.IRON_INGOT, baseStats.ironCount()), world, pos, baseStats.ironTime());
        this.goldGenerator = new ResourceGenerator(new ItemStack(Items.GOLD_INGOT, baseStats.goldCount()), world, pos, baseStats.goldTime());
        this.emeraldGenerator = new ResourceGenerator(new ItemStack(Items.EMERALD, baseStats.emeraldCount()), world, pos, baseStats.emeraldTime());
    }

    public void tick() {
        this.ironGenerator.tick();
        this.goldGenerator.tick();
        this.emeraldGenerator.tick();
    }

    public boolean upgrade() {
        if (GENERATOR_UPGRADES.size() <= this.tier + 1) return false;

        final IslandGeneratorUpgrade stats = GENERATOR_UPGRADES.get(++this.tier);

        this.ironGenerator.setStats(stats.ironTime(), stats.ironCount());
        this.goldGenerator.setStats(stats.goldTime(), stats.goldCount());
        this.emeraldGenerator.setStats(stats.emeraldTime(), stats.emeraldCount());

        return true;
    }

    public BlockPos getPos() {
        return this.pos;
    }
}
