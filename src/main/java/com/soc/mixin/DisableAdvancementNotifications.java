package com.soc.mixin;

import net.minecraft.advancement.AdvancementDisplay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AdvancementDisplay.class)
public abstract class DisableAdvancementNotifications {
	//TODO: Config option?
	@Inject(at = @At("HEAD"), method = "shouldShowToast", cancellable = true)
	protected void socwars_shouldNotShowToast(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(false);
	}

	@Inject(at = @At("HEAD"), method = "shouldAnnounceToChat", cancellable = true)
	protected void socwars_shouldNotAnnounceToChat(CallbackInfoReturnable<Boolean> cir) {
		cir.setReturnValue(false);
	}
}