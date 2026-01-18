package com.soc.game.manager.bedwars.traps;

import com.soc.game.manager.bedwars.TeamStats;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.Registry;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.function.Consumer;

public class SimpleTriggerTrap extends Trap {
    public static final Trap MINING_FATIGUE = Registry.register(TRAPS_REGISTRY, "mining_fatigue", new SimpleTriggerTrap(20 * 20, player -> player.addStatusEffect(new StatusEffectInstance(StatusEffects.MINING_FATIGUE, 20 * 20, 1, false, true, true))) {});

    final Consumer<ServerPlayerEntity> triggerFunction;

    public SimpleTriggerTrap(int time, Consumer<ServerPlayerEntity> triggerFunction) {
        super(time);
        this.triggerFunction = triggerFunction;
    }

    @Override
    public void trigger(TeamStats team, ServerPlayerEntity player, int time) {
        super.trigger(team, player, time);
        this.triggerFunction.accept(player);
    }
}
