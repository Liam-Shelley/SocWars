package com.soc.mixin;

import net.minecraft.entity.player.HungerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HungerManager.class)
public abstract class HungerTweaks {
    @Shadow private float saturationLevel;
    @Shadow private int foodLevel;

    @Inject(method = "update", at = @At("HEAD"))
    private void socwars_returnFullHunger(ServerPlayerEntity player, CallbackInfo ci) {
        this.foodLevel = 20;
        this.saturationLevel = 20f;
    }

    @ModifyConstant(method = "update", constant = @Constant(intValue = 10))
    private int socwars_nerfDefaultHungerRegeneration(int constant) {
        return 30;
    }
}
