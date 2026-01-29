package com.soc.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;

public class Flight extends StatusEffect {
    private PlayerEntity playerEntity;

    public Flight() {
        super(StatusEffectCategory.BENEFICIAL, 0xfffdec);
    }

    @Override
    public void onApplied(LivingEntity entity, int amplifier) {
        if (entity instanceof PlayerEntity playerEntity) {
            this.playerEntity = playerEntity;
            playerEntity.getAbilities().allowFlying = true;
            playerEntity.sendAbilitiesUpdate();
        }
    }

    @Override
    public void onRemoved(AttributeContainer attributeContainer) {
        this.playerEntity.getAbilities().allowFlying = this.playerEntity.isCreative() || this.playerEntity.isSpectator();
        if (!this.playerEntity.isCreative()) this.playerEntity.getAbilities().flying = false;
        this.playerEntity.sendAbilitiesUpdate();
    }
}
