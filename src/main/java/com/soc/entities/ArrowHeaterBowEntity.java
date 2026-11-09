package com.soc.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

import static com.soc.util.SphereExplosion.fireExplosion;

public class ArrowHeaterBowEntity extends ArrowEntity {
	public ArrowHeaterBowEntity(EntityType<? extends ArrowEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void onHit(LivingEntity target) {
		super.onHit(target);

		this.discard();
		fireExplosion(this.getWorld(), target.getBlockPos(), 5f, 0.125f, this.getOwner());
	}

	@Override
	protected void onBlockHit(BlockHitResult blockHitResult) {
		super.onBlockHit(blockHitResult);

		this.discard();
		fireExplosion(this.getWorld(), blockHitResult.getBlockPos(), 4f, 0.175f, this.getOwner());
	}
}
