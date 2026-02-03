package com.soc.items.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

@FunctionalInterface
public interface OnHitArmour {
    default boolean onHit(ItemStack stack, LivingEntity wearer, DamageSource source) {
        return this.onHit(stack, wearer, wearer.getWorld(), source);
    }

    boolean onHit(ItemStack stack, LivingEntity wearer, World world, DamageSource damageSource);
}
