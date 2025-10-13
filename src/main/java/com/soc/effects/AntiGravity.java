package com.soc.effects;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class AntiGravity extends StatusEffect {
    public AntiGravity() {
        super(StatusEffectCategory.BENEFICIAL, 0xfffdec);
    }

    @Override
    public void onApplied(LivingEntity entity, int amplifier) {
        float gravity = (float) entity.getAttributeValue(EntityAttributes.GRAVITY);
        Objects.requireNonNull(entity.getAttributeInstance(EntityAttributes.GRAVITY)).addTemporaryModifier(new EntityAttributeModifier(Identifier.of("gravity_orb"), -(11 + amplifier) * 0.1f * gravity, EntityAttributeModifier.Operation.ADD_VALUE));
    }

    @Override
    public void onRemoved(AttributeContainer attributeContainer) {
        Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> map = ImmutableMultimap.of(EntityAttributes.GRAVITY, new EntityAttributeModifier(Identifier.of("gravity_orb"), 0, EntityAttributeModifier.Operation.ADD_VALUE));
        attributeContainer.removeModifiers(map);
    }
}
