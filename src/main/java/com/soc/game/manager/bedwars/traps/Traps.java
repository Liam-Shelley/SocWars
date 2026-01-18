package com.soc.game.manager.bedwars.traps;

import com.mojang.serialization.Lifecycle;
import com.soc.SocWars;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;

public interface Traps {
    RegistryKey<Registry<Trap>> REGISTRY_KEY = RegistryKey.ofRegistry(Identifier.of(SocWars.MOD_ID, "traps"));
    Registry<Trap> REGISTRY = new SimpleRegistry<>(REGISTRY_KEY, Lifecycle.stable());

    static <T extends Trap> T register(String id, T trap) {
        return Registry.register(REGISTRY, Identifier.of(SocWars.MOD_ID, id), trap);
    }

    static void initialise() {
        SimpleTriggerTrap.initialise();
    }
}
