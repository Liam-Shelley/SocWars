package com.soc.blocks;

import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class ManyBedsBlock extends ColourStateBlock {
    private static final VoxelShape VOXEL_SHAPE = VoxelShapes.cuboid(0d, 0d, 0d, 1d, 9d/16d, 1d);

    public ManyBedsBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VOXEL_SHAPE;
    }

    @Override
    public void onEntityLand(BlockView world, Entity entity) {
        if (entity.bypassesLandingEffects()) {
            super.onEntityLand(world, entity);
        } else {
            final Vec3d velocity = entity.getVelocity();
            if (velocity.y < 0d) {
                entity.setVelocity(velocity.x, velocity.y * -0.4f, velocity.z);
            }
        }
    }
}
