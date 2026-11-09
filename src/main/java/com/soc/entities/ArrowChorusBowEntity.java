package com.soc.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

import static com.soc.lib.SocWarsLib.randomTeleport;
import static com.soc.util.SphereExplosion.fireExplosion;

public class ArrowChorusBowEntity extends ArrowEntity {
	public ArrowChorusBowEntity(EntityType<? extends ArrowEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void onHit(LivingEntity target) {
		super.onHit(target);

		this.discard();
		randomTeleport(this.getWorld(), target, 75, 7, 1f);
	}

	@Override
	protected void onBlockHit(BlockHitResult blockHitResult) {
		super.onBlockHit(blockHitResult);

		randomTeleport(this.getWorld(), this, 2, 15, 2f);
	}
}
