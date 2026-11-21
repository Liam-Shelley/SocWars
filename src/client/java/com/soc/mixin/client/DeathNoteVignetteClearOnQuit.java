package com.soc.mixin.client;

import com.soc.gui.hud.DeathNoteVignette;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DeathScreen.class)
abstract class DeathNoteVignetteClearOnQuit {
	@Inject(method = "quitLevel", at = @At("HEAD"))
	private void socwars$clearDeathNoteVignetteOnRespawn(CallbackInfo ci) {
		DeathNoteVignette.clear();
	}
}
