package com.soc.game.manager.bedwars;

import com.soc.items.components.ModComponents;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class PlayerStats {
    public static final Collector<PlayerStats, ?, Map<ServerPlayerEntity, PlayerStats>> MAP_COLLECTOR = Collectors.toMap(PlayerStats::getPlayer, Function.identity());

    private final ServerPlayerEntity player;
    private boolean isAlive;

    private int pickaxeTier;
    private int axeTier;
    private int shearsTier;
    private int armourTier;

    private final Int2IntMap toolSlotMap = new Int2IntOpenHashMap(8);

    public PlayerStats(ServerPlayerEntity player) {
        this.player = player;
    }

    public void onDeath(boolean canRespawn) {
        if (!canRespawn) {
            this.isAlive = false;
            return;
        }

        if (this.pickaxeTier > 0) this.pickaxeTier--;
        if (this.axeTier > 0) this.axeTier--;
        if (this.shearsTier > 0) this.shearsTier--;
        if (this.armourTier > 0) this.armourTier--;

        this.updateToolMap();
    }

    public boolean resurrect() {
        if (this.isAlive) return false;

        this.isAlive = true;
        return true;
    }

    private void updateToolMap() {
        final PlayerInventory inventory = this.player.getInventory();
        for (int i = 0; i < inventory.size(); i++) {
            final Integer component = inventory.getStack(i).get(ModComponents.GAME_TOOL);
            if (component != null) toolSlotMap.put(i, component.intValue());
        }
    }

    public Int2IntMap getToolSlotMap() {
        return this.toolSlotMap;
    }

    public ServerPlayerEntity getPlayer() {
        return this.player;
    }

    public boolean isAlive() {
        return this.isAlive;
    }
}
