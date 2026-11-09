package com.soc.effects;

import com.soc.SocWars;
import com.soc.effects.util.OnEffectRemoved;
import com.soc.lib.ModEntityAttributes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

public class Flight extends StatusEffect implements OnEffectRemoved {
    public Flight() {
        super(StatusEffectCategory.BENEFICIAL, 0xff66f0f2);
        this.addAttributeModifier(ModEntityAttributes.FLIGHT, Identifier.of(SocWars.MOD_ID, "effect.flight"), 1d, EntityAttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    public void onApplied(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity playerEntity) {
            playerEntity.getAbilities().allowFlying = true;

            playerEntity.sendAbilitiesUpdate();
        }
    }

    @Override
    public void onRemoved(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity playerEntity) {
            final boolean allowFlying = playerEntity.isCreative() || playerEntity.isSpectator();

            playerEntity.getAbilities().allowFlying = allowFlying;
            if (!allowFlying) playerEntity.getAbilities().flying = false;

            playerEntity.sendAbilitiesUpdate();
        }
    }
}
