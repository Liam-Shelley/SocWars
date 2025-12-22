package com.soc.game.manager.bedwars;

import com.soc.items.components.ModComponents;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class PlayerStats {
    public static final Collector<PlayerStats, ?, Map<UUID, PlayerStats>> MAP_COLLECTOR = Collectors.toMap(PlayerStats::getPlayer, Function.identity());

    private final UUID player;
    private boolean isAlive = true;

    private int pickaxeTier;
    private int axeTier;
    private int shearsTier;
    private int armourTier;

    private final Int2IntMap toolSlotMap = new Int2IntOpenHashMap(8);

    public PlayerStats(ServerPlayerEntity player) {
        this.player = player.getUuid();
    }

    public void onDeath(boolean canRespawn, World world) {
        if (!canRespawn) {
            this.isAlive = false;
        }

        if (this.pickaxeTier > 0) this.pickaxeTier--;
        if (this.axeTier > 0) this.axeTier--;
        if (this.shearsTier > 0) this.shearsTier--;
        if (this.armourTier > 0) this.armourTier--;

        this.updateToolMap(world);
    }

    public boolean resurrect() {
        if (this.isAlive) return false;

        this.isAlive = true;
        return true;
    }

    private void updateToolMap(World world) {
        final PlayerEntity player = world.getPlayerByUuid(this.player);
        if (player == null) return;

        final PlayerInventory inventory = player.getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            final Integer component = inventory.getStack(i).get(ModComponents.GAME_TOOL);
            if (component != null) toolSlotMap.put(i, component.intValue());
        }
    }

    public Int2IntMap getToolSlotMap() {
        return this.toolSlotMap;
    }

    public UUID getPlayer() {
        return this.player;
    }

    public boolean isAlive() {
        return this.isAlive;
    }
}
