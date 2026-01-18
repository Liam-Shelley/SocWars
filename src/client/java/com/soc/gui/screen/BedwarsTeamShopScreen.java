package com.soc.gui.screen;

import com.soc.SocWars;
import com.soc.game.manager.bedwars.BedwarsShopCategory;
import com.soc.game.manager.bedwars.ShopItem;
import com.soc.gui.ShopResourceDisplay;
import com.soc.screenhandler.BedwarsIndividualShopScreenHandler;
import com.soc.screenhandler.BedwarsTeamShopScreenHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.TooltipBackgroundRenderer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class BedwarsTeamShopScreen extends HandledScreen<BedwarsTeamShopScreenHandler> {
    private static final Identifier TEXTURE = Identifier.of(SocWars.MOD_ID, "textures/gui/container/bedwars_individual_shop_base.png");
    private static final Text CATEGORIES_TITLE = Text.translatable("shop.title.pages");
    private static final int CATEGORIES_TITLE_X = 8;
    private static final int CATEGORIES_TITLE_Y = 6;
    private static final Item[] RESOURCE_DISPLAY_ITEMS = new Item[] {Items.IRON_INGOT, Items.GOLD_INGOT, Items.DIAMOND, Items.EMERALD};

    private final PlayerInventory playerInventory;
    private final List<ShopResourceDisplay> resourceDisplays = new ArrayList<>(RESOURCE_DISPLAY_ITEMS.length);

    public BedwarsTeamShopScreen(BedwarsTeamShopScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        super.client = MinecraftClient.getInstance();

        super.backgroundHeight = 168;
        super.backgroundWidth = 216;
        super.titleX = 66;
        super.titleY = 6;
        super.playerInventoryTitleX = 48;
        super.playerInventoryTitleY = 74;

        this.playerInventory = inventory;

        for (int i = 0; i < RESOURCE_DISPLAY_ITEMS.length; i++) {
            this.resourceDisplays.add(new ShopResourceDisplay(RESOURCE_DISPLAY_ITEMS[i], 8, i * 18 + 86));
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        int i = (super.width - super.backgroundWidth) / 2;
        int j = (super.height - super.backgroundHeight) / 2;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0.0F, 0.0F, super.backgroundWidth, super.backgroundHeight, super.backgroundWidth, super.backgroundHeight);

        super.render(context, mouseX, mouseY, deltaTicks);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawMouseoverTooltip(DrawContext context, int x, int y) {
        if (super.focusedSlot != null && super.focusedSlot.hasStack()) {

            //if (this.handler.isStock(super.focusedSlot)) {
            //    this.drawCostTooltip(context, x, y, this.handler.getShopItem(super.focusedSlot));
            //    return;
            //}
//
            //if (this.handler.isCategory(super.focusedSlot)) {
            //    this.drawCategoryTooltip(context, x, y, this.handler.getShopCategory(super.focusedSlot));
            //    return;
            //}

            super.drawMouseoverTooltip(context, x, y);
        }
    }

    private void drawCostTooltip(DrawContext context, int x, int y, ShopItem<?> item) {
        final ItemStack icon = item.getIcon();
        {
            final MutableText name = item.getTooltipName().copy();
            name.append(item.affordabilitySuffix(playerInventory.player));
            int nameWidth = super.textRenderer.getWidth(name);

            TooltipBackgroundRenderer.render(context, x + 12, y - 12, Math.max(nameWidth, 65), 31, icon.get(DataComponentTypes.TOOLTIP_STYLE));

            context.drawText(super.textRenderer, name, x + 12, y - 12, 0xffffffff, true);
        }

        context.getMatrices().pushMatrix();
        context.getMatrices().scaleAround(0.8f, x, y);
        item.getCost().forEach((costItem, costAmount, i) -> {
            final boolean canAfford = this.playerInventory.count(costItem) >= costAmount;
            {
                final int xStart = x + i * 20 + 16;
                final int yStart = y + 2;

                context.drawItem(costItem.getDefaultStack(), xStart, yStart);

                if (!canAfford) {
                    context.fill(xStart, yStart, xStart + 16, yStart + 16, 0xaa000000);
                }
            }
            {
                String costString = String.valueOf(costAmount);
                context.drawText(super.textRenderer, costString, x + i * 20 + 34 - costString.length() * 6, y + 16, canAfford ? 0xefffffff : 0xefdf1020, true);
            }
        });

        context.getMatrices().popMatrix();
    }

    private void drawCategoryTooltip(DrawContext context, int x, int y, BedwarsShopCategory category) {
        TooltipBackgroundRenderer.render(context, x + 12, y - 12, super.textRenderer.getWidth(category.getName()), 8, null);
        context.drawText(super.textRenderer, category.getName(), x + 12, y - 12, 0xffffffff, true);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        super.drawForeground(context, mouseX, mouseY);
        context.drawText(this.textRenderer, CATEGORIES_TITLE, CATEGORIES_TITLE_X, CATEGORIES_TITLE_Y, Colors.DARK_GRAY, false);

        this.resourceDisplays.forEach(display -> display.render(context, BedwarsTeamShopScreen.super.textRenderer, BedwarsTeamShopScreen.this.playerInventory));
    }

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {

    }
}