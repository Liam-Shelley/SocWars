package com.soc.itemsrework.components;

import com.soc.SocWars;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;

public interface ModItemComponentsReworked {
	ComponentType<HitItemComponent> POST_HIT = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Identifier.of(SocWars.MOD_ID, "post_hit"),
			ComponentType.<HitItemComponent>builder().codec(HitItemComponent.CODEC).build()
	);

	ComponentType<RegistryKey<DamageType>> DAMAGE_TYPE = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Identifier.of(SocWars.MOD_ID, "damage_type"),
			ComponentType.<RegistryKey<DamageType>>builder().codec(RegistryKey.createCodec(RegistryKeys.DAMAGE_TYPE)).build()
	);
	ComponentType<Text> SIMPLE_TOOLTIP = Registry.register(
			Registries.DATA_COMPONENT_TYPE,
			Identifier.of(SocWars.MOD_ID, "simple_tooltip"),
			ComponentType.<Text>builder().codec(TextCodecs.CODEC).build()
	);
}
