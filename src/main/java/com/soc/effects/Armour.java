package com.soc.effects;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.LivingEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

import java.util.Objects;

public class Armour extends StatusEffect {
    public Armour() {
        super(StatusEffectCategory.BENEFICIAL, 0xff8e999c);
    }

    @Override
    public void onApplied(LivingEntity entity, int amplifier) {
        Objects.requireNonNull(entity.getAttributeInstance(EntityAttributes.ARMOR)).addTemporaryModifier(new EntityAttributeModifier(Identifier.ofVanilla("apple_armour"), amplifier + 1, EntityAttributeModifier.Operation.ADD_VALUE));
    }

    @Override
    public void onRemoved(AttributeContainer attributeContainer) {
        final Multimap<RegistryEntry<EntityAttribute>, EntityAttributeModifier> map = ImmutableMultimap.of(EntityAttributes.ARMOR, new EntityAttributeModifier(Identifier.ofVanilla("apple_armour"), 0, EntityAttributeModifier.Operation.ADD_VALUE));
        attributeContainer.removeModifiers(map);
    }
}