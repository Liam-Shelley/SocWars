package com.soc.mixin.client;

import com.soc.items.components.ModItemComponents;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.soc.lib.SocWarsLib.ifNotNull;

@Mixin(PlayerEntityRenderer.class)
abstract class ApplyDefaultArmPoseComponent {
	@Inject(method = "getArmPose(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/util/Hand;)Lnet/minecraft/client/render/entity/model/BipedEntityModel$ArmPose;", at = @At("HEAD"), cancellable = true)
	private static void socwars$getArmPose(PlayerEntity player, ItemStack stack, Hand hand, CallbackInfoReturnable<BipedEntityModel.ArmPose> cir) {
		ifNotNull(stack.get(ModItemComponents.DEFAULT_ARM_POSE), armPose -> cir.setReturnValue(BipedEntityModel.ArmPose.values()[armPose.ordinal()]));
	}
}
