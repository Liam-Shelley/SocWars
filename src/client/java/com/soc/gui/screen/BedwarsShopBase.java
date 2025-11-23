package com.soc.gui.screen;

import com.soc.SocWars;
import com.soc.screenhandler.BedwarsShopScreenHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class BedwarsShopBase extends HandledScreen<BedwarsShopScreenHandler> {
    private class ResourceDisplay {
        private final Item item;
        private final ItemStack defaultStack;
        private final int left;
        private final int top;

        protected ResourceDisplay(Item item, int left, int top) {
            this.item = item;
            this.defaultStack = item.getDefaultStack();
            this.left = left;
            this.top = top;
        }

        void render(DrawContext context) {
            final int count = BedwarsShopBase.this.playerInventory.count(this.item);

            context.drawItem(this.defaultStack, left, top);
            context.drawText(
                    BedwarsShopBase.super.textRenderer,
                    Text.of(count < 1000 ? String.valueOf(count) : "Big"),
                    left + 20,
                    top + 4,
                    count < 1000 ? 0xffffffff : 0xff00ff00,
                    true
            );
        }
    }

    private static final Identifier TEXTURE = Identifier.of(SocWars.MOD_ID, "textures/gui/container/bedwars_shop_base.png");
    private static final Text CATEGORIES_TITLE = Text.translatable("shop.title.pages");
    private static final int CATEGORIES_TITLE_X = 8;
    private static final int CATEGORIES_TITLE_Y = 6;
    private static final Item[] RESOURCE_DISPLAY_ITEMS = new Item[] {Items.IRON_INGOT, Items.GOLD_INGOT, Items.DIAMOND, Items.EMERALD};

    private final PlayerInventory playerInventory;
    private final List<ResourceDisplay> resourceDisplays = new ArrayList<>(RESOURCE_DISPLAY_ITEMS.length);

    public BedwarsShopBase(BedwarsShopScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        super.client = MinecraftClient.getInstance();

        super.backgroundHeight = 186;
        super.backgroundWidth = 216;
        super.titleX = 48;
        super.titleY = 6;
        super.playerInventoryTitleX = 48;
        super.playerInventoryTitleY = 92;

        this.playerInventory = inventory;

        for (int i = 0; i < RESOURCE_DISPLAY_ITEMS.length; i++) {
            this.resourceDisplays.add(new ResourceDisplay(RESOURCE_DISPLAY_ITEMS[i], 8, i * 18 + 104));
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        int i = (super.width - super.backgroundWidth) / 2;
        int j = (super.height - super.backgroundHeight) / 2;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0.0F, 0.0F, super.backgroundWidth, super.backgroundHeight, super.backgroundWidth, super.backgroundHeight);

        super.render(context, mouseX, mouseY, deltaTicks);
        super.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        super.drawForeground(context, mouseX, mouseY);
        context.drawText(this.textRenderer, CATEGORIES_TITLE, CATEGORIES_TITLE_X, CATEGORIES_TITLE_Y, Colors.DARK_GRAY, false);

        this.resourceDisplays.forEach(display -> display.render(context));
    }

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {

    }
}
