package com.soc.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.soc.itemsrework.components.ModItemComponentsReworked;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerInteractionManager.class)
abstract class CancelItemInteraction {
	@Redirect(method = "interactBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;shouldCancelInteraction()Z"))
	private boolean socwars_shouldCancelInteraction(ServerPlayerEntity player, @Local(argsOnly = true) Hand hand) {
		return player.getStackInHand(hand).get(ModItemComponentsReworked.CANCEL_BLOCK_INTERACTION) != null || player.shouldCancelInteraction();
	}
}
