package com.soc.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.soc.items.components.ModItemComponents;
import net.minecraft.entity.ai.goal.BowAttackGoal;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BowAttackGoal.class)
abstract class BowAttackGoalRecognisesCustomBows {
	@WrapOperation(method = "isHoldingBow", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/HostileEntity;isHolding(Lnet/minecraft/item/Item;)Z"))
	private boolean socwars$isHoldingCustomBow(HostileEntity instance, Item item, Operation<Boolean> original) {
		return original.call(instance, item) || instance.isHolding(stack -> stack.contains(ModItemComponents.BOW));
	}
}
