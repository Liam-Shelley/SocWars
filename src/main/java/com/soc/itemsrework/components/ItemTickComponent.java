package com.soc.itemsrework.components;

import com.mojang.serialization.Codec;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface ItemTickComponent {
	Codec<ItemTickComponent> CODEC = Codec.unit(null);

	void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot);
}
