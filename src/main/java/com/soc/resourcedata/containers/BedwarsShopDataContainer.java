package com.soc.resourcedata.containers;

import com.soc.game.manager.bedwars.BaseShopItem;
import com.soc.game.manager.bedwars.BedwarsShopCategory;
import com.soc.game.manager.bedwars.BedwarsShopContents;
import com.soc.resourcedata.deserialisation.BedwarsShopSlot;
import com.soc.resourcedata.deserialisation.PreSelectionBedwarsShopCategory;
import com.soc.screenhandler.BedwarsShopScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

import java.util.*;

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
        final List<BedwarsShopCategory> categories = this.categoryStockSlotsMap.entrySet().stream().limit(7).map(entry -> {
            final BedwarsShopSlot[][] preSelection = entry.getValue().contents();

            final int itemsSize = BedwarsShopScreenHandler.STOCK_WIDTH * BedwarsShopScreenHandler.STOCK_HEIGHT;
            final List<BaseShopItem> items = new ArrayList<>(itemsSize);
            for (int i = 0; i < itemsSize; i++) {
                items.add(null);
            }

            for (int i = 0; i < preSelection.length; i++) {
                for (int j = 0; j < preSelection[i].length; j++) {
                    final BedwarsShopSlot shopSlot = preSelection[i][j];
                    final int index = i * BedwarsShopScreenHandler.STOCK_HEIGHT + j;

                    if (shopSlot == null) {
                        items.set(index, null);
                    } else {
                        final List<Identifier> options = shopSlot.options();
                        final Identifier choice = options.get(random.nextBetween(0, options.size() - 1));
                        final BaseShopItem item = this.resourceItemMap.get(choice);
                        items.set(index, item == null ? BaseShopItem.EMPTY : item);
                    }
                }
            }

            return new BedwarsShopCategory(
                    items,
                    entry.getValue().icon(),
                    entry.getValue().name()
            );
        }).toList();

        return new BedwarsShopContents(categories);
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
