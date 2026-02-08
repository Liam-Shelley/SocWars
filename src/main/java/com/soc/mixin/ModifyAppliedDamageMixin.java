package com.soc.mixin;

import com.soc.lib.EntityAttributes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.DamageTypeTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class ModifyAppliedDamageMixin {
    @Shadow public abstract double getAttributeValue(RegistryEntry<EntityAttribute> attribute);

    @Inject(method = "modifyAppliedDamage", at = @At("RETURN"), cancellable = true)
    void a(DamageSource source, float amount, CallbackInfoReturnable<Float> cir) {
        if (source.isIn(DamageTypeTags.IS_EXPLOSION)) cir.setReturnValue(amount * (1f - (float)this.getAttributeValue(EntityAttributes.EXPLOSION_RESISTANCE)));
    }
}
