package com.soc.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.soc.mixinducks.ProjectileNoBlockClipAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracker;
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
}
