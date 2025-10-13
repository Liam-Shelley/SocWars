package com.soc.items.components;

import com.mojang.serialization.Codec;
import com.soc.SocWars;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModComponents {
    public static final ComponentType<RingItemComponent> RING_ITEM_COMPONENT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(SocWars.MOD_ID, "ring_item_component"),
            ComponentType.<RingItemComponent>builder().codec(RingItemComponent.CODEC).build()
    );
    public static final ComponentType<Integer> TRAINING_WEIGHTS_COMPONENT = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(SocWars.MOD_ID, "training_weights_component"),
            ComponentType.<Integer>builder().codec(Codec.INT).build()
    );

    public static void initialise() {}
}
