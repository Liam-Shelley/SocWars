package com.soc.mixin;

import com.soc.entities.BWFireballEntity;
import net.minecraft.entity.projectile.ExplosiveProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExplosiveProjectileEntity.class)
public abstract class ExplosiveProjectileEntityToggleAddParticles {
	@Inject(at = @At("HEAD"), method = "addParticles", cancellable = true)
	private void socwars_deposit(CallbackInfo ci) {
		if ((ExplosiveProjectileEntity)(Object)this instanceof BWFireballEntity entity) {
			if (!entity.shouldSpawnParticles()) ci.cancel();
		}
	}
}