package com.soc.effects.util;

import com.soc.SocWars;
import com.soc.effects.*;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public interface ModEffects {
    static void initialise() {}

    RegistryEntry<StatusEffect> ANTI_GRAVITY = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(SocWars.MOD_ID, "anti_gravity"), new AntiGravity());
    RegistryEntry<StatusEffect> ARMOUR = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(SocWars.MOD_ID, "armour"), new Armour());
    RegistryEntry<StatusEffect> FLIGHT = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(SocWars.MOD_ID, "flight"), new Flight());
    RegistryEntry<StatusEffect> ARTHRODESIS = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(SocWars.MOD_ID, "arthrodesis"), new Arthrodesis());
    RegistryEntry<StatusEffect> PERPLEXITY = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(SocWars.MOD_ID, "perplexity"), new Perplexity());
    RegistryEntry<StatusEffect> LIGHTWEIGHT = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(SocWars.MOD_ID, "lightweight"), new Perplexity());
}
