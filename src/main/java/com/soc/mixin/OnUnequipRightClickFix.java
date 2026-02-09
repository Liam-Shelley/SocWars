package com.soc.mixin;

import com.soc.items.util.OnEquipArmour;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class OnUnequipRightClickFix {
    @Inject(method = "equipStack", at = @At("HEAD"))
    protected void socwars_equipStack(EquipmentSlot slot, ItemStack stack, CallbackInfo ci) {
        if (slot == EquipmentSlot.MAINHAND && (LivingEntity)(Object)this instanceof PlayerEntity player && stack.getItem() instanceof OnEquipArmour onEquipArmour) onEquipArmour.unequip(player);
    }
}
