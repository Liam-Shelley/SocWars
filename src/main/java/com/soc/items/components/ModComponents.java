package com.soc.items.components;

import com.mojang.serialization.Codec;
import com.soc.SocWars;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;

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
    ComponentType<ExponComponent> EXPON_COMPONENT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(SocWars.MOD_ID, "expon"),
            ComponentType.<ExponComponent>builder().codec(ExponComponent.CODEC).build()
    );
    ComponentType<Unit> RESOURCE_COUNTED = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(SocWars.MOD_ID, "resource_counted"),
            ComponentType.<Unit>builder().codec(Codec.unit(Unit.INSTANCE)).build()
    );

    static void initialise() {}
}
