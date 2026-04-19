package com.soc.mixin.client;

import com.mojang.authlib.GameProfile;
import com.soc.items.util.ScaledUseDuration;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractClientPlayerEntity.class)
abstract class BowZoomFix extends PlayerEntity {
    public BowZoomFix(World world, GameProfile profile) {
        super(world, profile);
    }

    @Redirect(method = "getFovMultiplier", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
    private boolean socwars_bowZoomFixPredicate(ItemStack instance, Item item) {
        return instance.getUseAction() == UseAction.BOW;
    }

    @Redirect(method = "getFovMultiplier", at = @At(value = "INVOKE", target = "Ljava/lang/Math;min(FF)F"))
    private float socwars_bowZoomFixClamp(float a, float b) {
        if ((this.getActiveItem().getItem() instanceof ScaledUseDuration implementer)) {
            return Math.clamp(a, 0f, b) * (float)Math.sqrt(implementer.getScale(this.getActiveItem()));
        }
        return Math.clamp(a, 0f, b);
    }
}
