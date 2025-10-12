package com.soc.items.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

@FunctionalInterface
public interface OnHitArmour {
    default boolean onHit(ItemStack stack, LivingEntity wearer) {
        return this.onHit(stack, wearer, wearer.getWorld());
    }

    boolean onHit(ItemStack stack, LivingEntity wearer, World world);
}
