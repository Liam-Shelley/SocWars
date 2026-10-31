package com.soc.itemsrework;

import com.soc.itemsrework.components.*;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.consume.ApplyEffectsConsumeEffect;
import net.minecraft.item.consume.UseAction;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Rarity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static net.minecraft.component.type.PotionContentsComponent.buildTooltip;

public class ExtendedItemSettings extends Item.Settings {
	public ExtendedItemSettings itemHit(ItemHitComponent component) {
		return this.component(ModItemComponentsReworked.ITEM_HIT, component);
	}

	public ExtendedItemSettings itemHitAll(ItemHitAllComponent component) {
		return this.component(ModItemComponentsReworked.ITEM_HIT_ALL, component);
	}

	public ExtendedItemSettings itemUse(ItemUseComponent component) {
		return this.component(ModItemComponentsReworked.ITEM_USE, component);
	}

	public ExtendedItemSettings itemUseOnBlock(ItemUseOnBlockComponent component) {
		return this.component(ModItemComponentsReworked.ITEM_USE_ON_BLOCK, component);
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

	@SuppressWarnings("unchecked")
	public ExtendedItemSettings foodWithPotionTooltip(FoodComponent foodComponent, ConsumableComponent consumableComponent) {
		final List<ApplyEffectsConsumeEffect> effectsComponents = (List<ApplyEffectsConsumeEffect>)(Object)consumableComponent.onConsumeEffects().stream().filter(effect -> effect instanceof ApplyEffectsConsumeEffect).toList();
		if (effectsComponents.isEmpty()) return this.food(foodComponent, consumableComponent);

		final Text[] lines = effectsComponents.stream().flatMap(effectComponent -> {
			final List<Text> componentLines = new ArrayList<>(effectComponent.effects().size());

			buildTooltip(effectComponent.effects(), componentLines::add, 1, 1);

			return componentLines.stream();
		}).toArray(Text[]::new);

		return this
				.food(foodComponent, consumableComponent)
				.simpleTooltip(lines);
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

	public ExtendedItemSettings shears(ToolMaterial toolMaterial) {
		final RegistryEntryLookup<Block> registryEntryLookup = Registries.createEntryLookup(Registries.BLOCK);
		final float speed = toolMaterial.speed();

		final ToolComponent toolComponent = new ToolComponent(
				List.of(
						ToolComponent.Rule.ofAlwaysDropping(RegistryEntryList.of(Blocks.COBWEB.getRegistryEntry()), speed),
						ToolComponent.Rule.of(registryEntryLookup.getOrThrow(BlockTags.LEAVES), speed * 0.75f),
						ToolComponent.Rule.of(registryEntryLookup.getOrThrow(BlockTags.WOOL), speed * 0.65f),
						ToolComponent.Rule.of(RegistryEntryList.of(Blocks.VINE.getRegistryEntry(), Blocks.GLOW_LICHEN.getRegistryEntry()), speed * 0.25f)
				),1, 1, true
		);

		return this
				.component(DataComponentTypes.TOOL, toolComponent)
				.maxDamage(toolMaterial.durability())
				.enchantable(toolMaterial.enchantmentValue());
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

	@Override
	public ExtendedItemSettings food(FoodComponent foodComponent, ConsumableComponent consumableComponent) {
		return (ExtendedItemSettings)super.food(foodComponent, consumableComponent);
	}

	//endregion
}
