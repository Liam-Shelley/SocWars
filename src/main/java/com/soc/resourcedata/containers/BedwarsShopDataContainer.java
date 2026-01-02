package com.soc.resourcedata.containers;

import com.soc.SocWars;
import com.soc.game.manager.bedwars.ShopItem;
import com.soc.game.manager.bedwars.BedwarsShopCategory;
import com.soc.game.manager.bedwars.BedwarsShopContents;
import com.soc.game.manager.bedwars.SimpleShopItem;
import com.soc.resourcedata.deserialisation.BedwarsShopSlot;
import com.soc.resourcedata.deserialisation.PreSelectionBedwarsShopCategory;
import com.soc.screenhandler.BedwarsShopScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

import java.util.*;
import java.util.stream.Collectors;

public class BedwarsShopDataContainer implements CachedData {

    private BedwarsShopDataContainer() {}

    public static final BedwarsShopDataContainer INSTANCE = new BedwarsShopDataContainer();

    private final Map<Identifier, ShopItem<?>> resourceItemMap = new HashMap<>();
    private final Map<Identifier, PreSelectionBedwarsShopCategory> categoryStockSlotsMap = new TreeMap<>();

    public final void addSlotResource(Identifier id, ShopItem<?> shopItem) {
        this.resourceItemMap.put(id, shopItem);
    }

    public final void addCategorySlot(Identifier id, PreSelectionBedwarsShopCategory stockSlots) {
        this.categoryStockSlotsMap.put(id, stockSlots);
    }

    public final BedwarsShopContents getBedwarsShop(long shopSeed) {
        final Random random = Random.create(shopSeed);

        final List<BedwarsShopCategory> categories = this.categoryStockSlotsMap.entrySet().stream().map(entry -> {
            final BedwarsShopSlot[][] preSelection = entry.getValue().contents();

            final int itemsSize = BedwarsShopScreenHandler.STOCK_WIDTH * BedwarsShopScreenHandler.STOCK_HEIGHT;
            final List<ShopItem<?>> items = new ArrayList<>(itemsSize);
            for (int i = 0; i < itemsSize; i++) {
                items.add(SimpleShopItem.EMPTY);
            }

            for (int i = 0; i < preSelection.length; i++) {
                for (int j = 0; j < preSelection[i].length; j++) {
                    final BedwarsShopSlot shopSlot = preSelection[i][j];
                    final int index = i + j * BedwarsShopScreenHandler.STOCK_WIDTH;

                    if (shopSlot != null) {
                        final List<Identifier> options = shopSlot.options();
                        if (options.isEmpty()) {
                            items.set(index, SimpleShopItem.EMPTY);
                            SocWars.LOGGER.warn("Skipping loading slot at ({}, {}) as the options pool was empty", i, j);
                        } else {
                            final Identifier choice = options.get(random.nextBetween(0, options.size() - 1));
                            final ShopItem<?> item = this.resourceItemMap.get(choice);
                            items.set(index, item == null ? SimpleShopItem.EMPTY : (ShopItem<?>)item.lazyClone());
                        }
                    }
                }
            }

            return new BedwarsShopCategory(
                    items,
                    entry.getValue().icon(),
                    entry.getValue().name()
            );
        }).limit(BedwarsShopScreenHandler.CATEGORIES_WIDTH * BedwarsShopScreenHandler.CATEGORIES_HEIGHT - 1).collect(Collectors.toList());

        categories.addFirst(BedwarsShopCategory.DEFAULT_QUICK_BUY);

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
