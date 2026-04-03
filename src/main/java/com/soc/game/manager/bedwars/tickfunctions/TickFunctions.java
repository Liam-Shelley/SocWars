package com.soc.game.manager.bedwars.tickfunctions;

import com.mojang.serialization.Lifecycle;
import com.soc.SocWars;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;

public interface TickFunctions {
    RegistryKey<Registry<AbstractTickFunction>> REGISTRY_KEY = RegistryKey.ofRegistry(Identifier.of(SocWars.MOD_ID, "tick_functions"));
    Registry<AbstractTickFunction> REGISTRY = new SimpleRegistry<>(REGISTRY_KEY, Lifecycle.stable());

    static <T extends AbstractTickFunction> T register(T tickFunction) {
        return Registry.register(REGISTRY, tickFunction.getId(), tickFunction);
    }

    static AbstractTickFunction getOrThrow(Identifier id) {
        if(!REGISTRY.containsId(id)) throw new IllegalStateException("No tick function registered on the client for id: " + id + ". Possible registry mismatch?");
        return REGISTRY.get(id);
    }

    static void initialise() {
        SimpleTickFunction.initialise();
    }
}
