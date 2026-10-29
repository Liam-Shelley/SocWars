package com.soc.mixin.client;

import com.soc.itemsrework.components.ModItemComponentsReworked;
import com.soc.networking.c2s.UpdateDoubleJumpPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class DoubleJump {
	@Shadow private int jumpingCooldown;

	@Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;isOnGround()Z", ordinal = 2), method = "tickMovement")
	protected boolean socwars_doubleJump(LivingEntity instance) {
 		if (instance.getType() == EntityType.PLAYER && !instance.isOnGround() && this.jumpingCooldown == 0) {
			final PlayerEntity player = (PlayerEntity)instance;

			boolean canDoubleJump = false;

			ClientPlayNetworking.send(new UpdateDoubleJumpPayload(false));
			for (Hand hand : Hand.values()) {
				final ItemStack stack = player.getStackInHand(hand);
				if (Boolean.TRUE.equals(stack.get(ModItemComponentsReworked.DOUBLE_JUMP))) {
					stack.set(ModItemComponentsReworked.DOUBLE_JUMP, false);
					player.swingHand(hand);
					canDoubleJump = true;
				}
			}

			return canDoubleJump;
		} else {
			return instance.isOnGround();
		}
	}
}