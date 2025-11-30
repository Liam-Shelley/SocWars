package com.soc.game.manager.bedwarsshopitem;

import com.soc.game.manager.BedwarsGameManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.OptionalInt;

public abstract class BaseShopItem {
    public static final BaseShopItem EMPTY = new BaseShopItem(1, 1, 1, 1, Items.AIR.getDefaultStack()) {
        @Override
        public boolean buy(PlayerEntity player) {
            return false;
        }
    };

    protected final Map<Item, Integer> costMap;
    protected final ItemStack icon;

    protected BaseShopItem(int iron, int gold, int diamonds, int emeralds, ItemStack icon) {
        this.costMap = new LinkedHashMap<>(4);
        this.costMap.put(Items.IRON_INGOT, iron);
        this.costMap.put(Items.GOLD_INGOT, gold);
        this.costMap.put(Items.DIAMOND, diamonds);
        this.costMap.put(Items.EMERALD, emeralds);

        this.icon = icon;
    }

    public abstract boolean buy(PlayerEntity player);

    public Text getTooltipName() {
        return getTooltipNameOfItem(this.icon);
    }

    protected static Text getTooltipNameOfItem(ItemStack icon) {
        return Text.literal(icon.getCount() + "x ").formatted(Formatting.DARK_PURPLE).append(icon.getItemName().copy().formatted(icon.getRarity().getFormatting()));
    }

    public final OptionalInt canAfford(PlayerEntity player) {
        final PlayerInventory playerInventory = player.getInventory();

        final boolean canAfford = this.costMap.entrySet().stream().map(entry -> playerInventory.count(entry.getKey()) >= entry.getValue()).reduce(true, Boolean::logicalAnd);
        final int emptySlot = playerInventory.getEmptySlot();

        return canAfford && emptySlot >= 0 ? OptionalInt.of(emptySlot) : OptionalInt.empty();
    }

    //Just manually inline this once I actually use it
    protected final BedwarsGameManager getManager(PlayerEntity player) {
        return BedwarsGameManager.getBedwarsGameManager(player);
    }

    public final Map<Item, Integer> getCostMap() {
        return this.costMap;
    }

    public final ItemStack getIcon() {
        return this.icon;
    }
}
