package com.soc.game.manager.bedwars.traps;

import com.mojang.serialization.Lifecycle;
import com.soc.SocWars;
import com.soc.game.manager.bedwars.tickfunctions.AbstractTickFunction;
import com.soc.game.manager.bedwars.tickfunctions.TickFunctions;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;

public interface Traps {
    RegistryKey<Registry<AbstractTrap>> REGISTRY_KEY = RegistryKey.ofRegistry(Identifier.of(SocWars.MOD_ID, "traps"));
    Registry<AbstractTrap> REGISTRY = new SimpleRegistry<>(REGISTRY_KEY, Lifecycle.stable());

    static <T extends AbstractTrap> T register(T trap) {
        return Registry.register(REGISTRY, trap.getId(), trap);
    }

    static AbstractTrap getOrThrow(Identifier id) {
        if(!REGISTRY.containsId(id)) throw new IllegalStateException("No trap registered on the client for id: " + id + ". Possible registry mismatch?");
        return REGISTRY.get(id);
    }

    static void initialise() {
        SimpleTriggerTrap.initialise();
        AdvancedTriggerTrap.initialise();
    }
}
