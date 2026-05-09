package com.soc.gui.hud;

import com.soc.SocWars;
import com.soc.networking.s2c.JumpscarePayload;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class JumpscareHud { //Just awful class my god
    public static void initialise() {
        HudElementRegistry.addFirst(Identifier.of(SocWars.MOD_ID, "jumpscare_hud"), JumpscareHud::render);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (INSTANCE != null && INSTANCE.remainingTime-- <= 0) INSTANCE = null;
        });
    }

    @Nullable
    private static JumpscareHud INSTANCE;

    private final Identifier currentImage;
    private int remainingTime;

    private JumpscareHud(Identifier currentImage, int remainingTime) {
        this.currentImage = currentImage;
        this.remainingTime = remainingTime;
    }

    private static void render(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        if (INSTANCE != null) drawContext.drawTexturedQuad(INSTANCE.currentImage, 0, 0, drawContext.getScaledWindowWidth(), drawContext.getScaledWindowHeight(), 0f, 0f, 1f, 1f);
    }

    public static void triggerJumpscare(JumpscarePayload payload) {
        INSTANCE = new JumpscareHud(payload.image(), payload.time());
        MinecraftClient.getInstance().player.playSound(payload.sound());
    }
}
