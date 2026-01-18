package com.soc.screenhandler.slots;

import com.soc.screenhandler.BedwarsIndividualShopScreenHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;

public abstract class ShopSlot extends Slot {
    protected final PlayerEntity player;
    protected final BedwarsIndividualShopScreenHandler context;

    public ShopSlot(Inventory inventory, int index, int x, int y, PlayerEntity player, BedwarsIndividualShopScreenHandler context) {
        super(inventory, index, x, y);
        this.player = player;
        this.context = context;
    }

    @Override
    public boolean canInsert(ItemStack stack) {
        return false;
    }
}
