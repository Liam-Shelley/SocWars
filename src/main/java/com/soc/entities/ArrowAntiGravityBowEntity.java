package com.soc.entities;

import com.soc.entities.util.ModifyDrag;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.world.World;

public class ArrowAntiGravityBowEntity extends ArrowEntity implements ModifyDrag {
	public ArrowAntiGravityBowEntity(EntityType<? extends ArrowEntity> entityType, World world) {
		super(entityType, world);

		this.setNoGravity(true);
	}

	@Override
	public float getDrag() {
		return 1f;
	}
}
