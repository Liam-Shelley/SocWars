package com.soc.game.manager;

import com.soc.game.manager.bedwarsshopitem.BaseShopItem;
import com.soc.game.manager.bedwarsshopitem.SimpleShopItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.util.List;

public class BedwarsShopCategory {
    public static final int MAX_ITEMS = 36;

    private final List<BaseShopItem> items;
    private final ItemStack icon;
    private final Text name;

    public BedwarsShopCategory(ItemStack icon, Text name) {
        this.items = List.of(new SimpleShopItem(1, 11, 21, 101, new ItemStack(icon.getItem(), 3)));
        this.icon = icon.copyWithCount(1);
        this.name = name;
    }

    public BaseShopItem getShopItem(int slot) {
        if (slot < 0 || slot >= this.items.size()) return BaseShopItem.EMPTY;
        return this.items.get(slot);
    }

    public ItemStack getIcon() {
        return this.icon;
    }

    public Text getName() {
        return this.name;
    }
}
