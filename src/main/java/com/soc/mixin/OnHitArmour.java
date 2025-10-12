package com.soc.mixin;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class OnHitArmour {
	@Shadow public abstract ItemStack getEquippedStack(EquipmentSlot slot);

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getDamageBlockedAmount(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/damage/DamageSource;F)F", ordinal = 0), method = "damage", cancellable = true)
	private void socwars_onHitArmour(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		for (EquipmentSlot slot : new EquipmentSlot[] {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {

			final ItemStack stack = getEquippedStack(slot);
			final Item item = stack.getItem();
			if (item instanceof com.soc.items.util.OnHitArmour implementer) {
				final boolean allowDamage = implementer.onHit(stack, (LivingEntity)(Object)this);
				if (!allowDamage) cir.setReturnValue(false);
			}
		}
	}
}