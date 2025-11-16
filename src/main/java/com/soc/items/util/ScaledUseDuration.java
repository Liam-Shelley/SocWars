package com.soc.items.util;

import net.minecraft.item.ItemStack;

public interface ScaledUseDuration {
    float getScale(ItemStack stack);

    default int useTimeOffset(ItemStack stack) {
        return (int)(this.getScale(stack) * 20f) - 21;
    }
}
