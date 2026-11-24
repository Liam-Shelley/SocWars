package com.soc.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.soc.items.components.ModItemComponents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayerInteractionManager.class)
abstract class ServerPlayerInteractionManagerMixin {
	@Shadow
	@Final
	protected ServerPlayerEntity player;

	@Redirect(method = "interactBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;shouldCancelInteraction()Z"))
	private boolean socwars_shouldCancelInteraction(ServerPlayerEntity player, @Local(argsOnly = true) Hand hand) {
		return player.getStackInHand(hand).get(ModItemComponents.CANCEL_BLOCK_INTERACTION) != null || player.shouldCancelInteraction();
	}

	@Definition(id = "creativeMode", field = "Lnet/minecraft/entity/player/PlayerAbilities;creativeMode:Z")
	@Expression("?.?().creativeMode")
	@ModifyExpressionValue(method = "processBlockBreakingAction", at = @At("MIXINEXTRAS:EXPRESSION"))
	private boolean socwars$creativeMiningComponentBreaking(boolean original) {
		return original || this.player.getMainHandStack().contains(ModItemComponents.CREATIVE_MINING);
	}
}
