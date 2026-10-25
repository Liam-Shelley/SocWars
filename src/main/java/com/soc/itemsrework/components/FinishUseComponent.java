package com.soc.itemsrework.components;

import com.mojang.serialization.Codec;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface FinishUseComponent {
	Codec<FinishUseComponent> CODEC = Codec.unit(null);

	void finishUsing(ItemStack stack, World world, LivingEntity user);
}
