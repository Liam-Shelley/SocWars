package com.soc.itemsrework.components;

import com.mojang.serialization.Codec;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

@FunctionalInterface
public interface ItemHitAllComponent {
	Codec<ItemHitAllComponent> CODEC = Codec.unit(null);

	void hit(PlayerEntity player, ItemStack stack);
}
