package com.soc.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.soc.items.components.BowComponent;
import com.soc.items.components.ModItemComponents;
import net.minecraft.entity.mob.AbstractSkeletonEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(AbstractSkeletonEntity.class)
abstract class SkeletonsUseCustomBows {
	@WrapOperation(method = "updateAttackType", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
	private boolean socwars$checkIfItemIsCustomBow(ItemStack instance, Item item, Operation<Boolean> original) {
		return original.call(instance, item) || instance.contains(ModItemComponents.BOW);
	}

	@WrapOperation(method = "shootAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/AbstractSkeletonEntity;createArrowProjectile(Lnet/minecraft/item/ItemStack;FLnet/minecraft/item/ItemStack;)Lnet/minecraft/entity/projectile/PersistentProjectileEntity;"))
	private PersistentProjectileEntity socwars$createCorrectProjectile(AbstractSkeletonEntity instance, ItemStack arrow, float damageModifier, ItemStack shotFrom, Operation<PersistentProjectileEntity> original) {
		if (shotFrom.isOf(Items.BOW)) {
			return original.call(instance, arrow, damageModifier, shotFrom);
		} else {
			return BowComponent.createArrowEntity((ServerWorld)instance.getWorld(), instance, shotFrom, arrow, BowItem.getPullProgress(instance.getItemUseTime()) > 0.95f, shotFrom.get(ModItemComponents.BOW).arrowType());
		}
	}
}
