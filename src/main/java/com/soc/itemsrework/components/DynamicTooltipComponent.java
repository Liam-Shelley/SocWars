package com.soc.itemsrework.components;

import com.mojang.serialization.Codec;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import java.util.List;

@FunctionalInterface
public interface DynamicTooltipComponent {
	Codec<DynamicTooltipComponent> CODEC = Codec.unit(null);

	List<Text> create(long time);
}
