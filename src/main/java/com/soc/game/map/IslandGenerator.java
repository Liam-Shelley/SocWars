package com.soc.game.map;

import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class IslandGenerator {
    private int tier;

    private final ResourceGenerator ironGenerator;
    private final ResourceGenerator goldGenerator;
    private final ResourceGenerator emeraldGenerator;

    public IslandGenerator(World world, BlockPos pos) {
        this.ironGenerator = new ResourceGenerator(Items.IRON_INGOT.getDefaultStack().copyWithCount(4), world, pos, 2 * 20);
        this.goldGenerator = new ResourceGenerator(Items.GOLD_INGOT.getDefaultStack(), world, pos, 3 * 20);
        this.emeraldGenerator = new ResourceGenerator(Items.EMERALD.getDefaultStack(), world, pos, 0);
    }
}
