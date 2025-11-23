package com.soc.screenhandler;

import com.soc.items.components.ModComponents;
import com.soc.items.components.ShopCostComponent;
import com.soc.screenhandler.slots.PageSlot;
import com.soc.screenhandler.slots.StockSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Stream;

public class BedwarsShopScreenHandler extends ScreenHandler {
    private static final int STOCK_WIDTH = 9;
    private static final int STOCK_HEIGHT = 4;
    private static final int CATEGORIES_WIDTH = 2;
    private static final int CATEGORIES_HEIGHT = 4;

    private final PlayerEntity player;
    private final PlayerInventory playerInventory;
    private final Inventory stock;
    private final Inventory categories;

    public BedwarsShopScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, playerInventory.player);
    }

    public BedwarsShopScreenHandler(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        super(ScreenHandlers.BEDWARS_SHOP_SCREEN_HANDLER, syncId);
        this.player = player;
        this.playerInventory = playerInventory;
        this.stock = new SimpleInventory(STOCK_WIDTH * STOCK_HEIGHT);
        this.categories = new SimpleInventory(CATEGORIES_WIDTH * CATEGORIES_HEIGHT);

        this.makeSlots();
    }

    private void makeSlots() {
        for (int x = 0; x < STOCK_WIDTH; x++) {
            for (int y = 0; y < STOCK_HEIGHT; y++) {
                this.addSlot(new StockSlot(this.stock, x + STOCK_WIDTH * y, x * 18 + 48, y * 18 + 18, this.player, this));
            }
        }

        for (int x = 0; x < CATEGORIES_WIDTH; x++) {
            for (int y = 0; y < CATEGORIES_HEIGHT; y++) {
                this.addSlot(new PageSlot(this.categories, x + CATEGORIES_WIDTH * y, x * 18 + 8, y * 18 + 18, this.player, this));
            }
        }

        this.addPlayerSlots(this.playerInventory, 48, 104);

        for (int i = 0; i < this.stock.size(); i++) {
            final ItemStack stack = new ItemStack(Items.WHITE_WOOL, i + 1);
            stack.set(ModComponents.SHOP_COST_COMPONENT, new ShopCostComponent(1, 1, 1, 1));
            this.stock.setStack(i, stack);
        }

        for (int i = 0; i < this.categories.size();) {
            this.categories.setStack(i, new ItemStack(Items.EMERALD, ++i));
        }
    }

    public boolean buyItem(ItemStack stack) {
        final ShopCostComponent costComponent = stack.get(ModComponents.SHOP_COST_COMPONENT);
        if (costComponent == null) return false;

        final OptionalInt slot = this.canAffordItem(costComponent);
        if (slot.isEmpty()) return false;

        costComponent.getCosts().forEach(pair -> Inventories.remove(BedwarsShopScreenHandler.this.playerInventory, predStack -> predStack.isOf(pair.getLeft()), pair.getRight(), false));
        this.player.giveItemStack(stack.copy());
        return true;
    }

    private OptionalInt canAffordItem(ShopCostComponent costComponent) {
        final boolean canAfford = costComponent.getCosts().stream().map(pair -> BedwarsShopScreenHandler.this.playerInventory.count(pair.getLeft()) >= pair.getRight()).reduce(true, (a, b) -> a && b);
        final int emptySlot = this.playerInventory.getEmptySlot();

        return canAfford && emptySlot >= 0 ? OptionalInt.of(emptySlot) : OptionalInt.empty();
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
