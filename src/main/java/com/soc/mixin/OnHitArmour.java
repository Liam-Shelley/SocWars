package com.soc.mixin;

import com.soc.itemsrework.components.ArmourHitComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class OnHitArmour extends Entity {
	protected OnHitArmour(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Shadow public abstract ItemStack getEquippedStack(EquipmentSlot slot);

	@SuppressWarnings("ConstantValue")
	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;applyDamage(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/damage/DamageSource;F)V", ordinal = 1), method = "damage", cancellable = true)
	private void socwars_onHitArmour(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		for (EquipmentSlot slot : new EquipmentSlot[] {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
			final ItemStack stack = getEquippedStack(slot);
			final boolean allowDamage = stack.streamAll(ArmourHitComponent.class).map(armourHit -> armourHit.hit(stack, (LivingEntity)(Object)this, this.getWorld(), source)).reduce(true, Boolean::logicalAnd);

			if (!allowDamage) cir.setReturnValue(false);
		}
	}
}