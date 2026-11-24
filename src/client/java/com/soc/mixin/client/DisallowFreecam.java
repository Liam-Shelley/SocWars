package com.soc.mixin.client;

import com.soc.game.FreecamManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.tripod.TripodSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Freecam.class, remap = false)
abstract class DisallowFreecam {
	@Inject(method = "toggle", at = @At("HEAD"), cancellable = true)
	private static void socwars$disallowFreecamToggle(CallbackInfo ci) {
		if (FreecamManager.preventFreecam()) {
			MinecraftClient.getInstance().player.sendMessage(Text.translatable("message.freecam.disallowed"), false);
			ci.cancel();
		}
	}

	@Inject(method = "toggleTripod", at = @At("HEAD"), cancellable = true)
	private static void socwars$disallowFreecamTripodToggle(TripodSlot tripod, CallbackInfo ci) {
		if (FreecamManager.preventFreecam()) {
			MinecraftClient.getInstance().player.sendMessage(Text.translatable("message.freecam.disallowed"), false);
			ci.cancel();
		}
	}
}
