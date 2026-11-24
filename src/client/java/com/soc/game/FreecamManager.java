package com.soc.game;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.xolt.freecam.Freecam;

public class FreecamManager {
	private static boolean preventFreecam = false;

	public static void intialise() {
		if (!FabricLoader.getInstance().isModLoaded("freecam")) return;
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> preventFreecam = false);
	}

	public static boolean preventFreecam() {
		return preventFreecam;
	}

	public static void updatePreventFreecam(boolean newPreventFreecam) {
		if (!FabricLoader.getInstance().isModLoaded("freecam")) return;

		preventFreecam = newPreventFreecam;
		if (newPreventFreecam && Freecam.isEnabled()) {
			Freecam.toggle();
		}
	}
}
