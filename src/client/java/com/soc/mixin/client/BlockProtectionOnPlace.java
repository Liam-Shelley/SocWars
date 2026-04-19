package com.soc.mixin.client;

import com.soc.gui.hud.BlockProtectionManagerAndHud;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
abstract class BlockProtectionOnPlace {
    @Inject(method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/BlockItem;getPlacementState(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/block/BlockState;"), cancellable = true)
    private void socwars_blockProtectionOnPlace(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (BlockProtectionManagerAndHud.INSTANCE.isBlockProtected(context.getBlockPos()) && !MinecraftClient.getInstance().player.isCreative()) cir.setReturnValue(ActionResult.FAIL);
    }
}
