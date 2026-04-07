package com.soc.game.manager.bedwars.traps;

import com.google.common.collect.Multimap;
import com.soc.game.manager.AbstractGameManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Vec3d;

public interface TrapTriggerFunction {
    void trigger(Vec3d pos, AbstractGameManager<?, ?, ?> manager, Multimap<DyeColor, ServerPlayerEntity> enemies, DyeColor team);
}
