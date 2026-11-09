package com.soc.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import static com.soc.items.SocWarsItem.getUseScale;

@Mixin(HeldItemRenderer.class)
abstract class BowUseScale {
	@ModifyVariable(method = "renderFirstPersonItem", at = @At(value = "STORE"), index = 15)
	private float socwars_scaleBowUseDuration(float original, @Local(argsOnly = true) ItemStack item) {
		return original / getUseScale(item);
	}
}
