package com.soc.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.world.World;

public class ArrowAntiGravityBowEntity extends ArrowEntity {
	public ArrowAntiGravityBowEntity(EntityType<? extends ArrowEntity> entityType, World world) {
		super(entityType, world);

		this.setNoGravity(true);
	}
}
