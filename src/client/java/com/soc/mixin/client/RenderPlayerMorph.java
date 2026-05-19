package com.soc.mixin.client;

import com.soc.player.ClientPlayerDataManager;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

import static net.minecraft.client.render.entity.LivingEntityRenderer.getOverlay;

@Mixin(PlayerEntityRenderer.class)
abstract class RenderPlayerMorph extends LivingEntityRendererBaseMixin {
	@Unique
	private BlockRenderManager blockRenderManager;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void socwars_assignBlockRenderManager(EntityRendererFactory.Context ctx, boolean slim, CallbackInfo ci){
		this.blockRenderManager = ctx.getBlockRenderManager();
	}

	@Override
	protected void socwars_livingEntityRender(LivingEntityRenderState livingEntityRenderState, MatrixStack matrices, VertexConsumerProvider vertices, int light, CallbackInfo ci) {
		final Entity thisEntity = Objects.requireNonNull(MinecraftClient.getInstance().world).getEntityById(((PlayerEntityRenderState)livingEntityRenderState).id);
		final BlockState morph = thisEntity == null ? null : ClientPlayerDataManager.getMorph(thisEntity.getUuid());
		if (morph != null) {
			this.renderMorph(livingEntityRenderState, matrices, vertices, light, morph);
			ci.cancel();
		}
	}

	@Unique
	private void renderMorph(LivingEntityRenderState livingEntityRenderState, MatrixStack matrices, VertexConsumerProvider vertices, int light, BlockState morph) {
		matrices.push();
		matrices.translate(-0.5d, 0d, -0.5d);

		this.blockRenderManager.renderBlockAsEntity(morph, matrices, vertices, light, getOverlay(livingEntityRenderState, this.getAnimationCounter(livingEntityRenderState)));

		matrices.pop();
	}
}
