package com.soc.game.manager.bedwars.traps;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public abstract class Trap {
    private final int cooldownTime;

    public Trap(int time) {
        this.cooldownTime = time;
    }

    public abstract void trigger(Vec3d pos, List<ServerPlayerEntity> team, List<ServerPlayerEntity> enemies);

    public final int getCooldownTime() {
        return this.cooldownTime;
    }
}
