package com.soc.items.components;

import com.mojang.serialization.Codec;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

@FunctionalInterface
public interface ItemFinishUseComponent {
	Codec<ItemFinishUseComponent> CODEC = Codec.unit(null);

	void finishUsing(ItemStack stack, World world, LivingEntity user);
}
