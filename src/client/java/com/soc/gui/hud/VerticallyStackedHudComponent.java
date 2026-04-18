package com.soc.gui.hud;

public interface VerticallyStackedHudComponent extends HudComponentRenderFunction {
    int getSize();

    int priority();
}
