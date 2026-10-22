package com.soc.itemsrework.components;

import com.mojang.serialization.Codec;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

public interface HitItemComponent {
	Codec<HitItemComponent> CODEC = Codec.unit(null);

	void postHit(ItemStack stack, LivingEntity target, LivingEntity attacker);
}
