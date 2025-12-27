package com.soc.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;

public class Perplexity extends StatusEffect {
    public Perplexity() {
        super(StatusEffectCategory.HARMFUL, 0x00000000);
    }

    @Override
    public void onApplied(LivingEntity entity, int amplifier) {

    }
}