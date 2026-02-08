package com.soc.items.util;

import net.minecraft.entity.player.PlayerEntity;

public interface OnEquipArmour {
    void unequip(PlayerEntity player);

    void equip(PlayerEntity player);
}
