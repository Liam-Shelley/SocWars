package com.soc.effects;

import com.soc.util.Sounds;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class Arthrodesis extends StatusEffect {
    public Arthrodesis() {
        super(StatusEffectCategory.HARMFUL, 0xbabbc7);
    }

    @Override
    public void onApplied(LivingEntity entity, int amplifier) {
        entity.getWorld().playSound(null, entity.getX(), entity.getY(), entity.getZ(), Sounds.BONE_BREAK, SoundCategory.PLAYERS);
    }
}