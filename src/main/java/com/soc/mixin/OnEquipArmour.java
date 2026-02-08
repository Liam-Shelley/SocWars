package com.soc.mixin;

import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerEquipment;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEquipment.class)
public abstract class OnEquipArmour {
    @Shadow @Final private PlayerEntity player;

    @Inject(method = "put", at = @At("HEAD"))
    private void socwars_onEquipArmour(EquipmentSlot slot, ItemStack incomingStack, CallbackInfoReturnable<ItemStack> cir) {
        if (slot.isArmorSlot()) {
            final ItemStack outgoingStack = this.player.getEquippedStack(slot);
            if (outgoingStack.getItem() instanceof com.soc.items.util.OnEquipArmour outgoingOnEquip) outgoingOnEquip.unequip(this.player, slot, outgoingStack);
            if (incomingStack.getItem() instanceof com.soc.items.util.OnEquipArmour incomingOnEquip) incomingOnEquip.equip(this.player, slot, outgoingStack);
        }
    }
}
