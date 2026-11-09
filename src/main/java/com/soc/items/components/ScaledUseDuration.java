package com.soc.items.components;

import net.minecraft.item.ItemStack;

@FunctionalInterface
public interface ScaledUseDuration {
    float getScale(ItemStack stack);
}
