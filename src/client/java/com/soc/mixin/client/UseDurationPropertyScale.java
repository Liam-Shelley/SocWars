package com.soc.mixin.client;

import net.minecraft.client.render.item.property.numeric.UseDurationProperty;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.soc.items.SocWarsItem.getUseScale;

@Mixin(UseDurationProperty.class)
abstract class UseDurationPropertyScale {
	@Inject(method = "getValue", at = @At("RETURN"), cancellable = true)
	private void socwars_scaleUseDurationProperty(ItemStack stack, ClientWorld world, LivingEntity holder, int seed, CallbackInfoReturnable<Float> cir) {
		cir.setReturnValue(cir.getReturnValueF() / getUseScale(stack));
	}
}
