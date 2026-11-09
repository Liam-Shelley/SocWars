package com.soc.items.components;

import com.mojang.serialization.Codec;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

@FunctionalInterface
public interface ItemStopUsingComponent {
	Codec<ItemStopUsingComponent> CODEC = Codec.unit(null);

	boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks);
}
