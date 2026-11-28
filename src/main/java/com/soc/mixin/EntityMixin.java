package com.soc.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.soc.mixinducks.OwnableLightningAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.soc.lib.SocWarsLib.damageSource;

@Mixin(Entity.class)
abstract class EntityMixin {
	@Shadow public abstract World getWorld();

	@Shadow public abstract BlockPos getBlockPos();

	@Shadow public abstract Vec3d getPos();

	@Shadow public abstract boolean isInSneakingPose();

	@Shadow
	@Final
	protected DataTracker dataTracker;

	@Shadow
	public abstract void setPosition(Vec3d pos);

	@Shadow
	public abstract Vec3d getVelocity();

	@SuppressWarnings("CancellableInjectionUsage")
	@Inject(method = "getBoundingBox", at = @At("HEAD"), cancellable = true)
	protected void socwars$getBoundingBox(CallbackInfoReturnable<Box> cir) {}

	@SuppressWarnings("CancellableInjectionUsage")
	@Inject(method = "isCollidable", at = @At("HEAD"), cancellable = true)
	protected void socwars$isCollidable(CallbackInfoReturnable<Boolean> cir) {}

	@SuppressWarnings("CancellableInjectionUsage")
	@Inject(method = "isPushable", at = @At("HEAD"), cancellable = true)
	protected void socwars$isPushable(CallbackInfoReturnable<Boolean> cir) {}

	@SuppressWarnings("CancellableInjectionUsage")
	@Inject(method = "tickBlockCollision()V", at = @At("HEAD"), cancellable = true)
	protected void socwars$tickBlockCollision(CallbackInfo ci) {}

	@Definition(id = "noClip", field = "Lnet/minecraft/entity/Entity;noClip:Z")
	@Expression("this.noClip")
	@ModifyExpressionValue(method = "move", at = @At(value = "FIELD", ordinal = 0, opcode = Opcodes.GETFIELD))
	protected boolean socwars$move(boolean original) {
		return original;
	}

	@Definition(id = "noClip", field = "Lnet/minecraft/entity/Entity;noClip:Z")
	@Expression("this.?()")
	@ModifyExpressionValue(method = "shouldTickBlockCollision", at = @At(value = "MIXINEXTRAS:EXPRESSION"))
	protected boolean socwars$shouldNotTickBlockCollision(boolean original) {
		return original;
	}

	@WrapOperation(method = "onStruckByLightning", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;damage(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/damage/DamageSource;F)Z"))
	private boolean socwars$lightningStrikeOwnerDamageSource(Entity instance, ServerWorld serverWorld, DamageSource damageSource, float v, Operation<Boolean> original, @Local(argsOnly = true) LightningEntity lightning) {
		return original.call(instance, serverWorld, new DamageSource(damageSource.getTypeRegistryEntry(), ((OwnableLightningAccess)lightning).socwars$getOwner()), v); //No idea why I can't just call getOwner() here the interface has been injected and I thought that was how that was supposed to work
	}
}
