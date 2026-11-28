package com.soc.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.soc.entities.util.ModifyDrag;
import net.minecraft.entity.projectile.thrown.ThrownEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ThrownEntity.class)
abstract class ThrownEntityMixin {
	@WrapOperation(method = "applyDrag", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;multiply(D)Lnet/minecraft/util/math/Vec3d;"))
	private Vec3d socwars(Vec3d instance, double value, Operation<Vec3d> original) {
		return original.call(instance, (Object)this instanceof ModifyDrag modifyDrag ? modifyDrag.getDrag() : value);
	}
}
