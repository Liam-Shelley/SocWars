package com.soc.items.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.Formatting;

import java.util.Map;

public class ExtendedRarity {
	public static final Map<Formatting, ExtendedRarity> A = null;

	public static final Codec<ExtendedRarity> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Formatting.CODEC.fieldOf("formatting").forGetter(rarity -> rarity.formatting)
	).apply(instance, ExtendedRarity::get));

	private static ExtendedRarity get(Formatting formatting) {
		return A.getOrDefault(formatting, new ExtendedRarity(formatting));
	}

	final Formatting formatting;

	public static final ExtendedRarity COMMON = new ExtendedRarity(Formatting.WHITE);
	public static final ExtendedRarity UNCOMMON = new ExtendedRarity(Formatting.YELLOW);
	public static final ExtendedRarity RARE = new ExtendedRarity(Formatting.AQUA);
	public static final ExtendedRarity EPIC = new ExtendedRarity(Formatting.LIGHT_PURPLE);
	public static final ExtendedRarity LEGENDARY = new ExtendedRarity(Formatting.GOLD);
	public static final ExtendedRarity MYTHIC = new ExtendedRarity(Formatting.RED);

	public ExtendedRarity(Formatting formatting) {
		this.formatting = formatting;
	}

	public Formatting getFormatting() {
		return this.formatting;
	}
}
