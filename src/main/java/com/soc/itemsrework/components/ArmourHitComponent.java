package com.soc.itemsrework.components;

import com.mojang.serialization.Codec;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

@FunctionalInterface
public interface ArmourHitComponent {
	Codec<ArmourHitComponent> CODEC = Codec.unit(null);

	boolean hit(ItemStack stack, LivingEntity wearer, World world, DamageSource source);
}
