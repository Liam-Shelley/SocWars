package com.soc.itemsrework.components;

import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.List;
import java.util.function.BiConsumer;

public interface ComponentWithAttributeModifiers {
	List<AttributeModifiersComponent.Entry> getModifiers(EquipmentSlot slot);

	default void applyModifiers(EquipmentSlot slot, BiConsumer<RegistryEntry<EntityAttribute>, EntityAttributeModifier> attributeConsumer) {
		this.getModifiers(slot).forEach(entry -> {
			if (entry.slot().matches(slot)) {
				attributeConsumer.accept(entry.attribute(), entry.modifier());
			}
		});
	}
}
