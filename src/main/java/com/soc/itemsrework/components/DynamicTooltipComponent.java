package com.soc.itemsrework.components;

import com.mojang.serialization.Codec;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;

@FunctionalInterface
public interface DynamicTooltipComponent {
	Codec<DynamicTooltipComponent> CODEC = Codec.unit(null);

	List<Text> create(ItemStack stack, long time);
}
