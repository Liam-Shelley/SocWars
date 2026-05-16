package com.soc.screenhandler;

import com.soc.blocks.blockentities.KitBlockEntity;
import com.soc.game.GameKit;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class KitScreenHandler extends ScreenHandler {
    public static final int ITEM_SLOTS_WIDTH = 3;
    public static final int ITEM_SLOTS_HEIGHT = 3;

    private final GameKit gameKit;
    private final KitBlockEntity blockEntity;

    public KitScreenHandler(int syncId, PlayerInventory playerInventory, GameKit gameKit, KitBlockEntity blockEntity) {
        super(ScreenHandlers.KIT_SCREEN_HANDLER, syncId);
        this.gameKit = gameKit;
        this.blockEntity = blockEntity;

        for (int y = 0; y < ITEM_SLOTS_HEIGHT; y++) {
            for (int x = 0; x < ITEM_SLOTS_WIDTH; x++) {
                this.addSlot(new Slot(this.gameKit, x + ITEM_SLOTS_WIDTH * y, x * 18 + 8, y * 18 + 18) {
                    @Override
                    public void markDirty() {
                        super.markDirty();
                        if (KitScreenHandler.this.blockEntity != null) KitScreenHandler.this.blockEntity.markDirty();
                    }
                });
            }
        }
        this.addPlayerSlots(playerInventory, 8, 86);
    }

    public KitScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new GameKit(), null);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        //TODO: Reqrite all of this since this is a copy paste from the minecraft code and I don't understand how it works at all
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        if (slot2 != null && slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            if (slot < 9) {
                if (!this.insertItem(itemStack2, 9, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(itemStack2, 0, 9, false)) {
                return ItemStack.EMPTY;
            }

            if (itemStack2.isEmpty()) {
                slot2.setStack(ItemStack.EMPTY);
            } else {
                slot2.markDirty();
            }
        }

        return itemStack;

        //return ItemStack.EMPTY;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return player.isCreative();
    }
}
