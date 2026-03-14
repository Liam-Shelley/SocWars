package com.soc.screenhandler;

import com.soc.game.manager.bedwars.BedwarsShopCategory;
import com.soc.game.manager.bedwars.BedwarsShopContents;
import com.soc.game.manager.bedwars.shopitems.DisplayShopItem;
import com.soc.game.manager.bedwars.shopitems.ShopItem;
import com.soc.game.manager.bedwars.shopitems.SimpleShopItem;
import com.soc.game.manager.bedwars.shopitems.TrapShopItem;
import com.soc.screenhandler.slots.CategorySlot;
import com.soc.screenhandler.slots.DisplaySlot;
import com.soc.screenhandler.slots.StockSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.world.World;

public class BedwarsTeamShopScreenHandler extends AbstractCategoriesShopScreenHandler {

    public static final int STOCK_WIDTH = 5;
    public static final int STOCK_HEIGHT = 2;
    public static final int STOCK_SIZE = STOCK_WIDTH * STOCK_HEIGHT;
    public static final int DISPLAY_WIDTH = 5;
    public static final int DISPLAY_HEIGHT = 1;
    public static final int DISPLAY_SIZE = DISPLAY_WIDTH * DISPLAY_HEIGHT;
    public static final int CATEGORIES_WIDTH = 1;
    public static final int CATEGORIES_HEIGHT = 3;
    private static final int[] CATEGORY_SIZES = {STOCK_SIZE, DISPLAY_SIZE};

    private final Inventory stock;
    private final Inventory display;

    private int[] trapProgressStats = new int[4];

    public BedwarsTeamShopScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, playerInventory.player);
        this.addProperties(new ArrayPropertyDelegate(4));
    }

    public BedwarsTeamShopScreenHandler(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        super(ScreenHandlers.BEDWARS_TEAM_SHOP_SCREEN_HANDLER, syncId, playerInventory, player);
        this.stock = new SimpleInventory(STOCK_SIZE);
        this.display = new SimpleInventory(DISPLAY_SIZE);

        this.shopContents = super.manager == null ? null : super.manager.getTeamShopContents(player.getUuid());

        this.makeSlots();

        final int[] trapProgressStats = super.manager == null ? new int[4] : super.manager.getTrapProgressStats(player.getUuid());
        this.addProperties(new PropertyDelegate() {
            @Override
            public int get(int index) {
                return trapProgressStats[index];
            }

            @Override
            public void set(int index, int value) {
                BedwarsTeamShopScreenHandler.this.trapProgressStats[index] = value;
            }

            @Override
            public int size() {
                return 4;
            }
        });
    }

    @Override
    protected Inventory createCategoriesInventory() {
        return new SimpleInventory(CATEGORIES_WIDTH * CATEGORIES_HEIGHT);
    }

    @SuppressWarnings("ConstantValue")
    private void makeSlots() {
        for (int y = 0; y < STOCK_HEIGHT; y++) {
            for (int x = 0; x < STOCK_WIDTH; x++) {
                this.addSlot(new StockSlot(this.stock, x + STOCK_WIDTH * y, x * 18 + 76, y * 18 + 24, this.player, this));
            }
        }

        for (int y = 0; y < DISPLAY_HEIGHT; y++) {
            for (int x = 0; x < DISPLAY_WIDTH; x++) {
                this.addSlot(new DisplaySlot(this.display, x + DISPLAY_WIDTH * y, x * 18 + 76, y * 18 + 74, this.player, this));
            }
        }

        for (int y = 0; y < CATEGORIES_HEIGHT; y++) {
            for (int x = 0; x < CATEGORIES_WIDTH; x++) {
                this.addSlot(new CategorySlot(this.categories, x + CATEGORIES_WIDTH * y, x * 18 + 26, y * 18 + 24, this.player, this));
            }
        }

        this.addPlayerSlots(this.playerInventory, 48, this.getPlayerInventorySlotHeight());

        this.refreshItems();
        this.refreshCategories();
    }

    @Override
    public void refreshItems() {
        int i = 0;
        for (int j = 0; j < this.stock.size(); j++, i++) {
            this.stock.setStack(j, this.getShopItem(i).getIcon());
        }
        for (int j = 0; j < this.display.size(); j++, i++) {
            this.display.setStack(j, this.getShopItem(i).getIcon());
        }
    }

    public ShopItem<?> getShopItem(final int slot) {
        if (this.shopContents == null) return SimpleShopItem.EMPTY;

        int slotMut = slot;

        for (int i = 0; i < CATEGORY_SIZES.length; i++) {
            if (slotMut >= CATEGORY_SIZES[i]) {
                slotMut -= CATEGORY_SIZES[i];
                continue;
            }
            return this.shopContents.getCategory(i).getShopItem(slotMut);
        }

        throw new IndexOutOfBoundsException(slot);
    }

    @Override
    public void setShopContents(BedwarsShopContents shopContents) {
        this.shopContents = shopContents;
        this.refreshItems();
        this.refreshCategories();
    }

    public float getTrapProgress(World world) {
        return this.trapProgressStats[0] < world.getTime() ? 1f : (float)(this.trapProgressStats[0] - world.getTime()) / this.trapProgressStats[1];
    }

    public float getAbilityProgress(World world) {
        return this.trapProgressStats[2] < world.getTime() ? 1f : (float)(this.trapProgressStats[2] - world.getTime()) / this.trapProgressStats[3];
    }

    @Override
    public int getPlayerInventorySlotHeight() {
        return 110;
    }

    @SuppressWarnings("DataFlowIssue")
    public boolean hasRoomInTraps() {
        return this.shopContents.getCategory(3).hasEmptySlot();
    }

    @SuppressWarnings("DataFlowIssue")
    public boolean hasRoomInAbilities() {
        return this.shopContents.getCategory(4).hasEmptySlot();
    }

    public void useTrap() {
        this.shiftCategory(3);
        if (this.currentCategory == this.shopContents.getCategory(0)) this.refreshItems();
    }

    public void useAbility() {
        this.shiftCategory(4);
        if (this.currentCategory == this.shopContents.getCategory(1)) this.refreshItems();
    }

    @SuppressWarnings("DataFlowIssue") //I know that this will be valid because it can only ever be invalid before the shop has loaded, in which case you cannot buy a trap
    private void shiftCategory(int index) {
        final BedwarsShopCategory category = this.shopContents.getCategory(index);
        category.forEachEnumerate((i, item) -> {
            category.setShopItem(i - 1, item);
        });
    }

    @SuppressWarnings("DataFlowIssue")
    public void onBuyTrap(TrapShopItem trapShopItem) {
        final BedwarsShopCategory trapsCategory = this.shopContents.getCategory(2);
        trapsCategory.forEachEnumerate((i, item) -> {
            if (!(item instanceof DisplayShopItem display && !display.isEmpty())) {
                trapsCategory.setShopItem(i, trapShopItem.getDisplayCopy());
                return false;
            }
            return true;
        });

        this.refreshItems();
    }

    @SuppressWarnings("DataFlowIssue")
    private void refreshCategory(TrapShopItem trapShopItem) {
        final BedwarsShopCategory trapsCategory = this.shopContents.getCategory(2);
        trapsCategory.forEachEnumerate((i, item) -> {
            if (!(item instanceof DisplayShopItem display && !display.isEmpty())) {
                trapsCategory.setShopItem(i, trapShopItem.getDisplayCopy());
                return false;
            }
            return true;
        });
    }

    @Override
    @SuppressWarnings("DataFlowIssue")
    public void setCurrentCategory(int slot) {
        super.setCurrentCategory(slot);
        if (slot < 2) {
            this.shopContents.getCategory(slot + 3).forEachEnumerate((i, shopItem) -> {
                this.display.setStack(i, shopItem.getIcon());
            });
        } else {
            this.display.clear();
        }
    }
}
