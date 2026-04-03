package com.soc.game.manager.bedwars.tickfunctions;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;

public interface TickFunction {
    void tick(Vec3d pos, List<ServerPlayerEntity> team, int tier, World world);
}
