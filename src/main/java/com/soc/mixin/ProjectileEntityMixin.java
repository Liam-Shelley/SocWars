package com.soc.mixin;

import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.util.hit.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ProjectileEntity.class)
abstract class ProjectileEntityMixin extends EntityMixin {
	@SuppressWarnings("CancellableInjectionUsage")
	@Inject(method = "onCollision", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/projectile/ProjectileEntity;onBlockHit(Lnet/minecraft/util/hit/BlockHitResult;)V"), cancellable = true)
	protected void socwars$onCollision(HitResult hitResult, CallbackInfo ci) {}
}
