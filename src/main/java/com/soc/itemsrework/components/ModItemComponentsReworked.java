package com.soc.itemsrework.components;

import com.mojang.serialization.Codec;
import com.soc.SocWars;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;

import java.util.function.Predicate;

public interface ModItemComponentsReworked {
	ComponentType<ItemHitComponent> ITEM_HIT = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Identifier.of(SocWars.MOD_ID, "hit_item"),
			ComponentType.<ItemHitComponent>builder().codec(ItemHitComponent.CODEC).build()
	);
	ComponentType<ItemUseComponent> ITEM_USE = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Identifier.of(SocWars.MOD_ID, "use_item"),
			ComponentType.<ItemUseComponent>builder().codec(ItemUseComponent.CODEC).build()
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
	ComponentType<FinishUseComponent> FINISH_USE = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Identifier.of(SocWars.MOD_ID, "finish_use"),
			ComponentType.<FinishUseComponent>builder().codec(FinishUseComponent.CODEC).build()
	);
}
