package com.soc.mixin.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.OptionalInt;

@Mixin(PlayerListHud.class)
abstract class GamePlayerList {
    @Inject(method = "collectPlayerEntries", at = @At("RETURN"), cancellable = true)
    private void socwars_gamePlayerList(CallbackInfoReturnable<List<PlayerListEntry>> cir) {
        final PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null || player.isCreative()) return;

        final OptionalInt game = getGame(player);
        if (game.isEmpty()) return;

        cir.setReturnValue(cir.getReturnValue().stream().filter(listPlayer -> getGame(listPlayer).equals(game)).toList());
    }

    @Unique
    private static OptionalInt getGame(PlayerEntity player) {
        try {
            return OptionalInt.of(Integer.parseInt(player.getScoreboardTeam().getName().split("_")[0]));
        } catch (Exception e) {
            return OptionalInt.empty();
        }
    }

    @Unique
    private static OptionalInt getGame(PlayerListEntry player) {
        try {
            return OptionalInt.of(Integer.parseInt(player.getScoreboardTeam().getName().split("_")[0]));
        } catch (Exception e) {
            return OptionalInt.empty();
        }
    }
}
