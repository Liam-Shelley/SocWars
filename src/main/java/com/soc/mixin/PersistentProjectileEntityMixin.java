package com.soc.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.soc.entities.util.ModifyDrag;
import com.soc.mixinducks.ProjectileNoBlockClipAccess;
import net.minecraft.block.BlockState;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PersistentProjectileEntity.class)
public abstract class PersistentProjectileEntityMixin extends ProjectileEntityMixin implements ProjectileNoBlockClipAccess {
	@Unique
	private static final int NO_BLOCK_CLIP_FLAG = 1 << 7;

	@Unique
	private boolean socwars$noBlockClip;


	@Shadow
	@Final
	private static TrackedData<Byte> PROJECTILE_FLAGS;

	@Shadow
	protected abstract void setProjectileFlag(int index, boolean flag);

	@Unique
	@Override
	public void socwars$setBlockNoClip(boolean noBlockClip) {
		this.socwars$noBlockClip = noBlockClip;
		this.setProjectileFlag(NO_BLOCK_CLIP_FLAG, noBlockClip);
	}

	@Unique
	@Override
	public boolean socwars$isBlockNoClip() {
		if (!this.getWorld().isClient) {
			return this.socwars$noBlockClip;
		}
		return (this.dataTracker.get(PROJECTILE_FLAGS) & NO_BLOCK_CLIP_FLAG) != 0;
	}

	@Inject(method = "getDragInWater", at = @At("HEAD"), cancellable = true)
	private void socwars$getDragInWaterNoBlockClip(CallbackInfoReturnable<Float> cir) {
		if (this.socwars$isBlockNoClip()) cir.setReturnValue(0f);
	}

	@WrapOperation(method = "applyCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/PersistentProjectileEntity;setPosition(Lnet/minecraft/util/math/Vec3d;)V"))
	private void socwars$applyCollisionNoBlockClip(PersistentProjectileEntity instance, Vec3d vec3d, Operation<Void> original, @Local(ordinal = 0) Vec3d position) {
		original.call(instance, this.socwars$isBlockNoClip() ? position.add(this.getVelocity()) : vec3d);
	}

	@Override
	protected boolean socwars$shouldNotTickBlockCollision(boolean original) {
		return original || this.socwars$isBlockNoClip();
	}

	@Override
	protected void socwars$onCollision(HitResult hitResult, CallbackInfo ci) {
		if (this.socwars$isBlockNoClip()) ci.cancel();
	}

	@Override
	protected boolean socwars$move(boolean original) {
		return super.socwars$move(original) || this.socwars$isBlockNoClip();
	}

	@Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/BlockState;isAir()Z"))
	private boolean socwars$tickNoBlockClip(BlockState instance) {
		return instance.isAir() || this.socwars$isBlockNoClip();
	}

	@WrapMethod(method = "applyDrag")
	private void socwar$modifyDrag(float drag, Operation<Void> original) {
		if ((Object)this instanceof ModifyDrag modifyDrag) {
			original.call(modifyDrag.getDrag());
		} else {
			original.call(drag);
		}
	}
}
