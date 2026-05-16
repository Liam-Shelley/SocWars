package com.soc.blocks;

import com.mojang.serialization.MapCodec;
import com.soc.blocks.blockentities.KitBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class KitBlock extends BlockWithEntity {
    public KitBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return createCodec(KitBlock::new);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!(world.getBlockEntity(pos) instanceof KitBlockEntity blockEntity)) {
            return super.onUse(state, world, pos, player, hit);
        }

        if (player.isCreative()) {
            player.openHandledScreen(blockEntity);
        } else {
            this.openKitSelectionScreen(blockEntity);
        }

        return ActionResult.SUCCESS;
    }

    private void openKitSelectionScreen(KitBlockEntity blockEntity) {} //Mixin target for client only

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new KitBlockEntity(pos, state);
    }
}