package com.soc.gui;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class ShopResourceDisplay {
    private final Item item;
    private final ItemStack defaultStack;
    private final int left;
    private final int top;

    public ShopResourceDisplay(Item item, int left, int top) {
        this.item = item;
        this.defaultStack = item.getDefaultStack();
        this.left = left;
        this.top = top;
    }

    public void render(DrawContext context, TextRenderer textRenderer, PlayerInventory inventory) {
        final int count = inventory.count(this.item);

        context.drawItem(this.defaultStack, left, top);
        context.drawText(
                textRenderer,
                count < 1000 ? Text.of(String.valueOf(count)) : Text.translatable("hud.over_999_items"),
                left + 20,
                top + 4,
                count < 1000 ? 0xffffffff : 0xff00ff00,
                true
        );
    }
}
