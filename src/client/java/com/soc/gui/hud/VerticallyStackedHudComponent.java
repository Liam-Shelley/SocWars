package com.soc.gui.hud;

public record VerticallyStackedHudComponent(int height, HudComponentRenderFunction renderFunction) implements HudComponentRenderFunction {
    @Override
    public void render(int x, int y) {
        this.renderFunction.render(x, y);
    }
}
