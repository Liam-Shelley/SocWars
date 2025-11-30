package com.soc.game.manager;

import com.google.common.collect.Lists;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import java.util.List;

public class BedwarsShopContents {
    public static final int MAX_CATEGORIES = 8;

    private final List<BedwarsShopCategory> contents;

    public BedwarsShopContents() {
        this.contents = Lists.newArrayList(new BedwarsShopCategory(Items.NETHER_STAR.getDefaultStack(), Text.translatable("shop.category.quick_buy")), new BedwarsShopCategory(Items.GREEN_WOOL.getDefaultStack(), Text.translatable("shop.category.blocks")));
    }

    public BedwarsShopCategory getCategory(int slot) {
        if (slot < 0 || slot >= contents.size()) return null;
        return this.contents.get(slot);
    }

    public BedwarsShopCategory getFirstCategory() {
        if (this.contents.isEmpty()) return null;
        return this.contents.getFirst();
    }

    public List<ItemStack> getCategoryIcons() {
        return this.contents.stream().map(BedwarsShopCategory::getIcon).toList();
    }

    public int getNumCategories() {
        return this.contents.size();
    }
}
