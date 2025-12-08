package com.soc.game.manager.bedwars;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.OptionalInt;

public class SimpleShopItem extends BaseShopItem {
    private final ItemStack stack;

    public SimpleShopItem(int iron, int gold, int diamonds, int emeralds, ItemStack stack) {
        super(iron, gold, diamonds, emeralds, stack);
        this.stack = stack;
    }

    @Override
    public boolean buy(PlayerEntity player) {
        final OptionalInt slot = super.canAfford(player);
        if (slot.isEmpty()) return false;

        super.costMap.forEach((item, count) -> Inventories.remove(player.getInventory(), predStack -> predStack.isOf(item), count, false));
        player.giveItemStack(this.stack.copy());
        return true;
    }

    @Override
    public Text getTooltipName() {
        return getTooltipNameOfItem(this.stack);
    }
}
