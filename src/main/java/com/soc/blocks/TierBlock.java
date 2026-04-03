package com.soc.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class TierBlock extends HorizontalFacingBlock {
    public static final MapCodec<TierBlock> CODEC = createCodec(settings -> new TierBlock(settings, 0));
    public static final IntProperty TIER = IntProperty.of("tier", 0, 3);

    public TierBlock(Settings settings, int tier) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(TIER, tier).with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(TIER).add(FACING);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (placer == null) return;

        world.setBlockState(pos, state.with(FACING, Direction.getFacing(placer.getRotationVector().getHorizontal())));
    }

    @Override
    protected MapCodec<TierBlock> getCodec() {
        return CODEC;
    }
}
