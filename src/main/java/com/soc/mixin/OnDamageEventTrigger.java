package com.soc.mixin;

import com.soc.events.ModEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DamageTracker.class)
public abstract class OnDamageEventTrigger {
	@Shadow @Final private LivingEntity entity;

	@Inject(at = @At(value = "HEAD"), method = "onDamage", cancellable = true)
	private void socwars_onDamage(DamageSource damageSource, float damage, CallbackInfo ci) {
		if (this.entity instanceof ServerPlayerEntity player) {
			final boolean result = ModEvents.ON_PLAYER_DAMAGE_TAKEN.invoker().onDamage(player, damageSource, damage);
			if (!result) ci.cancel();
		}
	}
}