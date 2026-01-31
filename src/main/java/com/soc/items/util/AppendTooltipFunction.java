package com.soc.items.util;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.function.Consumer;

@FunctionalInterface
public interface AppendTooltipFunction {
    void appendTooltip(ItemStack stack, Consumer<Text> textConsumer);
}
