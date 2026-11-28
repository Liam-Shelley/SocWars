package com.soc.mixin.client;

import com.soc.game.FreecamManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import net.xolt.freecam.Freecam;
import net.xolt.freecam.tripod.TripodSlot;
import net.xolt.freecam.util.FreeCamera;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Freecam.class, remap = false)
abstract class FreecamMixin {
	@Unique
	private static boolean socwars$rememberedSneak = false;

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

	@Inject(method = "onEnable", at = @At("HEAD"))
	private static void socwars$rememberSneak(CallbackInfo ci) {
		socwars$rememberedSneak = MinecraftClient.getInstance().options.sneakKey.isPressed();
		MinecraftClient.getInstance().options.sneakKey.setPressed(false);
	}


	@Inject(method = "onDisable", at = @At("TAIL"))
	private static void socwars$restoreSneak(CallbackInfo ci) {
		final KeyBinding sneakKey = MinecraftClient.getInstance().options.sneakKey;
		sneakKey.setPressed(sneakKey.isPressed() || socwars$rememberedSneak);
	}
}
