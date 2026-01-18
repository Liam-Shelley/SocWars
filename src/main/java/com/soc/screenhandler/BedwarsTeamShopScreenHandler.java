package com.soc.screenhandler;

import com.soc.game.manager.bedwars.BedwarsShopContents;
import com.soc.game.manager.bedwars.ShopItem;
import com.soc.game.manager.bedwars.SimpleShopItem;
import com.soc.networking.s2c.bedwars.BedwarsShopDataPayload;
import com.soc.screenhandler.slots.StockSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;

public class BedwarsTeamShopScreenHandler extends AbstractShopScreenHandler {
    public static final int TRAPS_WIDTH = 5;
    public static final int TRAPS_HEIGHT = 2;
    public static final int TRAPS_SIZE = TRAPS_WIDTH * TRAPS_HEIGHT;
    public static final int ABILITIES_WIDTH = 3;
    public static final int ABILITIES_HEIGHT = 2;
    public static final int ABILITIES_SIZE = ABILITIES_WIDTH * ABILITIES_HEIGHT;
    public static final int TRAPS_DISPLAY_WIDTH = 5;
    public static final int TRAPS_DISPLAY_HEIGHT = 1;
    public static final int TRAPS_DISPLAY_SIZE = TRAPS_DISPLAY_WIDTH * TRAPS_DISPLAY_HEIGHT;
    public static final int ABILITIES_DISPLAY_WIDTH = 3;
    public static final int ABILITIES_DISPLAY_HEIGHT = 1;
    public static final int ABILITIES_DISPLAY_SIZE = ABILITIES_DISPLAY_WIDTH * ABILITIES_DISPLAY_HEIGHT;
    private static final int[] CATEGORY_SIZES = {TRAPS_SIZE, ABILITIES_SIZE, TRAPS_DISPLAY_SIZE, ABILITIES_DISPLAY_SIZE};
    private final Inventory traps;
    private final Inventory abilities;
    private final Inventory trapsDisplay;
    private final Inventory abilitiesDisplay;

    private BedwarsShopContents shopContents;

    public BedwarsTeamShopScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, playerInventory.player);
    }

    public BedwarsTeamShopScreenHandler(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        super(ScreenHandlers.BEDWARS_INDIVIDUAL_SHOP_SCREEN_HANDLER, syncId, playerInventory, player);
        this.traps = new SimpleInventory(TRAPS_SIZE);
        this.abilities = new SimpleInventory(ABILITIES_SIZE);
        this.trapsDisplay = new SimpleInventory(TRAPS_DISPLAY_SIZE);
        this.abilitiesDisplay = new SimpleInventory(ABILITIES_DISPLAY_SIZE);

        this.shopContents = super.manager == null ? null : super.manager.getTeamShopContents(player.getUuid());

        this.makeSlots();
    }

    @SuppressWarnings("ConstantValue")
    private void makeSlots() {
        for (int x = 0; x < TRAPS_WIDTH; x++) {
            for (int y = 0; y < TRAPS_HEIGHT; y++) {
                this.addSlot(new StockSlot(this.traps, x + TRAPS_WIDTH * y, x * 18 + 66, y * 18 + 18, this.player, this));
            }
        }

        for (int x = 0; x < ABILITIES_WIDTH; x++) {
            for (int y = 0; y < ABILITIES_HEIGHT; y++) {
                this.addSlot(new StockSlot(this.abilities, x + ABILITIES_WIDTH * y, x * 18 + 8, y * 18 - 36, this.player, this));
            }
        }

        for (int x = 0; x < TRAPS_DISPLAY_WIDTH; x++) {
            for (int y = 0; y < TRAPS_DISPLAY_HEIGHT; y++) {
                this.addSlot(new StockSlot(this.trapsDisplay, x + TRAPS_DISPLAY_WIDTH * y, x * 18 + 66, y * 18 - 36, this.player, this));
            }
        }

        for (int x = 0; x < ABILITIES_DISPLAY_WIDTH; x++) {
            for (int y = 0; y < ABILITIES_DISPLAY_HEIGHT; y++) {
                this.addSlot(new StockSlot(this.abilitiesDisplay, x + ABILITIES_DISPLAY_WIDTH * y, x * 18 + 8, y * 18 - 36, this.player, this));
            }
        }

        this.addPlayerSlots(this.playerInventory, 48, 86);

        this.refreshItems();
    }

    @Override
    public void refreshItems() {
        int i = 0;
        for (int j = 0; j < this.traps.size(); j++, i++) {
            this.traps.setStack(j, this.getShopItem(i).getIcon());
        }
        for (int j = 0; j < this.abilities.size(); j++, i++) {
            this.abilities.setStack(j, this.getShopItem(i).getIcon());
        }
        for (int j = 0; j < this.trapsDisplay.size(); j++, i++) {
            this.trapsDisplay.setStack(j, this.getShopItem(i).getIcon());
        }
        for (int j = 0; j < this.abilitiesDisplay.size(); j++, i++) {
            this.abilitiesDisplay.setStack(j, this.getShopItem(i).getIcon());
        }
    }

    public ShopItem<?> getShopItem(final int slot) {
        int slotMut = slot;

        for (int i = 0; i < CATEGORY_SIZES.length; i++) {
            if (i > 0) slotMut -= CATEGORY_SIZES[i - 1];
            return this.shopContents.getCategory(i).getShopItem(slotMut);
        }

        throw new IndexOutOfBoundsException(slot);
    }

    public void setShopContents(BedwarsShopDataPayload payload) {
        this.shopContents = payload.shopContents();
        this.refreshItems();
    }
}
