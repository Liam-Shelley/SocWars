package com.soc.mixin.client;

import com.soc.blocks.DisplayBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = DisplayBlock.class, remap = false)
public abstract class DisplayBlockOutline {
    @Inject(method = "getOutlineShapeForMixin", at = @At("HEAD"), cancellable = true)
    private void socwars_displayBlockOutline(BlockState state, BlockView world, BlockPos pos, ShapeContext context, CallbackInfoReturnable<VoxelShape> cir) {
        final ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null && !player.isCreative()) {
            cir.setReturnValue(VoxelShapes.empty());
        }
    }
}
