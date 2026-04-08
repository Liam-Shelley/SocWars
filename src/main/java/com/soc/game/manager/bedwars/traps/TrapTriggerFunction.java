package com.soc.game.manager.bedwars.traps;

import com.soc.game.manager.AbstractGameManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Vec3d;

import java.util.Collection;

public interface TrapTriggerFunction {
    void trigger(Vec3d pos, AbstractGameManager<?, ?, ?> manager, Collection<ServerPlayerEntity> enemies, DyeColor team);
}
