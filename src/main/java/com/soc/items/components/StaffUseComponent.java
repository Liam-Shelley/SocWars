package com.soc.items.components;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public record StaffUseComponent() implements ItemUseComponent {
	public static final StaffUseComponent INSTANCE = new StaffUseComponent();

	@Override
	public ActionResult use(World world, PlayerEntity user, Hand hand) {
		user.setCurrentHand(hand);
		return ActionResult.CONSUME;
	}
}
