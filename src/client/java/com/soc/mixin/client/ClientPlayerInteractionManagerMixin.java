package com.soc.mixin.client;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.soc.items.components.ModItemComponents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Objects;

@Mixin(ClientPlayerInteractionManager.class)
abstract class ClientPlayerInteractionManagerMixin {
	@Shadow
	@Final
	private MinecraftClient client;

	@Redirect(method = "interactBlockInternal", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;shouldCancelInteraction()Z"))
	private boolean socwars$shouldCancelInteraction(ClientPlayerEntity player, @Local(argsOnly = true) Hand hand) {
		return player.getStackInHand(hand).get(ModItemComponents.CANCEL_BLOCK_INTERACTION) != null || player.shouldCancelInteraction();
	}

	@Definition(id = "creativeMode", field = "Lnet/minecraft/entity/player/PlayerAbilities;creativeMode:Z")
	@Expression("?.?().creativeMode")
	@ModifyExpressionValue(method = "updateBlockBreakingProgress", at = @At("MIXINEXTRAS:EXPRESSION"))
	private boolean socwars$creativeMiningComponentBreaking(boolean original) {
		return original || Objects.requireNonNull(this.client.player).getMainHandStack().contains(ModItemComponents.CREATIVE_MINING);
	}

	@Definition(id = "creativeMode", field = "Lnet/minecraft/entity/player/PlayerAbilities;creativeMode:Z")
	@Expression("?.?().creativeMode")
	@ModifyExpressionValue(method = "attackBlock", at = @At("MIXINEXTRAS:EXPRESSION"))
	private boolean socwars$creativeMiningComponentBreakingAttackBlock(boolean original) {
		return original || Objects.requireNonNull(this.client.player).getMainHandStack().contains(ModItemComponents.CREATIVE_MINING);
	}
}
