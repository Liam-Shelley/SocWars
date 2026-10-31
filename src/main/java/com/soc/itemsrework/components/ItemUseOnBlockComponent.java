package com.soc.itemsrework.components;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@FunctionalInterface
public interface ItemUseOnBlockComponent {
	Codec<ItemUseOnBlockComponent> CODEC = Codec.unit(null);

	ActionResult use(World world, PlayerEntity user, Hand hand, BlockState state, BlockPos pos);
}
