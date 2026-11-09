package com.soc.entities;

import com.soc.util.SphereExplosion;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ArrowMegaboomBowEntity extends ArrowEntity {
	public ArrowMegaboomBowEntity(EntityType<? extends ArrowEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void onHit(LivingEntity target) {
		super.onHit(target);

		this.discard();
		SphereExplosion.explode(this.getWorld(), target.getPos(), 7f, 1.5f, 0.7f, 0.7f, true, this.getOwner(), null);
	}

	@Override
	protected void onBlockHit(BlockHitResult blockHitResult) {
		super.onBlockHit(blockHitResult);

		this.discard();
		SphereExplosion.explode(this.getWorld(), blockHitResult.getPos(), 8f, 1.5f, 0.9f, 0.8f, true, this.getOwner(), null);
	}
}
