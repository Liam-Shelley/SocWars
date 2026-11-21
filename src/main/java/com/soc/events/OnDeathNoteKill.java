package com.soc.events;

import net.minecraft.entity.player.PlayerEntity;

@FunctionalInterface
public interface OnDeathNoteKill {
	enum Result {
		SUCCESS,
		FAIL,
		CANCEL
	}

	Result tryKill(PlayerEntity uuid, PlayerEntity killer);
}
