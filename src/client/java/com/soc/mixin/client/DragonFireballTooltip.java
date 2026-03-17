package com.soc.mixin.client;

import com.soc.items.ThrowableItem;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ThrowableItem.class, remap = false)
public abstract class DragonFireballTooltip {
    @Inject(method = "getWorldTime", at = @At("HEAD"), cancellable = true)
    private static void socwars_dragonTooltipTooltip(CallbackInfoReturnable<Long> cir) {
        cir.setReturnValue(MinecraftClient.getInstance().world.getTime());
    }
}
