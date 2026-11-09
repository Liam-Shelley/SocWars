package com.soc.effects.util;

import net.minecraft.entity.LivingEntity;

@FunctionalInterface
public interface OnEffectRemoved {
	void onRemoved(LivingEntity entity, int amplifier);
}
