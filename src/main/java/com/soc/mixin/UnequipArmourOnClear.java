package com.soc.mixin;

import com.soc.items.util.OnEquipArmour;
import net.minecraft.entity.EntityEquipment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Arrays;
import java.util.function.Predicate;

@Mixin(PlayerInventory.class)
public abstract class UnequipArmourOnClear {
    @Shadow @Final private EntityEquipment equipment;

    @Shadow @Final public PlayerEntity player;

    @Inject(method = "remove", at = @At("HEAD"))
    private void socwars_unequipArmourOnClear(Predicate<ItemStack> shouldRemove, int maxCount, Inventory craftingInventory, CallbackInfoReturnable<Integer> cir) {
        if (maxCount == 0) return;
        Arrays.stream(EquipmentSlot.values()).filter(EquipmentSlot::isArmorSlot).forEach(slot -> {
            final ItemStack stack = equipment.get(slot);
            if (stack.getItem() instanceof OnEquipArmour onEquipArmourItem) onEquipArmourItem.unequip(this.player);
        });
    }
}
