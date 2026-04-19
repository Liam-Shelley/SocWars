package com.soc.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static com.soc.lib.SocWarsLib.hasInfinity;

@Mixin(PlayerEntity.class)
abstract class BowInfinityFix {
    @Redirect(method = "getProjectileType", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isInCreativeMode()Z"))
    private boolean socwars_bowInfinityFix(PlayerEntity instance, @Local(argsOnly = true) ItemStack stack) {
        return instance.isCreative() || hasInfinity(stack);
    }
}
