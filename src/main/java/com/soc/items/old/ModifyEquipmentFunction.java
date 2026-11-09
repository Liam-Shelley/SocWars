package com.soc.items.old;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;

@FunctionalInterface
public interface ModifyEquipmentFunction {
    void modifyEquipment(LivingEntity target, EquipmentSlot slot);
}
