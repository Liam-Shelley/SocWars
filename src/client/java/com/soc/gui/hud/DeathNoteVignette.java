package com.soc.gui.hud;

import com.soc.SocWars;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;

public class DeathNoteVignette {
	@Nullable
	private static DeathNoteVignette INSTANCE;

	private final long darknessStartTime;

	public DeathNoteVignette(long time) {
		this.darknessStartTime = time;
	}

	public static void initialise() { //TODO: Do this in a non-deprecated way
		HudRenderCallback.EVENT.register(DeathNoteVignette::render);
		ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> INSTANCE = null);
	}

	public static void receiveDeathNote() {
		INSTANCE = new DeathNoteVignette(MinecraftClient.getInstance().world.getTime());
	}

	public static void clear() {
		INSTANCE = null;
	}

	private static void render(DrawContext drawContext, RenderTickCounter renderTickCounter) {
		if (INSTANCE != null) {
			final long elapsedTime = MinecraftClient.getInstance().world.getTime() - INSTANCE.darknessStartTime;
			final float darkness = Math.min((elapsedTime + renderTickCounter.getTickProgress(false)) * 0.05f * 0.05f, 1f);

			drawContext.fill(0, 0, drawContext.getScaledWindowWidth(), drawContext.getScaledWindowHeight(), ColorHelper.withAlpha(darkness, 0));
		}
	}
}
