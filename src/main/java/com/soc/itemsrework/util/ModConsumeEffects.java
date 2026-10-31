package com.soc.itemsrework.util;

import com.soc.effects.util.ModEffects;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.consume.ApplyEffectsConsumeEffect;

import java.util.List;

import static net.minecraft.component.type.ConsumableComponents.food;

public interface ModConsumeEffects {
	ConsumableComponent IRON_APPLE = food().consumeEffect(new ApplyEffectsConsumeEffect(List.of(
			new StatusEffectInstance(ModEffects.ARMOUR, 60 * 20, 5)
	))).build();
	ConsumableComponent COPPER_APPLE = food().consumeEffect(new ApplyEffectsConsumeEffect(List.of(
			new StatusEffectInstance(StatusEffects.RESISTANCE, 60 * 20, 0),
			new StatusEffectInstance(StatusEffects.ABSORPTION, -1, 0)
	))).build();
	ConsumableComponent DIAMOND_APPLE = food().consumeEffect(new ApplyEffectsConsumeEffect(List.of(
			new StatusEffectInstance(StatusEffects.RESISTANCE, 60 * 20, 1),
			new StatusEffectInstance(StatusEffects.STRENGTH, 60 * 20, 0),
			new StatusEffectInstance(StatusEffects.INSTANT_HEALTH, 1, 1),
			new StatusEffectInstance(StatusEffects.ABSORPTION, -1, 2)
	))).build();
	ConsumableComponent EMERALD_APPLE = food().consumeEffect(new ApplyEffectsConsumeEffect(List.of(
			new StatusEffectInstance(StatusEffects.JUMP_BOOST, 30 * 20, 1),
			new StatusEffectInstance(StatusEffects.SPEED, 30 * 20, 1),
			new StatusEffectInstance(StatusEffects.REGENERATION, 60 * 20, 1),
			new StatusEffectInstance(StatusEffects.ABSORPTION, -1, 1)
	))).build();
	ConsumableComponent NETHERITE_APPLE = food().consumeEffect(new ApplyEffectsConsumeEffect(List.of(
			new StatusEffectInstance(StatusEffects.RESISTANCE, 60 * 20, 1),
			new StatusEffectInstance(StatusEffects.STRENGTH, 120 * 20, 1),
			new StatusEffectInstance(StatusEffects.REGENERATION, 60 * 20, 1),
			new StatusEffectInstance(StatusEffects.INSTANT_HEALTH, 1, 100),
			new StatusEffectInstance(StatusEffects.ABSORPTION, -1, 3)
	))).build();
	ConsumableComponent JELLY_DONUT = food().consumeEffect(new ApplyEffectsConsumeEffect(List.of(
			new StatusEffectInstance(StatusEffects.JUMP_BOOST, 30 * 20, 3),
			new StatusEffectInstance(StatusEffects.SPEED, 45 * 20, 1),
			new StatusEffectInstance(StatusEffects.REGENERATION, 60 * 20, 1),
			new StatusEffectInstance(StatusEffects.ABSORPTION, 30 * 20, 9),
			new StatusEffectInstance(StatusEffects.STRENGTH, 60 * 20, 1),
			new StatusEffectInstance(StatusEffects.RESISTANCE, 15 * 20, 3),
			new StatusEffectInstance(StatusEffects.SATURATION, 60 * 20, 9),
			new StatusEffectInstance(StatusEffects.HASTE, 60 * 20, 2)
	))).build();
	ConsumableComponent ENCHANTED_IRON_APPLE = food().consumeEffect(new ApplyEffectsConsumeEffect(List.of(
			new StatusEffectInstance(ModEffects.ARMOUR, 60 * 20, 11)
	))).build();
	ConsumableComponent ENCHANTED_COPPER_APPLE = food().consumeEffect(new ApplyEffectsConsumeEffect(List.of(
			new StatusEffectInstance(StatusEffects.RESISTANCE, 60 * 20, 1),
			new StatusEffectInstance(StatusEffects.ABSORPTION, -1, 1)
	))).build();
	ConsumableComponent ENCHANTED_DIAMOND_APPLE = food().consumeEffect(new ApplyEffectsConsumeEffect(List.of(
			new StatusEffectInstance(StatusEffects.RESISTANCE, 60 * 20, 3),
			new StatusEffectInstance(StatusEffects.STRENGTH, 60 * 20, 1),
			new StatusEffectInstance(StatusEffects.INSTANT_HEALTH, 1, 3),
			new StatusEffectInstance(StatusEffects.ABSORPTION, -1, 5)
	))).build();
	ConsumableComponent ENCHANTED_EMERALD_APPLE = food().consumeEffect(new ApplyEffectsConsumeEffect(List.of(
			new StatusEffectInstance(StatusEffects.JUMP_BOOST, 30 * 20, 3),
			new StatusEffectInstance(StatusEffects.SPEED, 30 * 20, 3),
			new StatusEffectInstance(StatusEffects.REGENERATION, 60 * 20, 3),
			new StatusEffectInstance(StatusEffects.ABSORPTION, -1, 3)
	))).build();
	ConsumableComponent ENCHANTED_NETHERITE_APPLE = food().consumeEffect(new ApplyEffectsConsumeEffect(List.of(
			new StatusEffectInstance(StatusEffects.RESISTANCE, 60 * 20, 3),
			new StatusEffectInstance(StatusEffects.STRENGTH, 120 * 20, 3),
			new StatusEffectInstance(StatusEffects.REGENERATION, 60 * 20, 3),
			new StatusEffectInstance(StatusEffects.INSTANT_HEALTH, 1, 100),
			new StatusEffectInstance(StatusEffects.ABSORPTION, -1, 7)
	))).build();
}
