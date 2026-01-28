package com.soc.gui.screen.util;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.util.List;

public record HoverTooltipRenderer(List<HoverTooltipChunk> chunks) {
    //TODO: maybe actually write everything in this package at some point to make my text rendering much easier

    public void draw(DrawContext context, TextRenderer textRenderer, int x, int y) {
        this.chunks.forEach(HoverTooltipChunk::height);
    }
}
