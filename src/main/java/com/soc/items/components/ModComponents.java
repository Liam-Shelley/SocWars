package com.soc.items.components;

import com.mojang.serialization.Codec;
import com.soc.SocWars;
import com.soc.networking.helper.BlockLocation;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;

import java.util.Optional;

public interface ModComponents {
    ComponentType<RingItemComponent> RING_ITEM_COMPONENT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(SocWars.MOD_ID, "ring_item"),
            ComponentType.<RingItemComponent>builder().codec(RingItemComponent.CODEC).build()
    );
    ComponentType<Integer> TRAINING_WEIGHTS_COMPONENT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(SocWars.MOD_ID, "training_weights"),
            ComponentType.<Integer>builder().codec(Codec.INT).build()
    );
    ComponentType<Unit> RESOURCE_COUNTED = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(SocWars.MOD_ID, "resource_counted"),
            ComponentType.<Unit>builder().codec(Unit.CODEC).build()
    );
    ComponentType<Unit> RESOURCE_SPLIT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(SocWars.MOD_ID, "resource_split"),
            ComponentType.<Unit>builder().codec(Unit.CODEC).build()
    );
    ComponentType<Integer> GAME_TOOL = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(SocWars.MOD_ID, "game_tool"),
            ComponentType.<Integer>builder().codec(Codec.INT).build()
    );
    ComponentType<Integer> GENERATOR_REFERENCE = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(SocWars.MOD_ID, "generator_reference"),
            ComponentType.<Integer>builder().codec(Codec.INT).build()
    );
    ComponentType<Optional<BlockLocation>> STASIS_POSITION = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(SocWars.MOD_ID, "stasis_position"),
            ComponentType.<Optional<BlockLocation>>builder().codec(BlockLocation.OPTIONAL_CODEC).build()
    );

    static void initialise() {}
}
