package com.soc.items.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Formatting;

import java.util.Map;

public record ExtendedRarity(Formatting formatting) {
	public static final Map<Formatting, ExtendedRarity> A = null;

	public static final Codec<ExtendedRarity> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Formatting.CODEC.fieldOf("formatting").forGetter(rarity -> rarity.formatting)
	).apply(instance, ExtendedRarity::get));

	private static ExtendedRarity get(Formatting formatting) {
		return A.getOrDefault(formatting, new ExtendedRarity(formatting)); //TODO: Finish this
	}

	public static final ExtendedRarity LEGENDARY = new ExtendedRarity(Formatting.GOLD);
	public static final ExtendedRarity MYTHIC = new ExtendedRarity(Formatting.RED);
}
