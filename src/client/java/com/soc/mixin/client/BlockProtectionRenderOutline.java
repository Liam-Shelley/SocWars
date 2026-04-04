package com.soc.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.soc.game.BlockProtectionManager;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldRenderer.class)
abstract class BlockProtectionRenderOutline {
    @Redirect(method = "drawBlockOutline", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexRendering;drawOutline(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/util/shape/VoxelShape;DDDI)V"))
    void socwars_drawBlockProtectionOutline(MatrixStack matrices, VertexConsumer vertexConsumers, VoxelShape shape, double offsetX, double offsetY, double offsetZ, int color, @Local(argsOnly = true) BlockPos pos) {
        VertexRendering.drawOutline(matrices, vertexConsumers, shape, offsetX, offsetY, offsetZ, BlockProtectionManager.INSTANCE.getBlockOutlineColour(pos, color));
    }
}
