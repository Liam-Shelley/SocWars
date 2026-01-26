package com.soc.screenhandler;

import com.soc.game.manager.bedwars.BedwarsShopContents;
import com.soc.game.manager.bedwars.shopitems.ShopItem;
import com.soc.screenhandler.slots.DisplaySlot;
import com.soc.screenhandler.slots.StockSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Items;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.world.World;

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

    public static final int PLAYER_INVENTORY_SLOT_HEIGHT = 102;

    private final Inventory traps;
    private final Inventory abilities;
    private final Inventory trapsDisplay;
    private final Inventory abilitiesDisplay;

    private int[] trapProgressStats = new int[4];

    private BedwarsShopContents shopContents;

    public BedwarsTeamShopScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, playerInventory.player);
        this.addProperties(new ArrayPropertyDelegate(4));
    }

    public BedwarsTeamShopScreenHandler(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        super(ScreenHandlers.BEDWARS_TEAM_SHOP_SCREEN_HANDLER, syncId, playerInventory, player);
        this.traps = new SimpleInventory(TRAPS_SIZE);
        this.abilities = new SimpleInventory(ABILITIES_SIZE);
        this.trapsDisplay = new SimpleInventory(TRAPS_DISPLAY_SIZE);
        this.abilitiesDisplay = new SimpleInventory(ABILITIES_DISPLAY_SIZE);

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

    @SuppressWarnings("ConstantValue")
    private void makeSlots() {
        for (int y = 0; y < TRAPS_HEIGHT; y++) {
            for (int x = 0; x < TRAPS_WIDTH; x++) {
                this.addSlot(new StockSlot(this.traps, x + TRAPS_WIDTH * y, x * 18 + 26, y * 18 + 24, this.player, this));
            }
        }

        for (int y = 0; y < ABILITIES_HEIGHT; y++) {
            for (int x = 0; x < ABILITIES_WIDTH; x++) {
                this.addSlot(new StockSlot(this.abilities, x + ABILITIES_WIDTH * y, x * 18 + 138, y * 18 + 24, this.player, this));
            }
        }

        for (int y = 0; y < TRAPS_DISPLAY_HEIGHT; y++) {
            for (int x = 0; x < TRAPS_DISPLAY_WIDTH; x++) {
                this.addSlot(new DisplaySlot(this.trapsDisplay, x + TRAPS_DISPLAY_WIDTH * y, x * 18 + 26, y * 18 + 74, this.player, this));
            }
        }

        for (int y = 0; y < ABILITIES_DISPLAY_HEIGHT; y++) {
            for (int x = 0; x < ABILITIES_DISPLAY_WIDTH; x++) {
                this.addSlot(new DisplaySlot(this.abilitiesDisplay, x + ABILITIES_DISPLAY_WIDTH * y, x * 18 + 138, y * 18 + 74, this.player, this));
            }
        }

        this.addPlayerSlots(this.playerInventory, 48, this.getPlayerInventorySlotHeight());

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

    public boolean hasRoomInTrapDisplay() {
        return this.trapsDisplay.getStack(TRAPS_DISPLAY_SIZE - 1).isEmpty();
    }

    public boolean hasRoomInAbilityDisplay() {
        return this.abilitiesDisplay.getStack(ABILITIES_DISPLAY_SIZE - 1).isEmpty();
    }
}
