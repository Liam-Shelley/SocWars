package com.soc.mixin;

import com.soc.items.util.ScaledUseDuration;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class UseDurationScale {
	@Shadow public abstract Hand getActiveHand();
	@Shadow public abstract ItemStack getStackInHand(Hand hand);

	@Inject(at = @At("RETURN"), method = "getItemUseTimeLeft", cancellable = true)
	private void socwars_useDurationScale(CallbackInfoReturnable<Integer> cir) {
		final ItemStack stack = getStackInHand(getActiveHand());
		final Item item = stack.getItem();

		if (item instanceof ScaledUseDuration implementer) {
			final int value = cir.getReturnValue();
			final float scale = (implementer.getScale(stack) * 20f - 20f) / stack.getMaxUseTime((LivingEntity)(Object)this) + 1f;
			cir.setReturnValue((int)(value * scale));
		}
	}
}