package com.soc.items.util;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.entry.RegistryEntry;

public record EffectRecord(RegistryEntry<StatusEffect> effect, int amplifier, int duration) {

    public EffectRecord(RegistryEntry<StatusEffect> effect, int amplifier) {
        this(effect, amplifier, 0);
    }
}