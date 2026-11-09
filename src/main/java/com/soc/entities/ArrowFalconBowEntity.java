package com.soc.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ArrowFalconBowEntity extends ArrowEntity {
	public ArrowFalconBowEntity(EntityType<? extends ArrowEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void onHit(LivingEntity target) {
		super.onHit(target);

		this.discard();

		final Vec3d knockback = this.getVelocity().getHorizontal().add(0d, 0.5d, 0d);
		if (target instanceof ServerPlayerEntity serverPlayer) {
			serverPlayer.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(serverPlayer.getId(), serverPlayer.getVelocity().add(knockback)));
		}
		target.addVelocity(knockback);
	}
}
