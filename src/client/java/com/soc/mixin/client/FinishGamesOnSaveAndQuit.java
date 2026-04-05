package com.soc.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.soc.game.manager.GamesManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameMenuScreen.class)
abstract class FinishGamesOnSaveAndQuit {
    @Inject(method = "disconnect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getCurrentServerEntry()Lnet/minecraft/client/network/ServerInfo;"))
    private static void socwars_onDisconnect(MinecraftClient client, Text disconnectReason, CallbackInfo ci, @Local boolean bl) {
        if (bl) GamesManager.getInstance().returnAllPlayersToLobby();
    }
}
