package com.soc.items.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface FinishUsingFunction {
    ItemStack finishUsing(ItemStack stack, World world, LivingEntity user);
}
