package com.soc.itemsrework.components;

import com.mojang.serialization.Codec;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public interface UseItemComponent {
	Codec<UseItemComponent> CODEC = Codec.unit(null);

	void use(World world, PlayerEntity user, Hand hand);
}
