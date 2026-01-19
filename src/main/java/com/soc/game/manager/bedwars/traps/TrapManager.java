package com.soc.game.manager.bedwars.traps;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;

import static com.soc.game.manager.AbstractGameManager.mapUuidsToPlayers;

public class TrapManager {
    public static final double TRAP_DETECTION_RANGE = 8d;
    public static final int MAX_TRAP_QUEUE_SIZE = 3;

    private final Set<UUID> players;
    private final World world;

    private final Queue<Trap> traps = new LinkedList<>();
    private long nextTrapTriggerTime;

    public TrapManager(Set<UUID> players, World world) {
        this.players = players;
        this.world = world;
        this.nextTrapTriggerTime = world.getTime();

        traps.addAll(List.of(SimpleTriggerTrap.ENLARGEMENT, SimpleTriggerTrap.SHUFFLE, SimpleTriggerTrap.POSTURA, SimpleTriggerTrap.PERPLEXITY, SimpleTriggerTrap.LAUNCH));
    }

    public boolean hasActiveTrap() {
        return !this.traps.isEmpty() && this.cooldownFinished();
    }

    public void trigger(Vec3d pos, List<ServerPlayerEntity> players) {
        final Trap trap = this.traps.remove();
        trap.trigger(pos, mapUuidsToPlayers(this.world, this.players), players);
        this.nextTrapTriggerTime = this.world.getTime() + trap.getCooldownTime();
    }

    private boolean cooldownFinished() {
        final boolean finished = this.nextTrapTriggerTime < this.world.getTime();
        return finished;
    }

    public boolean addTrap(Trap trap) {
        if (this.traps.size() >= MAX_TRAP_QUEUE_SIZE) return false;

        this.traps.add(trap);
        return true;
    }
}
