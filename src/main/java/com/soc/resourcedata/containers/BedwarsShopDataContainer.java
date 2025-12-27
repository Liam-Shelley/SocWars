package com.soc.resourcedata.containers;

import com.soc.game.manager.bedwars.BaseShopItem;
import com.soc.game.manager.bedwars.BedwarsShopCategory;
import com.soc.game.manager.bedwars.BedwarsShopContents;
import com.soc.resourcedata.deserialisation.PreSelectionBedwarsShopCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

import java.util.HashMap;
import java.util.Map;

public class BedwarsShopDataContainer implements CachedData {

    private BedwarsShopDataContainer() {}

    public static final BedwarsShopDataContainer INSTANCE = new BedwarsShopDataContainer();

    private final Map<Identifier, BaseShopItem> resourceItemMap = new HashMap<>();
    private final Map<Identifier, PreSelectionBedwarsShopCategory> categoryStockSlotsMap = new HashMap<>();

    public final void addSlotResource(Identifier id, BaseShopItem shopItem) {
        this.resourceItemMap.put(id, shopItem);
    }

    public final void addCategorySlot(Identifier id, PreSelectionBedwarsShopCategory stockSlots) {
        this.categoryStockSlotsMap.put(id, stockSlots);
    }

    public final BedwarsShopContents getBedwarsShop(Random random) {
        this.resourceItemMap.entrySet().stream().limit(7).map(entry -> new BedwarsShopCategory(
                null,
                ItemStack.EMPTY,
                Text.of("")
        ));

        return new BedwarsShopContents();
    }

    @Override
    public void cache() {

    }

    @Override
    public void clear() {
        this.resourceItemMap.clear();
        this.categoryStockSlotsMap.clear();
    }
}
