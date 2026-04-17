package com.soc.game;

import com.soc.SocWars;
import com.soc.gui.hud.VerticallyStackedHudComponent;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.util.Identifier;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.List;

public class SidebarHud {
    public static void initialise() {
        HudElementRegistry.addFirst(Identifier.of(SocWars.MOD_ID, "sidebar_hud"), SidebarHud::render);
    }

    private static final List<Reference<VerticallyStackedHudComponent>> ELEMENTS = new ArrayList<>();

    private SidebarHud() {}

    private static void render(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        //ELEMENTS.stream().filter()
    }
}
