package com.soc.game.manager.bedwars;

import com.soc.items.components.ModComponents;
import com.soc.resourcedata.containers.BedwarsShopDataContainer;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class PlayerStats {
    public static final Collector<PlayerStats, ?, Map<UUID, PlayerStats>> MAP_COLLECTOR = Collectors.toMap(PlayerStats::getPlayer, Function.identity());

    private final UUID player;
    private final BedwarsShopContents shopContents;
    private boolean isAlive = true;

    private final Map<UUID, OptionalInt> toolSlotMap;

    public PlayerStats(ServerPlayerEntity player, long shopSeed) {
        this.player = player.getUuid();
        this.shopContents = BedwarsShopDataContainer.INSTANCE.getBedwarsShop(shopSeed);
        this.toolSlotMap = new HashMap<>();
    }

    public void onDeath(boolean canRespawn, World world) {
        if (!canRespawn) {
            this.isAlive = false;
        }

        this.shopContents.downgradeItems();
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

    public UUID getPlayer() {
        return this.player;
    }

    public boolean isAlive() {
        return this.isAlive;
    }

    public BedwarsShopContents getShopContents() {
        return this.shopContents;
    }
}
