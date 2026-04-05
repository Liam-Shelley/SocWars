package com.soc.gui.screen.util;

import com.soc.game.manager.bedwars.shopitems.TooltipProvider;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record SplitText(List<OrderedText> lines, int width, int height) {
    public static final SplitText EMPTY = new SplitText(List.of(), 0, 0);

    public static SplitText split(@Nullable Text text, int maxWidth, TextRenderer textRenderer) {
        if (text == null) return EMPTY;

        final List<OrderedText> lines = textRenderer.wrapLines(text, maxWidth - 8);
        final int width = lines.stream().map(textRenderer::getWidth).max(Integer::compareTo).orElse(0) + 10;

        return new SplitText(lines, width, lines.size() * 12);
    }

    public static SplitText split(Object object, int maxWidth, TextRenderer textRenderer) {
        if (object instanceof TooltipProvider tooltipProvider) {
            return split(tooltipProvider.getTooltip(), maxWidth, textRenderer);
        } else {
            return EMPTY;
        }
    }

    public OrderedText getLine(int index) {
        return this.lines.get(index);
    }

    public void draw(DrawContext context, TextRenderer textRenderer, int x, int y) {
        for (int i = 0; i < this.lines.size(); i++) {
            context.drawText(textRenderer, this.getLine(i), x, y + 12 * i, 0xffffffff, true);
        }
    }
}