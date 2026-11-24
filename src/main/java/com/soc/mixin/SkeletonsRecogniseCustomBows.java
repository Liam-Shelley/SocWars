package com.soc.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.soc.items.components.ModItemComponents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ProjectileUtil.class)
abstract class SkeletonsRecogniseCustomBows {
	@WrapMethod(method = "getHandPossiblyHolding")
	private static Hand socwars$findCustomBowInHand(LivingEntity entity, Item item, Operation<Hand> original) {
		final Hand originalReturn = original.call(entity, item);

		if (item == Items.BOW && !entity.getStackInHand(originalReturn).isEmpty()) {
			return originalReturn;
		} else {
			return entity.getMainHandStack().contains(ModItemComponents.BOW) ? Hand.MAIN_HAND : Hand.OFF_HAND;
		}
	}
}
