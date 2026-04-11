package com.soc.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.soc.entities.util.AllowsDismount;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayNetworkHandler.class)
abstract class OnMountDismountAlert {
    @Redirect(method = "onEntityPassengersSet", at = @At(value = "INVOKE", target = "Lnet/minecraft/text/Text;translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/text/MutableText;"))
    private MutableText socwars_onMountDismountAlert(String key, Object[] args, @Local(argsOnly = true) EntityPassengersSetS2CPacket packet, @Local(ordinal = 0) Entity entity) {
        return !(entity instanceof AllowsDismount allowsDismountEntity) || allowsDismountEntity.allowsDismount() ? Text.translatable(key, args) : Text.translatable("game.disallow_dismount", MinecraftClient.getInstance().options.sneakKey.getBoundKeyLocalizedText());
    }
}
