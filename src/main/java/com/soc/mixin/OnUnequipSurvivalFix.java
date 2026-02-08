package com.soc.mixin;

import com.soc.items.util.OnEquipArmour;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Slot.class)
public abstract class OnUnequipSurvivalFix {
    @Shadow public abstract int getIndex();

    @Shadow @Final public Inventory inventory;

    @Inject(method = "setStack(Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemStack;)V", at = @At("HEAD"))
    protected void socwars_tryTakeStackRange(ItemStack stack, ItemStack previousStack, CallbackInfo ci) {
        if (this.getIndex() >= 36 && this.inventory instanceof PlayerInventory playerInventory && previousStack.getItem() instanceof OnEquipArmour onEquipArmour) onEquipArmour.unequip(playerInventory.player);
    }
}
