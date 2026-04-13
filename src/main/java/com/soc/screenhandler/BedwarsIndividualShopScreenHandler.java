package com.soc.screenhandler;

import com.soc.game.manager.bedwars.BedwarsShopContents;
import com.soc.game.manager.bedwars.shopitems.ShopItem;
import com.soc.game.manager.bedwars.shopitems.SimpleShopItem;
import com.soc.screenhandler.slots.CategorySlot;
import com.soc.screenhandler.slots.DisplaySlot;
import com.soc.screenhandler.slots.StockSlot;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;

public class BedwarsIndividualShopScreenHandler extends AbstractCategoriesShopScreenHandler {
    public static final int STOCK_WIDTH = 8;
    public static final int STOCK_HEIGHT = 3;
    public static final int CATEGORIES_WIDTH = 3;
    public static final int CATEGORIES_HEIGHT = 3;

    private final PlayerEntity player;
    private final PlayerInventory playerInventory;
    private final Inventory stock;
    private final Inventory armourDisplay;

    public BedwarsIndividualShopScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, playerInventory.player);
    }

    public BedwarsIndividualShopScreenHandler(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        super(ScreenHandlers.BEDWARS_INDIVIDUAL_SHOP_SCREEN_HANDLER, syncId, playerInventory, player);
        this.player = player;
        this.playerInventory = playerInventory;
        this.stock = new SimpleInventory(STOCK_WIDTH * STOCK_HEIGHT);
        this.armourDisplay = new SimpleInventory(4);

        this.shopContents = super.manager == null ? null : super.manager.getIndividualShopContents(player.getUuid());
        this.currentCategory = this.shopContents == null ? null : this.shopContents.getFirstCategory();

        this.makeSlots();
    }

    @Override
    protected Inventory createCategoriesInventory() {
        return new SimpleInventory(CATEGORIES_WIDTH * CATEGORIES_HEIGHT);
    }

    private void makeSlots() {
        for (int y = 0; y < STOCK_HEIGHT; y++) {
            for (int x = 0; x < STOCK_WIDTH; x++) {
                this.addSlot(new StockSlot(this.stock, x + STOCK_WIDTH * y, x * 18 + 66, y * 18 + 18, this.player, this));
            }
        }

        for (int y = 0; y < CATEGORIES_HEIGHT; y++) {
            for (int x = 0; x < CATEGORIES_WIDTH; x++) {
                this.addSlot(new CategorySlot(this.categories, x + CATEGORIES_WIDTH * y, x * 18 + 8, y * 18 + 18, this.player, this));
            }
        }

        this.addPlayerSlots(this.playerInventory, 48, this.getPlayerInventorySlotHeight());

        for (int y = 0; y < 4; y++) {
            this.addSlot(new DisplaySlot(this.armourDisplay, y, 214, y * -18 + 142, this.player, this));
        }

        this.refreshItems();
        this.refreshCategories();
    }

    public void refreshItems() {
        for (int i = 0; i < this.stock.size(); i++) {
            this.stock.setStack(i, this.getShopItem(i).getIcon());
        }
        for (int i = 0; i < this.armourDisplay.size(); i++) {
            this.armourDisplay.setStack(i, this.player.getEquippedStack(EquipmentSlot.values()[i + 2]));
        }
    }

    public ShopItem<?> getShopItem(int slot) {
        return this.currentCategory == null ? SimpleShopItem.EMPTY : this.currentCategory.getShopItem(slot);
    }

    @Override
    public void setShopContents(BedwarsShopContents shopContents) {
        this.shopContents = shopContents;
        this.refreshItems();
        this.refreshCategories();
    }

    @Override
    public int getPlayerInventorySlotHeight() {
        return 86;
    }
}
