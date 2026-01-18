package com.soc.screenhandler.slots;

import com.soc.screenhandler.BedwarsIndividualShopScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class StockSlot extends ShopSlot {
    private static final SoundEvent BUY_SUCCESS = SoundEvents.BLOCK_NOTE_BLOCK_XYLOPHONE.value();
    private static final SoundEvent BUY_FAIL = SoundEvents.BLOCK_ANVIL_FALL;

    public StockSlot(Inventory inventory, int index, int x, int y, PlayerEntity player, BedwarsIndividualShopScreenHandler context) {
        super(inventory, index, x, y, player, context);
    }

    @Override
    public ItemStack takeStack(int amount) {
        final boolean success = super.context.getShopItem(this).buy(super.player, super.context);

        super.player.playSound(success ? BUY_SUCCESS : BUY_FAIL);

        return ItemStack.EMPTY;
    }
}
