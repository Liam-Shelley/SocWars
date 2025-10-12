package com.soc.mixin;

import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityVelocityUpdateS2CPacket.class)
public abstract class VelocityPacketUnlimit {
	@Redirect(method = "<init>(ILnet/minecraft/util/math/Vec3d;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;clamp(DDD)D"))
	private double injected(double value, double min, double max) {
		return value;
	}
}