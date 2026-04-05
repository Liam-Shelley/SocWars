package com.soc.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public class ClearInventoryHoverItem {
    @Shadow @Final public PlayerEntity player;

    @Inject(method = "clear", at = @At("HEAD"))
    private void socwars_clearInventoryHoverItemServer(CallbackInfo ci) {
        this.player.currentScreenHandler.setCursorStack(ItemStack.EMPTY);
    }
}
