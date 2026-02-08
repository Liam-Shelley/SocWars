package com.soc.mixin;

import com.soc.lib.EntityAttributes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class LivingEntityAttributesMixin {
    @Redirect(method = "createLivingAttributes", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/attribute/DefaultAttributeContainer$Builder;add(Lnet/minecraft/registry/entry/RegistryEntry;)Lnet/minecraft/entity/attribute/DefaultAttributeContainer$Builder;", ordinal = 0))
    private static DefaultAttributeContainer.Builder a(DefaultAttributeContainer.Builder instance, RegistryEntry<EntityAttribute> attribute) {
        instance.add(attribute);
        instance.add(EntityAttributes.EXPLOSION_RESISTANCE);
        return instance;
    }
}
