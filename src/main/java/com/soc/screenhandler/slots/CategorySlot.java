package com.soc.screenhandler.slots;

import com.soc.screenhandler.BedwarsIndividualShopScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvents;

public class CategorySlot extends ShopSlot<BedwarsIndividualShopScreenHandler> {
    public CategorySlot(Inventory inventory, int index, int x, int y, PlayerEntity player, BedwarsIndividualShopScreenHandler context) {
        super(inventory, index, x, y, player, context);
    }

    @Override
    public ItemStack takeStack(int amount) {
        super.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BELL.value());
        this.context.setCurrentCategory(this.getIndex());
        return ItemStack.EMPTY;
    }
}
