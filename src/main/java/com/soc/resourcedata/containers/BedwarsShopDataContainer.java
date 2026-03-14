package com.soc.resourcedata.containers;

import com.soc.SocWars;
import com.soc.game.manager.bedwars.*;
import com.soc.game.manager.bedwars.shopitems.ShopItem;
import com.soc.game.manager.bedwars.shopitems.SimpleShopItem;
import com.soc.game.manager.bedwars.shopitems.TeamShopItem;
import com.soc.resourcedata.deserialisation.BedwarsShopSlot;
import com.soc.resourcedata.deserialisation.PreSelectionBedwarsShopCategory;
import com.soc.screenhandler.BedwarsIndividualShopScreenHandler;
import com.soc.screenhandler.BedwarsTeamShopScreenHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;

import java.util.*;
import java.util.stream.Collectors;

public class BedwarsShopDataContainer implements CachedData {

    private BedwarsShopDataContainer() {}

    public static final BedwarsShopDataContainer INSTANCE = new BedwarsShopDataContainer();

    private final Map<Identifier, ShopItem<?>> resourceItemMap = new HashMap<>();
    private final Map<Identifier, PreSelectionBedwarsShopCategory> categoryStockSlotsMap = new TreeMap<>();
    private PreSelectionBedwarsShopCategory[] teamStockCategories = new PreSelectionBedwarsShopCategory[3];

    public final void addSlotResource(Identifier id, ShopItem<?> shopItem) {
        this.resourceItemMap.put(id, shopItem);
    }

    public final void addCategorySlot(Identifier id, PreSelectionBedwarsShopCategory stockSlots) {
        this.categoryStockSlotsMap.put(id, stockSlots);
    }

    public final void setTeamStockCategory(int index, PreSelectionBedwarsShopCategory stockSlots) {
        this.teamStockCategories[index] = stockSlots;
    }

    public final BedwarsShopContents getIndividualBedwarsShop(long shopSeed, DyeColor team) {
        final Random random = Random.create(shopSeed);
        final List<BedwarsShopCategory> categories = this.categoryStockSlotsMap.entrySet().stream().map(entry -> new BedwarsShopCategory(
                this.resolveItems(entry.getValue(), random, team, BedwarsIndividualShopScreenHandler.STOCK_WIDTH, BedwarsIndividualShopScreenHandler.STOCK_HEIGHT),
                entry.getValue().icon(),
                entry.getValue().name()
        ))
                .limit(BedwarsIndividualShopScreenHandler.CATEGORIES_WIDTH * BedwarsIndividualShopScreenHandler.CATEGORIES_HEIGHT - 1)
                .collect(Collectors.toList());

        categories.addFirst(BedwarsShopCategory.DEFAULT_QUICK_BUY);

        return new BedwarsShopContents(categories);
    }

    public final BedwarsShopContents getTeamBedwarsShop(long shopSeed, DyeColor team) {
        final Random random = Random.create(shopSeed);
        final List<BedwarsShopCategory> categories = new ArrayList<>();
        categories.add(new BedwarsShopCategory(this.resolveItems(this.teamStockCategories[0], random, team, BedwarsTeamShopScreenHandler.STOCK_WIDTH, BedwarsTeamShopScreenHandler.STOCK_HEIGHT), Items.TRIPWIRE_HOOK.getDefaultStack(), Text.of("Traps")));
        categories.add(new BedwarsShopCategory(this.resolveItems(this.teamStockCategories[1], random, team, BedwarsTeamShopScreenHandler.STOCK_WIDTH, BedwarsTeamShopScreenHandler.STOCK_HEIGHT), Items.ANVIL.getDefaultStack(), Text.of("Abilities")));
        categories.add(new BedwarsShopCategory(this.resolveItems(this.teamStockCategories[2], random, team, BedwarsTeamShopScreenHandler.STOCK_WIDTH, BedwarsTeamShopScreenHandler.STOCK_HEIGHT), Items.ARROW.getDefaultStack(), Text.of("Upgrades")));

        return new BedwarsShopContents(categories);
    }

    private List<ShopItem<?>> resolveItems(PreSelectionBedwarsShopCategory preSelectionCategory, Random random, DyeColor team, int width, int height) {
        final BedwarsShopSlot[][] preSelection = preSelectionCategory.contents();
        final int itemsSize = width * height;
        final List<ShopItem<?>> items = new ArrayList<>(itemsSize);
        for (int i = 0; i < itemsSize; i++) {
            items.add(SimpleShopItem.EMPTY);
        }

        for (int i = 0; i < preSelection.length; i++) {
            for (int j = 0; j < preSelection[i].length; j++) {
                final BedwarsShopSlot shopSlot = preSelection[i][j];
                final int index = i + j * width;

                if (shopSlot != null) items.set(index, this.resolveRandomItem(shopSlot, random, team));
            }
        }

        return items;
    }

    private ShopItem<?> resolveRandomItem(BedwarsShopSlot slot, Random random, DyeColor team) {
        final List<Identifier> options = slot.options();
        if (options.isEmpty()) {
            SocWars.LOGGER.warn("Skipping loading slot as the options pool was empty");
            return SimpleShopItem.EMPTY;
        } else {
            final Identifier choice = options.get(random.nextBetween(0, options.size() - 1));
            final ShopItem<?> item = this.resourceItemMap.get(choice);
            final ShopItem<?> lazilyClonedItem = item == null ? SimpleShopItem.EMPTY : (ShopItem<?>)item.lazyClone();
            if (lazilyClonedItem instanceof TeamShopItem teamItem) {
                teamItem.setTeam(team);
            }
            return lazilyClonedItem;
        }
    }

    @Override
    public void cache() {

    }

    @Override
    public void clear() {
        this.resourceItemMap.clear();
        this.categoryStockSlotsMap.clear();
        this.teamStockCategories = new PreSelectionBedwarsShopCategory[3];
    }
}
