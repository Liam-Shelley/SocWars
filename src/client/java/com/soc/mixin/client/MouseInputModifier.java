package com.soc.mixin.client;

import com.soc.effects.util.ModEffects;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Mouse.class)
public abstract class MouseInputModifier {
    @Shadow @Final private MinecraftClient client;

    @Redirect(method = "updateMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;changeLookDirection(DD)V"))
    private void injected(ClientPlayerEntity instance, double dx, double dy) {
        final double modifier = this.client.player.hasStatusEffect(ModEffects.PERPLEXITY) ? -1d : 1d;
        this.client.player.changeLookDirection(modifier * dx, modifier * dy);
    }
}