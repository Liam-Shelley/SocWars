package com.soc.screenhandler;

import com.soc.SocWars;
import com.soc.game.manager.BedwarsGameManager;
import com.soc.game.manager.BedwarsShopCategory;
import com.soc.game.manager.BedwarsShopContents;
import com.soc.game.manager.bedwarsshopitem.BaseShopItem;
import com.soc.screenhandler.slots.CategorySlot;
import com.soc.screenhandler.slots.StockSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class BedwarsShopScreenHandler extends ScreenHandler {
    private static final int STOCK_WIDTH = 9;
    private static final int STOCK_HEIGHT = 4;
    private static final int CATEGORIES_WIDTH = 2;
    private static final int CATEGORIES_HEIGHT = 4;

    private final PlayerEntity player;
    private final PlayerInventory playerInventory;
    private final Inventory stock;
    private final Inventory categories;

    private final BedwarsGameManager manager;
    private final BedwarsShopContents shopContents;
    private BedwarsShopCategory currentCategory;

    public BedwarsShopScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, playerInventory.player);
    }

    public BedwarsShopScreenHandler(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        super(ScreenHandlers.BEDWARS_SHOP_SCREEN_HANDLER, syncId);
        this.player = player;
        this.playerInventory = playerInventory;
        this.stock = new SimpleInventory(STOCK_WIDTH * STOCK_HEIGHT);
        this.categories = new SimpleInventory(CATEGORIES_WIDTH * CATEGORIES_HEIGHT);

        this.manager = BedwarsGameManager.getBedwarsGameManager(player);
        this.shopContents = this.manager == null ? new BedwarsShopContents() : this.manager.getShopContents();
        this.currentCategory = this.shopContents.getFirstCategory();

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
                this.addSlot(new CategorySlot(this.categories, x + CATEGORIES_WIDTH * y, x * 18 + 8, y * 18 + 18, this.player, this));
            }
        }

        this.addPlayerSlots(this.playerInventory, 48, 104);

        for (int i = 0; i < this.stock.size(); i++) {
            this.stock.setStack(i, this.getShopItem(i).getIcon());
        }

        for (int i = 0; i < this.categories.size(); i++) {
            this.categories.setStack(i, this.getCategoryIcon(i));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.manager != null || true;
    }

    public final BedwarsGameManager getManager() {
        return this.manager;
    }

    public final BedwarsShopContents getShopContents() {
        return this.shopContents;
    }

    public boolean runInManager(Consumer<BedwarsGameManager> consumer) {
        if (this.manager == null) return false;
        consumer.accept(this.manager);
        return true;
    }

    public void setCurrentCategory(int slot) {
        if (slot < 0 || slot >= this.shopContents.getNumCategories()) return;
        this.currentCategory = this.shopContents.getCategory(slot);

        for (int i = 0; i < this.stock.size(); i++) {
            this.stock.setStack(i, this.getShopItem(i).getIcon());
        }
    }

    public BedwarsShopCategory getCurrentCategory() {
        return this.currentCategory;
    }

    public BaseShopItem getShopItem(Slot slot) {
        return this.getShopItem(slot.getIndex());
    }

    public BaseShopItem getShopItem(int slot) {
        return this.currentCategory.getShopItem(slot);
    }

    public ItemStack getCategoryIcon(int slot) {
        if (slot < 0 || slot >= this.shopContents.getNumCategories()) return Items.AIR.getDefaultStack();
        return this.shopContents.getCategoryIcons().get(slot);
    }

    public BedwarsShopCategory getShopCategory(Slot slot) {
        return this.getShopCategory(slot.getIndex());
    }

    public BedwarsShopCategory getShopCategory(int slot) {
        return this.shopContents.getCategory(slot);
    }

    public boolean isStock(Slot slot) {
        return slot.inventory == this.stock;
    }

    public boolean isCategory(Slot slot) {
        return slot.inventory == this.categories;
    }
}
