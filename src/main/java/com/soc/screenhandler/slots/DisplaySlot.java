package com.soc.screenhandler.slots;

import com.soc.screenhandler.AbstractShopScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class DisplaySlot extends ShopSlot<AbstractShopScreenHandler> {
    public DisplaySlot(Inventory inventory, int index, int x, int y, PlayerEntity player, AbstractShopScreenHandler context) {
        super(inventory, index, x, y, player, context);
    }

    @Override
    public boolean canBeHighlighted() {
        return false;
    }

    @Override
    public ItemStack takeStack(int amount) {
        return ItemStack.EMPTY;
    }

    @Override
    public ShopSlotType getSlotType() {
        return ShopSlotType.DISPLAY;
    }
}
