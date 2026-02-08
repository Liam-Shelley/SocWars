package com.soc.items.util;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

public interface OnEquipArmour {
    void unequip(PlayerEntity player, EquipmentSlot slot, ItemStack stack);

    void equip(PlayerEntity player, EquipmentSlot slot, ItemStack stack);
}
