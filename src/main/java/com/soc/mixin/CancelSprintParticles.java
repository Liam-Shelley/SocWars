package com.soc.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.Entity;

import java.util.UUID;

@Mixin(Entity.class)
public abstract class CancelSprintParticles {
	@Shadow public abstract UUID getUuid();

	@Shadow public abstract boolean isInvisible();

	@Inject(at = @At("HEAD"), method = "spawnSprintingParticles", cancellable = true)
	private void socwars_cancelSprintParticles(CallbackInfo ci) {
		if (this.isInvisible()) ci.cancel();
	}
}