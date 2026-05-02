package com.soc.game.manager.bedwars;

import com.soc.items.components.ModComponents;
import com.soc.resourcedata.containers.BedwarsShopDataContainer;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class PlayerStats {
    public static final Collector<PlayerStats, ?, Map<UUID, PlayerStats>> MAP_COLLECTOR = Collectors.toMap(PlayerStats::getPlayer, Function.identity());

    private final UUID player;
    private final BedwarsShopContents shopContents;
    private boolean isAlive = true;

    private Consumer<UUID> playerEliminationCallback;

    private final Int2IntMap toolSlotMap = new Int2IntOpenHashMap();

    public PlayerStats(ServerPlayerEntity player, DyeColor team, long shopSeed) {
        this.player = player.getUuid();
        this.shopContents = BedwarsShopDataContainer.INSTANCE.getIndividualBedwarsShop(shopSeed, team, player.getWorld());
    }

    public void onDeath(boolean canRespawn, World world) {
        if (!canRespawn) {
            this.isAlive = false;
            this.playerEliminationCallback.accept(this.player);
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
            if (component != null) this.toolSlotMap.put(component.intValue(), i);
        }
    }

    public void returnToolsToSlots(World world) {
        final PlayerEntity player = world.getPlayerByUuid(this.player);
        if (player == null) return;

        final PlayerInventory inventory = player.getInventory();
        this.toolSlotMap.forEach((id, slot) -> this.shopContents.getUpgradeableShopItemBySlotTrackingId(id).ifPresent(shopItem -> inventory.setStack(slot, shopItem.getDowngradedStackCopy().copy())));

        player.giveItemStack(new ItemStack(Items.WOODEN_SWORD));
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

    public void buyEnchantmentUpgrade(RegistryEntry<Enchantment> enchantment, World world, int tier) {
        this.shopContents.forEach(category -> category.forEach(item -> {
            if (enchantment.value().isAcceptableItem(item.getIcon())) {
                item.enchant(enchantment, tier); //Change this to an enchant method on the ShopItem interface?
            }
        }));

        final PlayerEntity player = world.getPlayerByUuid(this.player);
        if (player != null) {
            player.getInventory().forEach(stack -> {
                if (enchantment.value().isAcceptableItem(stack)) {
                    stack.addEnchantment(enchantment, tier);
                }
            });
        }
    }

    public void setPlayerEliminationCallback(Consumer<UUID> playerEliminationCallback) {
        this.playerEliminationCallback = playerEliminationCallback;
    }
}
