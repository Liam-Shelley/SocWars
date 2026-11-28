package com.soc.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.soc.items.components.ModItemComponents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.soc.items.SocWarsItem.getUseScale;

@Mixin(HeldItemRenderer.class)
abstract class HeldItemRendererMixin {
	@Shadow
	@Final
	private MinecraftClient client;

	@ModifyVariable(method = "renderFirstPersonItem", at = @At(value = "STORE"), index = 15)
	private float socwars$scaleBowUseDuration(float original, @Local(argsOnly = true) ItemStack item) {
		return original / getUseScale(item);
	}

	@WrapOperation(method = "renderFirstPersonItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z", ordinal = 0))
	private boolean socwars$noHeldItemRendering(ItemStack instance, Operation<Boolean> original) {
		return original.call(instance) || instance.contains(ModItemComponents.NO_HELD_ITEM_RENDERING);
	}

	@Inject(method = "renderArm", at = @At(value = "JUMP", opcode = Opcodes.IF_ACMPNE), cancellable = true)
	private void socwars$renderBothArms(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Arm arm, CallbackInfo ci, @Local PlayerEntityRenderer playerEntityRenderer, @Local Identifier identifier) {
		final PlayerEntity player = this.client.player;
		assert player != null;
		if (player.getStackInHand(Hand.MAIN_HAND).contains(ModItemComponents.RENDER_BOTH_HANDS) || player.getStackInHand(Hand.OFF_HAND).contains(ModItemComponents.RENDER_BOTH_HANDS)) {
			playerEntityRenderer.renderRightArm(matrices, vertexConsumers, light, identifier, this.client.player.isPartVisible(PlayerModelPart.RIGHT_SLEEVE));
			playerEntityRenderer.renderLeftArm(matrices, vertexConsumers, light, identifier, this.client.player.isPartVisible(PlayerModelPart.LEFT_SLEEVE));

			matrices.pop();
			ci.cancel();
		}
	}
}
