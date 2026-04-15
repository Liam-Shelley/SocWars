package com.soc.effects.util;

import com.soc.SocWars;
import com.soc.effects.*;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.potion.Potion;
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
    RegistryEntry<StatusEffect> LIGHTWEIGHT = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(SocWars.MOD_ID, "lightweight"), new Lightweight());

    RegistryEntry<Potion> ANTI_GRAVITY_BASE = registerPotion("anti_gravity", new Potion("anti_gravity", new StatusEffectInstance(ANTI_GRAVITY, 10 * 20, 0)));
    RegistryEntry<Potion> ANTI_GRAVITY_STRONG = registerPotion("anti_gravity_strong", new Potion("anti_gravity", new StatusEffectInstance(ANTI_GRAVITY, 8 * 20, 1)));

    RegistryEntry<Potion> ARMOUR_BASE = registerPotion("armour", new Potion("armour", new StatusEffectInstance(ARMOUR, 120 * 20, 1)));
    RegistryEntry<Potion> ARMOUR_STRONG = registerPotion("armour_strong", new Potion("armour", new StatusEffectInstance(ARMOUR, 60 * 20, 3)));
    RegistryEntry<Potion> ARMOUR_LONG = registerPotion("armour_long", new Potion("armour", new StatusEffectInstance(ARMOUR, 300 * 20, 1)));

    RegistryEntry<Potion> FLIGHT_BASE = registerPotion("flight", new Potion("flight", new StatusEffectInstance(FLIGHT, 20 * 20, 0)));
    RegistryEntry<Potion> FLIGHT_LONG = registerPotion("flight_long", new Potion("flight", new StatusEffectInstance(FLIGHT, 40 * 20, 0)));

    RegistryEntry<Potion> ARTHRODESIS_BASE = registerPotion("arthrodesis", new Potion("arthrodesis", new StatusEffectInstance(ARTHRODESIS, 30 * 20, 0)));
    RegistryEntry<Potion> ARTHRODESIS_LONG = registerPotion("arthrodesis_long", new Potion("arthrodesis", new StatusEffectInstance(ARTHRODESIS, 60 * 20, 0)));

    RegistryEntry<Potion> PERPLEXITY_BASE = registerPotion("perplexity", new Potion("perplexity", new StatusEffectInstance(PERPLEXITY, 20 * 20, 0)));
    RegistryEntry<Potion> PERPLEXITY_STRONG = registerPotion("perplexity_strong", new Potion("perplexity", new StatusEffectInstance(PERPLEXITY, 15 * 20, 1)));
    RegistryEntry<Potion> PERPLEXITY_EXTRA_STRONG = registerPotion("perplexity_extra_strong", new Potion("perplexity", new StatusEffectInstance(PERPLEXITY, 10 * 20, 2)));

    RegistryEntry<Potion> LIGHTWEIGHT_BASE = registerPotion("lightweight", new Potion("lightweight", new StatusEffectInstance(LIGHTWEIGHT, 30 * 20, 1)));
    RegistryEntry<Potion> LIGHTWEIGHT_STRONG = registerPotion("lightweight_strong", new Potion("lightweight", new StatusEffectInstance(LIGHTWEIGHT, 20 * 20, 3)));

    private static RegistryEntry<Potion> registerPotion(String name, Potion potion) {
        return Registry.registerReference(Registries.POTION, Identifier.of(SocWars.MOD_ID, name), potion);
    }
}
