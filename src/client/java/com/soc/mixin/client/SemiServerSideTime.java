package com.soc.mixin.client;

import com.soc.lib.SocWarsLib;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SocWarsLib.class, remap = false)
public abstract class SemiServerSideTime {
    @Inject(method = "getWorldTime", at = @At("HEAD"), cancellable = true)
    private static void socwars_semiServerSideTime(CallbackInfoReturnable<Long> cir) {
        cir.setReturnValue(MinecraftClient.getInstance().world.getTime());
    }
}
