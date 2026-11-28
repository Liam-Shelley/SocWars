package com.soc.mixin;

import com.soc.items.components.ModItemComponents;
import com.soc.networking.s2c.CustomDeathProtectionPayload;
import com.soc.player.Morph;
import com.soc.player.PlayerData;
import com.soc.player.PlayerDataManager;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.soc.lib.SocWarsLib.ifNotNull;
import static java.lang.Math.floor;

@Mixin(PlayerEntity.class)
abstract class PlayerEntityMixin extends LivingEntityMixin {
	@Shadow
	public abstract boolean isSpectator();

	@Shadow
	@Final
	PlayerInventory inventory;

	@Override
	protected void socwars$getBoundingBox(CallbackInfoReturnable<Box> cir) {
		final PlayerData playerData = PlayerDataManager.getSideLocalPlayerData((PlayerEntity)(Object)this);
		if (playerData == null) return;

		final Morph morph = playerData.getMorph();
		if (morph == null) return;

		final Vec3d thisPos = this.getPos();
		final Box boundingBox = morph.boundingBox();

		cir.setReturnValue(morph.shouldSnap(this.isInSneakingPose(), this.getWorld(), this.getBlockPos()) ?
				boundingBox.offset(floor(thisPos.x), thisPos.y, floor(thisPos.z)) :
				boundingBox.offset(thisPos.x - 0.5d, thisPos.y, thisPos.z - 0.5d)
		);
	}

	@Unique
	private boolean socwars$isMorphed() {
		final PlayerData playerData = PlayerDataManager.getSideLocalPlayerData((PlayerEntity)(Object)this);
		return playerData != null && playerData.getMorph() != null;
	}

	@Override
	protected void socwars$isCollidable(CallbackInfoReturnable<Boolean> cir) {
		if (this.socwars$isMorphed()) cir.setReturnValue(true);
	}

	@Override
	protected void socwars$isPushable(CallbackInfoReturnable<Boolean> cir) {
		if (this.socwars$isMorphed()) cir.setReturnValue(false);
	}

	@Override
	protected void socwars$pushAwayFrom(Entity entity, CallbackInfo ci) {
		if (this.socwars$isMorphed()) ci.cancel();
	}

	@Override
	protected void socwars$customDeathProtectors(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
		for (ItemStack itemStack : this.inventory) {
			final Float deathProtection = itemStack.get(ModItemComponents.FULL_DEATH_PROTECTION);
			if (deathProtection != null) {
				this.socwars$savePlayer(deathProtection, itemStack);
				cir.setReturnValue(true);
				break;
			}
		}
	}

	@Unique
	private void socwars$savePlayer(float health, ItemStack stack) {
		this.setHealth(health);
		ifNotNull(stack.get(DataComponentTypes.DEATH_PROTECTION), component -> component.applyDeathEffects(stack, (PlayerEntity)(Object)this));

		if ((Object)this instanceof ServerPlayerEntity serverPlayer) {
			final CustomDeathProtectionPayload payload = new CustomDeathProtectionPayload(serverPlayer.getId(), stack.copy());

			for (ServerPlayerEntity nearbyPlayer : PlayerLookup.world(serverPlayer.getWorld())) {
				ServerPlayNetworking.send(nearbyPlayer, payload);
			}
		}

		stack.decrementUnlessCreative(1, (PlayerEntity)(Object)this);
	}
}
