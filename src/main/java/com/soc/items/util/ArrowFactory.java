package com.soc.items.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

@FunctionalInterface
public interface ArrowFactory<T extends ArrowEntity> {
    T build(World world, LivingEntity shooter, ItemStack projectileStack, ItemStack weaponStack);
}
