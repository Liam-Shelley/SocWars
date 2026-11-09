package com.soc.entities;

import com.soc.util.DamageTypes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

import static com.soc.lib.SocWarsLib.damageSource;
import static com.soc.lib.SocWarsLib.forceKillTarget;

public class ArrowCatastropheBowEntity extends ArrowEntity {
	public ArrowCatastropheBowEntity(EntityType<? extends ArrowEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void onHit(LivingEntity target) {
		super.onHit(target);

		this.discard();
		forceKillTarget(target, this.getOwner());
	}

	@Override
	public void onPlayerCollision(PlayerEntity player) {
		super.onPlayerCollision(player);

		if (player != this.getOwner() && this.getWorld() instanceof ServerWorld serverWorld) {
			player.damage(serverWorld, damageSource(serverWorld, DamageTypes.CATASTROPHE_BOW), 1);
		}
	}
}
