package com.soc.items.components;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ItemPostMineComponent {
	Codec<ItemPostMineComponent> CODEC = Codec.unit(null);

	boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner);
}
