package com.soc.mixin;

import com.soc.game.manager.GamesManager;
import net.minecraft.block.AbstractSignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractSignBlock.class)
abstract class ProtectSignBlock {
    @Inject(method = "openEditScreen", at = @At("HEAD"), cancellable = true)
    private void socwars_signEditScreenBlock(PlayerEntity player, SignBlockEntity blockEntity, boolean front, CallbackInfo ci) {
        GamesManager.getInstance().getGame(player).ifPresent(manager -> {
            if (manager.isBlockProtected(blockEntity.getPos()) && !player.isCreative()) ci.cancel();
        });
    }
}
