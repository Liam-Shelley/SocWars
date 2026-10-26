package com.soc.itemsrework;

import com.soc.itemsrework.components.*;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.consume.UseAction;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Rarity;

import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class ExtendedItemSettings extends Item.Settings {
	public ExtendedItemSettings itemHit(ItemHitComponent component) {
		return this.component(ModItemComponentsReworked.ITEM_HIT, component);
	}

	public ExtendedItemSettings itemUse(ItemUseComponent component) {
		return this.component(ModItemComponentsReworked.ITEM_USE, component);
	}

	public ExtendedItemSettings simpleTooltip(Text... lines) {
		final List<Text> linesList = List.of(lines);
		return this.component(DataComponentTypes.LORE, new LoreComponent(linesList, linesList));
	}

	public ExtendedItemSettings dynamicTooltip(DynamicTooltipComponent dynamicTooltip) {
		return this.component(ModItemComponentsReworked.DYNAMIC_TOOLTIP, dynamicTooltip);
	}
	public ExtendedItemSettings simpleIterTooltip(int lines, IntFunction<Text> mapper) {
		final List<Text> linesList = IntStream.range(0, lines).mapToObj(mapper).toList();
		return this.component(DataComponentTypes.LORE, new LoreComponent(linesList, linesList));
	}

	public ExtendedItemSettings damageType(RegistryKey<DamageType> damageType) {
		return this.component(ModItemComponentsReworked.DAMAGE_TYPE, damageType);
	}

	public ExtendedItemSettings enchantmentGlint() {
		return this.component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
	}

	public ExtendedItemSettings consumable(float consumeTime, FinishUseComponent onConsumeEffect) {
		return this
				.component(DataComponentTypes.CONSUMABLE, new ConsumableComponent(consumeTime, UseAction.EAT, SoundEvents.ENTITY_GENERIC_EAT, true, List.of()))
				.component(ModItemComponentsReworked.FINISH_USE, onConsumeEffect);
	}

	public ExtendedItemSettings conditionallyConsumable(float consumeTime, FinishUseComponent onConsumeEffect, Predicate<LivingEntity> canConsume) {
		return this
				.consumable(consumeTime, onConsumeEffect)
				.component(ModItemComponentsReworked.CAN_CONDITIONALLY_CONSUME, canConsume);
	}

	public <T extends Entity> ExtendedItemSettings throwable(Function<LivingEntity, T> entityCreator, float speed) {
		return this.itemUse((world, user, hand) -> {
			if (world instanceof ServerWorld serverWorld) {
				spawnEntityWithVelocity(entityCreator.apply(user), serverWorld, user, speed);
			}
			user.getStackInHand(hand).decrementUnlessCreative(1, user);
			return ActionResult.SUCCESS;
		});
	}






	public static <T extends Entity> T spawnEntityWithVelocity(T entity, ServerWorld world, LivingEntity user, float speed) {
		entity.setPosition(user.getEyePos().add(user.getRotationVector().multiply(0.75d)));
		entity.setVelocity(user.getRotationVector().multiply(speed));

		world.spawnEntity(entity);

		return entity;
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
}
