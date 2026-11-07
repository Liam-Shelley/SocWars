package com.soc.itemsrework.components;

import com.mojang.serialization.Codec;
import com.soc.SocWars;
import com.soc.items.components.CommandFunctionComponent;
import com.soc.itemsrework.TrainingWeightsComponent;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;

import java.util.function.Predicate;

public interface ModItemComponentsReworked {
	ComponentType<ItemHitComponent> ITEM_HIT = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Identifier.of(SocWars.MOD_ID, "item_hit"),
			ComponentType.<ItemHitComponent>builder().codec(ItemHitComponent.CODEC).build()
	);
	///Is called on both the server and client side whenever the attack button is pressed, rather than only when an entity is hit as with {@link #ITEM_HIT}
	ComponentType<ItemHitAllComponent> ITEM_HIT_ALL = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Identifier.of(SocWars.MOD_ID, "item_hit_all"),
			ComponentType.<ItemHitAllComponent>builder().codec(ItemHitAllComponent.CODEC).build()
	);
	ComponentType<ItemUseComponent> ITEM_USE = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Identifier.of(SocWars.MOD_ID, "item_use"),
			ComponentType.<ItemUseComponent>builder().codec(ItemUseComponent.CODEC).build()
	);
	ComponentType<ItemStopUsingComponent> ITEM_STOP_USING = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Identifier.of(SocWars.MOD_ID, "item_stop_using"),
			ComponentType.<ItemStopUsingComponent>builder().codec(ItemStopUsingComponent.CODEC).build()
	);
	ComponentType<ItemUseOnBlockComponent> ITEM_USE_ON_BLOCK = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Identifier.of(SocWars.MOD_ID, "item_use_on_block"),
			ComponentType.<ItemUseOnBlockComponent>builder().codec(ItemUseOnBlockComponent.CODEC).build()
	);
	ComponentType<ItemTickComponent> ITEM_TICK = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Identifier.of(SocWars.MOD_ID, "item_tick"),
			ComponentType.<ItemTickComponent>builder().codec(ItemTickComponent.CODEC).build()
	);
	ComponentType<RegistryKey<DamageType>> DAMAGE_TYPE = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Identifier.of(SocWars.MOD_ID, "damage_type"),
			ComponentType.<RegistryKey<DamageType>>builder().codec(RegistryKey.createCodec(RegistryKeys.DAMAGE_TYPE)).build()
	);
	ComponentType<Predicate<LivingEntity>> CAN_CONDITIONALLY_CONSUME = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Identifier.of(SocWars.MOD_ID, "can_conditionally_consume"),
			ComponentType.<Predicate<LivingEntity>>builder().codec(Codec.unit(entity -> true)).build()
	);
	ComponentType<ItemFinishUseComponent> FINISH_USE = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Identifier.of(SocWars.MOD_ID, "item_finish_use"),
			ComponentType.<ItemFinishUseComponent>builder().codec(ItemFinishUseComponent.CODEC).build()
	);
	ComponentType<DynamicTooltipComponent> DYNAMIC_TOOLTIP = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Identifier.of(SocWars.MOD_ID, "dynamic_tooltip"),
			ComponentType.<DynamicTooltipComponent>builder().codec(DynamicTooltipComponent.CODEC).build()
	);
	ComponentType<ExponComponent> EXPON = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Identifier.of(SocWars.MOD_ID, "expon"),
			ComponentType.<ExponComponent>builder().codec(ExponComponent.CODEC).build()
	);
	ComponentType<Boolean> DOUBLE_JUMP = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Identifier.of(SocWars.MOD_ID, "double_jump"),
			ComponentType.<Boolean>builder().codec(Codec.BOOL).build()
	);
	ComponentType<CommandFunctionComponent> COMMAND_FUNCTION = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Identifier.of(SocWars.MOD_ID, "command_function"),
			ComponentType.<CommandFunctionComponent>builder().codec(CommandFunctionComponent.CODEC).build()
	);
	ComponentType<Unit> CANCEL_BLOCK_INTERACTION = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Identifier.of(SocWars.MOD_ID, "cancel_block_interaction"),
			ComponentType.<Unit>builder().codec(Unit.CODEC).build()
	);
	ComponentType<StasisChamberComponent> STASIS_CHAMBER = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Identifier.of(SocWars.MOD_ID, "stasis_chamber"),
			ComponentType.<StasisChamberComponent>builder().codec(StasisChamberComponent.CODEC).build()
	);
	ComponentType<Boolean> STASIS_CHAMBER_PRESENT = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Identifier.of(SocWars.MOD_ID, "stasis_chamber_present"),
			ComponentType.<Boolean>builder().codec(Codec.BOOL).build()
	);
	ComponentType<GamblerSwordComponent> GAMBLER_SWORD = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Identifier.of(SocWars.MOD_ID, "gambler_sword"),
			ComponentType.<GamblerSwordComponent>builder().codec(GamblerSwordComponent.CODEC).build()
	);
	ComponentType<GamblerArmourComponent> GAMBLER_ARMOUR = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Identifier.of(SocWars.MOD_ID, "gambler_armour"),
			ComponentType.<GamblerArmourComponent>builder().codec(GamblerArmourComponent.CODEC).build()
	);
	ComponentType<Unit> NO_COMPONENT_UPDATE_BOB = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Identifier.of(SocWars.MOD_ID, "no_component_update_bob"),
			ComponentType.<Unit>builder().codec(Unit.CODEC).build()
	);
	ComponentType<ArmourSetBonus> ARMOUR_SET_BONUS = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Identifier.of(SocWars.MOD_ID, "armour_set_bonus"),
			ComponentType.<ArmourSetBonus>builder().codec(ArmourSetBonus.CODEC).build()
	);
	ComponentType<ArmourHitComponent> ARMOUR_HIT = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Identifier.of(SocWars.MOD_ID, "armour_hit"),
			ComponentType.<ArmourHitComponent>builder().codec(ArmourHitComponent.CODEC).build()
	);
	ComponentType<GlassArmourComponent> GLASS_ARMOUR = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Identifier.of(SocWars.MOD_ID, "glass_armour"),
			ComponentType.<GlassArmourComponent>builder().codec(GlassArmourComponent.CODEC).build()
	);
	ComponentType<IllusionerArmourComponent> ILLUSIONER_ARMOUR = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Identifier.of(SocWars.MOD_ID, "illusioner_armour"),
			ComponentType.<IllusionerArmourComponent>builder().codec(IllusionerArmourComponent.CODEC).build()
	);
	ComponentType<Unit> TRANSPARENT_ARMOUR = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Identifier.of(SocWars.MOD_ID, "transparent_armour"),
			ComponentType.<Unit>builder().codec(Unit.CODEC).build()
	);
	ComponentType<TrainingWeightsComponent> TRAINING_WEIGHTS = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Identifier.of(SocWars.MOD_ID, "training_weights"),
			ComponentType.<TrainingWeightsComponent>builder().codec(TrainingWeightsComponent.CODEC).build()
	);
}
