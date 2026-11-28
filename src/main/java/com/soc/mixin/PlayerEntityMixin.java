package com.soc.mixin;

import com.soc.player.Morph;
import com.soc.player.PlayerData;
import com.soc.player.PlayerDataManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import static java.lang.Math.floor;

@Mixin(PlayerEntity.class)
abstract class PlayerEntityMixin extends LivingEntityMixin {
	@Shadow
	public abstract boolean isSpectator();

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
}
