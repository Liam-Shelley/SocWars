package com.soc.screenhandler.slots;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundEvents;

public class PageSlot extends ShopSlot {
    public PageSlot(Inventory inventory, int index, int x, int y, PlayerEntity player, ScreenHandler context) {
        super(inventory, index, x, y, player, context);
    }

    @Override
    public ItemStack takeStack(int amount) {
        super.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BELL.value());
        return ItemStack.EMPTY;
    }
}
