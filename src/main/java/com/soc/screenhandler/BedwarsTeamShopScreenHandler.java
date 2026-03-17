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
import net.minecraft.world.World;

public class BedwarsTeamShopScreenHandler extends AbstractCategoriesShopScreenHandler {
    public static final int STOCK_WIDTH = 6;
    public static final int STOCK_HEIGHT = 2;
    public static final int STOCK_SIZE = STOCK_WIDTH * STOCK_HEIGHT;
    public static final int DISPLAY_WIDTH = 5;
    public static final int DISPLAY_HEIGHT = 1;
    public static final int DISPLAY_SIZE = DISPLAY_WIDTH * DISPLAY_HEIGHT;
    public static final int CATEGORIES_WIDTH = 1;
    public static final int CATEGORIES_HEIGHT = 3;

    private final Inventory stock;
    private final Inventory display;
    private final Inventory displayOffset;

    private long nextTrapTime;
    private int trapDuration;
    private long nextAbilityTime;
    private int abilityDuration;

    private BedwarsShopCategory displayCategory;

    public BedwarsTeamShopScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, playerInventory.player);
    }

    public BedwarsTeamShopScreenHandler(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        super(ScreenHandlers.BEDWARS_TEAM_SHOP_SCREEN_HANDLER, syncId, playerInventory, player);
        this.stock = new SimpleInventory(STOCK_SIZE);
        this.display = new SimpleInventory(DISPLAY_SIZE);
        this.displayOffset = new SimpleInventory(DISPLAY_SIZE - 1);

        this.shopContents = super.manager == null ? null : super.manager.getTeamShopContents(player.getUuid());
        this.currentCategory = this.shopContents == null ? null : this.shopContents.getFirstCategory();

        this.makeSlots();
    }

    @Override
    protected Inventory createCategoriesInventory() {
        return new SimpleInventory(CATEGORIES_WIDTH * CATEGORIES_HEIGHT);
    }

    @SuppressWarnings("ConstantValue")
    private void makeSlots() {
        for (int y = 0; y < STOCK_HEIGHT; y++) {
            for (int x = 0; x < STOCK_WIDTH; x++) {
                this.addSlot(new StockSlot(this.stock, x + STOCK_WIDTH * y, x * 18 + 88, y * 18 + 24, this.player, this));
            }
        }

        for (int y = 0; y < DISPLAY_HEIGHT; y++) {
            for (int x = 0; x < DISPLAY_WIDTH; x++) {
                this.addSlot(new DisplaySlot(this.display, x + DISPLAY_WIDTH * y, x * 18 + 97, y * 18 + 74, this.player, this));
            }
        }
        for (int y = 0; y < DISPLAY_HEIGHT; y++) {
            for (int x = 0; x < DISPLAY_WIDTH - 1; x++) {
                this.addSlot(new DisplaySlot(this.displayOffset, x + DISPLAY_WIDTH * y, x * 18 + 106, y * 18 + 74, this.player, this));
            }
        }

        for (int y = 0; y < CATEGORIES_HEIGHT; y++) {
            for (int x = 0; x < CATEGORIES_WIDTH; x++) {
                this.addSlot(new CategorySlot(this.categories, x + CATEGORIES_WIDTH * y, x * 18 + 66, y * 18 + 24, this.player, this));
            }
        }

        this.addPlayerSlots(this.playerInventory, 48, this.getPlayerInventorySlotHeight());

        this.refreshItems();
        this.refreshCategories();
    }

    @Override
    public void refreshItems() {
        for (int i = 0; i < this.stock.size(); i++) {
            this.stock.setStack(i, this.getShopItem(i).getIcon());
        }

        //I should make this whole thing more elegant; maybe even make a StaggeredInventory class or something and some method to automatically build out the slots
        final int stacksInDisplay = this.getStacksInDisplay();
        final int stackOffset = 2 - (stacksInDisplay >> 1);

        if (stacksInDisplay % 2 == 1) {
            for (int i = 0; i < this.display.size() - stackOffset; i++) {
                this.display.setStack(i + stackOffset, this.getDisplayItem(i).getIcon());
            }
            this.displayOffset.clear();
        } else {
            for (int i = 0; i < this.displayOffset.size() - stackOffset; i++) {
                this.displayOffset.setStack(i + stackOffset, this.getDisplayItem(i).getIcon());
            }
            this.display.clear();
        }
    }

    private int getStacksInDisplay() {
        if (this.displayCategory == null) return 0;
        for (int i = 0; i < this.displayCategory.getItems().size(); i++) {
            if (this.displayCategory.getShopItem(i).getIcon().isEmpty()) return i;
        }
        return this.displayCategory.getItems().size();
    }

    public ShopItem<?> getShopItem(final int slot) {
        return this.currentCategory == null ? SimpleShopItem.EMPTY : this.currentCategory.getShopItem(slot);
    }

    public ShopItem<?> getDisplayItem(final int slot) {
        return this.displayCategory == null ? SimpleShopItem.EMPTY : this.displayCategory.getShopItem(slot);
    }

    @Override
    public void setShopContents(BedwarsShopContents shopContents) {
        this.shopContents = shopContents;
        this.refreshItems();
        this.refreshCategories();
    }

    public float getTrapProgress(World world) {
        return this.nextTrapTime < world.getTime() ? 1f : (float)(world.getTime() - this.nextTrapTime) / this.trapDuration + 1f;
    }

    public float getAbilityProgress(World world) {
        return this.nextAbilityTime < world.getTime() ? 1f : (float)(world.getTime() - this.nextAbilityTime) / this.abilityDuration + 1f;
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

    public void useTrap(long nextTime, int duration) {
        this.shiftCategory(3);
        if (this.currentCategory == this.shopContents.getCategory(0)) this.refreshItems();

        this.nextTrapTime = nextTime;
        this.trapDuration = duration;
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
        final BedwarsShopCategory trapsCategory = this.shopContents.getCategory(3);
        trapsCategory.forEachEnumerate((i, item) -> {
            if (!(item instanceof DisplayShopItem displayItem && !displayItem.isEmpty())) {
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
            if (!(item instanceof DisplayShopItem displayItem && !displayItem.isEmpty())) {
                trapsCategory.setShopItem(i, trapShopItem.getDisplayCopy());
                return false;
            }
            return true;
        });
    }

    @Override
    public void setCurrentCategory(int slot) {
        if (slot < 2) {
            this.displayCategory = this.shopContents.getCategory(slot + 3);
        } else {
            this.displayCategory = null;
        }
        super.setCurrentCategory(slot);
    }
}
