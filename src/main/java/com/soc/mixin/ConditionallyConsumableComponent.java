package com.soc.mixin;

import com.soc.itemsrework.components.ModItemComponentsReworked;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.soc.lib.SocWarsLib.ifNotNull;

@Mixin(ConsumableComponent.class)
abstract class ConditionallyConsumableComponent {
	@Inject(method = "canConsume", at = @At("HEAD"), cancellable = true)
	private void socwars_conditionallyConsumeComponent(LivingEntity user, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
		ifNotNull(stack.get(ModItemComponentsReworked.CAN_CONDITIONALLY_CONSUME), predicate -> {
			if (!predicate.test(user)) cir.setReturnValue(false);
		});
	}
}
