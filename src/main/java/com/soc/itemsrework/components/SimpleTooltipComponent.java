package com.soc.itemsrework.components;

import com.mojang.serialization.Codec;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public interface SimpleTooltipComponent {
	Codec<SimpleTooltipComponent> CODEC = Codec.unit(null);

	void appendTooltip(Consumer<Text> textConsumer);
}
