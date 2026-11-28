package com.soc.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.soc.items.components.ModItemComponents;
import net.minecraft.client.render.entity.state.ArmedEntityRenderState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ArmedEntityRenderState.class)
abstract class NoHeldItemRendering {
	@WrapOperation(method = "updateRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getStackInArm(Lnet/minecraft/util/Arm;)Lnet/minecraft/item/ItemStack;"))
	private static ItemStack socwars$ignoreTheseHands(LivingEntity instance, Arm arm, Operation<ItemStack> original) {
		final ItemStack originalStack = original.call(instance, arm);
		return originalStack.contains(ModItemComponents.NO_HELD_ITEM_RENDERING) ? ItemStack.EMPTY : originalStack;
	}
}
