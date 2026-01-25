package com.soc.game.manager.bedwars.traps;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;

import static com.soc.game.manager.AbstractGameManager.mapUuidsToPlayers;

public class TrapManager {
    public static final double TRAP_DETECTION_RANGE = 8d;
    public static final int MAX_TRAP_QUEUE_SIZE = 3;

    private final Set<UUID> team;
    private final World world;

    private final Queue<Trap> traps = new LinkedList<>();
    private long nextTrapTriggerTime;
    private int currentTrapDuration;
    private long nextAbilityTriggerTime;
    private int currentAbilityDuration;

    public TrapManager(Set<UUID> players, World world) {
        this.team = players;
        this.world = world;
        this.nextTrapTriggerTime = world.getTime();
    }

    public boolean hasActiveTrap() {
        return !this.traps.isEmpty() && this.nextTrapTriggerTime < this.world.getTime();
    }

    public void trigger(Vec3d pos, List<ServerPlayerEntity> players) {
        final Trap trap = this.traps.remove();
        trap.trigger(pos, mapUuidsToPlayers(this.world, this.team), players);

        this.nextTrapTriggerTime = this.world.getTime() + trap.getCooldownTime();
        this.currentTrapDuration = trap.getCooldownTime();
    }

    private void triggerAbility(Vec3d pos, List<ServerPlayerEntity> players) {
        final Trap ability = this.traps.remove();
        ability.trigger(pos, mapUuidsToPlayers(this.world, this.team), players);

        this.nextAbilityTriggerTime = this.world.getTime() + ability.getCooldownTime();
        this.currentAbilityDuration = ability.getCooldownTime();
    }

    public boolean addTrap(Trap trap) {
        if (this.traps.size() >= MAX_TRAP_QUEUE_SIZE) return false;

        this.traps.add(trap);
        return true;
    }

    public int[] getTrapProgressStats() {
        return new int[]{
                (int)this.nextTrapTriggerTime,
                this.currentTrapDuration,
                (int)this.nextAbilityTriggerTime,
                this.currentTrapDuration
        };
    }
}
