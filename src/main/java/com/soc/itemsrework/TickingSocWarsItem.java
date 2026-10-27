package com.soc.itemsrework;

import com.soc.itemsrework.components.ItemTickComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import org.jetbrains.annotations.Nullable;

public class TickingSocWarsItem extends SocWarsItem {
	public TickingSocWarsItem(Settings settings) {
		super(settings);
	}

	@Override
	public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
		stack.streamAll(ItemTickComponent.class).forEach(component -> component.inventoryTick(stack, world, entity, slot));
	}
}
