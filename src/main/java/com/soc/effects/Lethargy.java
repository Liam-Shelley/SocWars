package com.soc.effects;

import com.soc.SocWars;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.util.Identifier;

public class Lethargy extends StatusEffect {
    public Lethargy() {
        super(StatusEffectCategory.HARMFUL, 0xff000040);
        this.addAttributeModifier(
                EntityAttributes.MOVEMENT_SPEED, Identifier.of(SocWars.MOD_ID, "effect.lethargy"), -0.15f, EntityAttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
    }
}
