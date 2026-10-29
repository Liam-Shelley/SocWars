package com.soc.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.soc.itemsrework.components.ModItemComponentsReworked;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ClientPlayerInteractionManager.class)
abstract class CancelItemInteractionClient {
	@Redirect(method = "interactBlockInternal", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;shouldCancelInteraction()Z"))
	private boolean socwars_shouldCancelInteraction(ClientPlayerEntity player, @Local(argsOnly = true) Hand hand) {
		return player.getStackInHand(hand).get(ModItemComponentsReworked.CANCEL_BLOCK_INTERACTION) != null || player.shouldCancelInteraction();
	}
}
