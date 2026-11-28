package com.soc.util;

import com.mojang.serialization.Codec;

import static com.soc.lib.SocWarsLib.enumCodec;

public enum ServerArmPose {
	EMPTY,
	ITEM,
	BLOCK,
	BOW_AND_ARROW,
	THROW_SPEAR,
	CROSSBOW_CHARGE,
	CROSSBOW_HOLD,
	SPYGLASS,
	TOOT_HORN,
	BRUSH;

	public static final Codec<ServerArmPose> CODEC = enumCodec(ServerArmPose.values());
}
