package com.soc.mixin;

import net.minecraft.block.BlockSetType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockSetType.class)
abstract class MetalGloveAllowOpen { //TODO: This needs to inject at the call sites not here
	@Inject(method = "canOpenByHand", at = @At("HEAD"), cancellable = true)
	private void a(CallbackInfoReturnable<Boolean> cir) {

	}
}
