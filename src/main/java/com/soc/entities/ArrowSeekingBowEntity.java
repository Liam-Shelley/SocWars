package com.soc.entities;

import com.soc.game.manager.AbstractHidingGameManager;
import com.soc.game.manager.GamesManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;

public class ArrowSeekingBowEntity extends ArrowEntity {
	public ArrowSeekingBowEntity(EntityType<? extends ArrowEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void onHit(LivingEntity target) {
		super.onHit(target);

		this.discard();
		GamesManager.getInstance().getGame(this.getOwner()).ifPresent(game -> {
			if (game instanceof AbstractHidingGameManager<?, ?, ?> hidingGameManager && target instanceof ServerPlayerEntity serverPlayerTarget) {
				hidingGameManager.tryFindPlayer(this.getOwner(), serverPlayerTarget);
			}
		});
	}

	@Override
	protected void onBlockHit(BlockHitResult blockHitResult) {
		super.onBlockHit(blockHitResult);

		this.discard();
	}
}
