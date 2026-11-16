package com.soc.events;

import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;

@FunctionalInterface
public interface OnDeathNoteKill {
	enum Result {
		SUCCESS,
		FAIL,
		CANCEL
	}

	Result tryKill(UUID uuid, PlayerEntity killer);
}
