package com.soc.entities;

import com.soc.mixinducks.ProjectileNoBlockClipAccess;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.world.World;

public class ArrowUnstoppableBowEntity extends ArrowEntity {
	public ArrowUnstoppableBowEntity(EntityType<? extends ArrowEntity> entityType, World world) {
		super(entityType, world);

		((ProjectileNoBlockClipAccess)this).socwars$setBlockNoClip(true);
	}
}
