package com.soc.itemsrework.components;

import com.mojang.serialization.Codec;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public interface ItemUseComponent {
	Codec<ItemUseComponent> CODEC = Codec.unit(null);

	ActionResult use(World world, PlayerEntity user, Hand hand);
}
