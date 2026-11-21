package com.soc.mixin.client;

import com.soc.gui.hud.DeathNoteVignette;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
abstract class DeathNoteVignetteClearOnRespawn {
	@Inject(method = "requestRespawn", at = @At("HEAD"))
	private void socwars$clearDeathNoteVignetteOnRespawn(CallbackInfo ci) {
		DeathNoteVignette.clear();
	}
}
