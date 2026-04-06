package com.soc.mixin;

import com.soc.effects.util.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static com.soc.lib.SocWarsLib.mapIfNotNull;

@Mixin(LivingEntity.class)
abstract class LightweightKnockbackModifier {
    @Shadow @Nullable public abstract StatusEffectInstance getStatusEffect(RegistryEntry<StatusEffect> effect);

    @Redirect(method = "takeKnockback", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;multiply(D)Lnet/minecraft/util/math/Vec3d;"))
    private Vec3d socwars_lightweightKnockbackModifier(Vec3d instance, double value) {
        return instance.multiply(value * (mapIfNotNull(this.getStatusEffect(ModEffects.LIGHTWEIGHT), StatusEffectInstance::getAmplifier, -1) + 2)); //I know that I could write this more neatly by only actually redirecting if the effect intance is not null, but I like this one liner it tickles my brain
    }
}
