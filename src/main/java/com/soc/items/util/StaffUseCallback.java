package com.soc.items.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

@FunctionalInterface
public interface StaffUseCallback {
	void useStaff(ItemStack stack, World world, LivingEntity user, float holdTime);
}
