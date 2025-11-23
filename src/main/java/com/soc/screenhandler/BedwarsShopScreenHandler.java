package com.soc.screenhandler;

import com.soc.SocWars;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;

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
                this.addSlot(new Slot(this.stock, x + STOCK_WIDTH * y, x * 18 + 48, y * 18 + 18) {
                    @Override
                    public boolean canInsert(ItemStack stack) {
                        return false;
                    }

                    @Override
                    public ItemStack takeStack(int amount) {
                        BedwarsShopScreenHandler.this.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_XYLOPHONE.value());

                        ItemStack stack = super.getStack();
                        SocWars.LOGGER.info(stack.toString());
                        return ItemStack.EMPTY;
                    }
                });
            }
        }

        for (int x = 0; x < CATEGORIES_WIDTH; x++) {
            for (int y = 0; y < CATEGORIES_HEIGHT; y++) {
                this.addSlot(new Slot(this.categories, x + CATEGORIES_WIDTH * y, x * 18 + 8, y * 18 + 18) {
                    @Override
                    public boolean canInsert(ItemStack stack) {
                        return false;
                    }

                    @Override
                    public ItemStack takeStack(int amount) {
                        BedwarsShopScreenHandler.this.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BELL.value());

                        ItemStack stack = super.getStack();
                        SocWars.LOGGER.info(stack.toString());
                        return ItemStack.EMPTY;
                    }
                });
            }
        }

        this.addPlayerSlots(this.playerInventory, 48, 104);

        for (int i = 0; i < this.stock.size();) {
            this.stock.setStack(i, new ItemStack(Items.DIAMOND, ++i));
        }

        for (int i = 0; i < this.categories.size();) {
            this.categories.setStack(i, new ItemStack(Items.EMERALD, ++i));
        }
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
