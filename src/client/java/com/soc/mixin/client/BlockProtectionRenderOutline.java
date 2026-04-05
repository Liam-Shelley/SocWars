package com.soc.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.soc.game.BlockProtectionManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexRendering;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WorldRenderer.class)
abstract class BlockProtectionRenderOutline {
    @Redirect(method = "drawBlockOutline", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/VertexRendering;drawOutline(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumer;Lnet/minecraft/util/shape/VoxelShape;DDDI)V"))
    void socwars_drawBlockProtectionOutline(MatrixStack matrices, VertexConsumer vertexConsumers, VoxelShape shape, double offsetX, double offsetY, double offsetZ, int color, @Local(argsOnly = true) BlockPos pos) {
        final HitResult hitResult = MinecraftClient.getInstance().crosshairTarget;

        if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
            int thisBlock = BlockProtectionManager.INSTANCE.isBlockProtected(pos) ? 1 : 0;
            int facingBlock = BlockProtectionManager.INSTANCE.isBlockProtected(pos.offset(((BlockHitResult)hitResult).getSide())) ? 2 : 0;

            switch(thisBlock + facingBlock) {
                case 1 -> color = 0xa0e06000;
                case 2 -> color = 0xa0e0c000;
                case 3 -> color = 0xa0e00000;
            }
        }

        VertexRendering.drawOutline(matrices, vertexConsumers, shape, offsetX, offsetY, offsetZ, color);
    }
}
