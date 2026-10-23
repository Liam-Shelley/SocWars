package com.soc.itemsrework;

import com.soc.itemsrework.components.ItemHitComponent;
import com.soc.itemsrework.components.ItemUseComponent;
import com.soc.itemsrework.components.ModItemComponentsReworked;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;

import java.util.List;

public class ExtendedItemSettings extends Item.Settings {
	public ExtendedItemSettings itemHit(ItemHitComponent component) {
		return this.component(ModItemComponentsReworked.ITEM_HIT, component);
	}

	public ExtendedItemSettings itemUse(ItemUseComponent component) {
		return this.component(ModItemComponentsReworked.ITEM_USE, component);
	}

	public ExtendedItemSettings simpleTooltip(Text... lines) {
		return this.component(DataComponentTypes.LORE, tooltip(lines));
	}

	public ExtendedItemSettings damageType(RegistryKey<DamageType> damageType) {
		return this.component(ModItemComponentsReworked.DAMAGE_TYPE, damageType);
	}

	public ExtendedItemSettings enchantmentGlint() {
		return this.component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
	}


	static LoreComponent tooltip(Text... lines) {
		final List<Text> linesList = List.of(lines);
		return new LoreComponent(linesList, linesList);
	}

	//region Overrides so that I can call my extended functions after the superclass functions
	//I'll just add overrides for whatever I use for now because I'm lazy

	@Override
	public ExtendedItemSettings maxCount(int maxCount) {
		return (ExtendedItemSettings)super.maxCount(maxCount);
	}

	@Override
	public ExtendedItemSettings maxDamage(int maxDamage) {
		return (ExtendedItemSettings)super.maxDamage(maxDamage);
	}

	@Override
	public ExtendedItemSettings rarity(Rarity rarity) {
		return (ExtendedItemSettings)super.rarity(rarity);
	}

	@Override
	public <T> ExtendedItemSettings component(ComponentType<T> type, T value) {
		return (ExtendedItemSettings)super.component(type, value);
	}

	@Override
	public ExtendedItemSettings sword(ToolMaterial material, float attackDamage, float attackSpeed) {
		return (ExtendedItemSettings)super.sword(material, attackDamage, attackSpeed);
	}

	@Override
	public ExtendedItemSettings attributeModifiers(AttributeModifiersComponent attributeModifiersComponent) {
		return (ExtendedItemSettings)super.attributeModifiers(attributeModifiersComponent);
	}

	@Override
	public ExtendedItemSettings useCooldown(float seconds) {
		return (ExtendedItemSettings)super.useCooldown(seconds);
	}

	@Override
	public ExtendedItemSettings enchantable(int enchantability) {
		return (ExtendedItemSettings)super.enchantable(enchantability);
	}
	//endregion

	//public ExtendedItemSettings simpleConsumableEffect() {
	//	return this.component(new ConsumableComponent())
	//}
}
