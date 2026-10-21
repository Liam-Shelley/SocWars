package com.soc.items.util;

import net.minecraft.item.Item;

import java.util.Optional;

import static com.soc.items.components.ModComponents.STASIS_POSITION;

public class StasisChamber extends Item {
	public StasisChamber(Settings settings) {
		super(settings.component(STASIS_POSITION, Optional.empty()));
	}
}
