package com.soc.events;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;

@FunctionalInterface
public interface OnDamageTaken {
    boolean onDamage(ServerPlayerEntity target, DamageSource source, float amount);
}
