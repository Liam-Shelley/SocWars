package com.soc.blocks.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameMode;

public class SurvivalIntangibleBlock extends Block {
    public SurvivalIntangibleBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getModifiedOutlineShape(state, world, pos, context);
    }

    public static VoxelShape getModifiedOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        if (context instanceof EntityShapeContext entityShapeContext && entityShapeContext.getEntity() instanceof PlayerEntity playerEntity && playerEntity.getGameMode() != GameMode.CREATIVE) {
            return VoxelShapes.empty();
        }

        return VoxelShapes.fullCube();
    }
}
