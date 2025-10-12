package com.soc.blocks;

import com.soc.util.DamageTypes;
import com.soc.util.BlockTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ColoredFallingBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCollisionHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ColorCode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import static com.soc.lib.SocWarsLib.damageSource;

public class GalliumBlock extends ColoredFallingBlock {
    public GalliumBlock(ColorCode color, Settings settings) {
        super(color, settings);
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        final BlockPos pos1 = pos.subtract(new Vec3i(0, 1, 0));
        if (world.getBlockState(pos1).isIn(BlockTags.IMMUNE)) {
            world.setBlockState(pos, Blocks.LAVA.getDefaultState());
        } else {
            world.setBlockState(pos1, Blocks.AIR.getDefaultState());
        }

        super.onBlockAdded(state, world, pos, oldState, notify);
    }

    @Override
    protected void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity, EntityCollisionHandler handler) {
        if (entity instanceof final LivingEntity livingEntity && !world.isClient()) {
            final float damage = livingEntity.getArmor() * 0.045f + 0.9f;
            final DamageSource source = damageSource(world, DamageTypes.GALLIUM);
            livingEntity.damage((ServerWorld)world, source, damage);
        }
    }
}
