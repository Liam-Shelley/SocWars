package com.soc.game.manager.bedwars.traps;

import com.mojang.serialization.Lifecycle;
import com.soc.SocWars;
import com.soc.game.manager.bedwars.TeamStats;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

public abstract class Trap {
    public static final RegistryKey<Registry<Trap>> TRAPS_REGISTRY_KEY = RegistryKey.ofRegistry(Identifier.of(SocWars.MOD_ID, "traps"));
    public static final Registry<Trap> TRAPS_REGISTRY = new SimpleRegistry<>(TRAPS_REGISTRY_KEY, Lifecycle.stable());

    private final int time;

    private int timeTriggered = -1;

    public Trap(int time) {
        this.time = time;
    }

    @MustBeInvokedByOverriders
    public void trigger(TeamStats team, ServerPlayerEntity player, int time) {
        this.timeTriggered = time;
    }

    public boolean readyToRemoveFromQueue(int time) {
        return this.timeTriggered != -1 && time > this.timeTriggered + this.time;
    }
}
