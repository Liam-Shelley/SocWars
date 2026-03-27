package com.soc.gui.screen;

import com.soc.game.manager.bedwars.BedwarsShopCategory;
import com.soc.game.manager.bedwars.shopitems.DisplayShopItem;
import com.soc.game.manager.bedwars.shopitems.ShopItem;
import com.soc.gui.ShopResourceDisplay;
import com.soc.resourcedata.deserialisation.Cost;
import com.soc.screenhandler.AbstractCategoriesShopScreenHandler;
import com.soc.screenhandler.AbstractShopScreenHandler;
import com.soc.screenhandler.slots.ShopSlot;
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
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractShopScreen<T extends AbstractShopScreenHandler> extends HandledScreen<T> {
    protected static final Item[] RESOURCE_DISPLAY_ITEMS = new Item[] {Items.IRON_INGOT, Items.GOLD_INGOT, Items.DIAMOND, Items.EMERALD};
    protected final List<ShopResourceDisplay> resourceDisplays = new ArrayList<>(RESOURCE_DISPLAY_ITEMS.length);

    protected final PlayerInventory playerInventory;

    protected int backgroundYOffset = 0;

    public AbstractShopScreen(T handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.playerInventory = inventory;

        for (int i = 0; i < RESOURCE_DISPLAY_ITEMS.length; i++) {
            this.resourceDisplays.add(new ShopResourceDisplay(RESOURCE_DISPLAY_ITEMS[i], 8, i * 18 + handler.getPlayerInventorySlotHeight()));
        }
    }

    @Override
    protected void drawMouseoverTooltip(DrawContext context, int x, int y) {
        if (super.focusedSlot instanceof ShopSlot<?> shopSlot && super.focusedSlot.hasStack()) {
            switch(shopSlot.getSlotType()) {
                case STOCK -> this.drawCostTooltip(context, x, y, this.handler.getShopItem(shopSlot));
                case CATEGORY -> this.drawCategoryTooltip(context, x, y, ((AbstractCategoriesShopScreenHandler)this.handler).getShopCategory(super.focusedSlot));
                case DISPLAY -> {
                    final ShopItem<?> shopItem = this.handler.getShopItem(shopSlot);
                    if (shopItem instanceof DisplayShopItem displayShopItem) {
                        this.drawDisplayTooltip(context, x, y, displayShopItem);
                    }
                }
                default -> super.drawMouseoverTooltip(context, x, y);
            }
        }
    }

    protected void drawCostTooltip(DrawContext context, int x, int y, ShopItem<?> item) {
        final ItemStack icon = item.getIcon();
        {
            final MutableText name = item.getDisplayName().copy();
            name.append(item.affordabilitySuffix(this.playerInventory.player));
            int nameWidth = super.textRenderer.getWidth(name);

            TooltipBackgroundRenderer.render(context, x + 12, y - 12, Math.max(nameWidth, 65), 31, icon.get(DataComponentTypes.TOOLTIP_STYLE));

            context.drawText(super.textRenderer, name, x + 12, y - 12, 0xffffffff, true);

            this.drawCostIcons(context, x, y, item.getCost());
        }
    }

    protected void drawCostIcons(DrawContext context, int x, int y, Cost cost) {
        context.getMatrices().pushMatrix();
        context.getMatrices().scaleAround(0.8f, x, y);
        cost.forEach((costItem, costAmount, i) -> {
            final boolean canAfford = this.playerInventory.count(costItem) >= costAmount;
            {
                final int xStart = x + i * 20 + 16;
                final int yStart = y + 2;

                context.drawItem(costItem.getDefaultStack(), xStart, yStart);

                if (!canAfford || costAmount == 0) {
                    context.fill(xStart, yStart, xStart + 16, yStart + 16, 0xaa000000);
                }
            }
            {
                final String costString = String.valueOf(costAmount);
                final int colour;
                if (costAmount == 0) {
                    colour = 0xef444444;
                } else if (canAfford) {
                    colour = 0xef10df20;
                } else {
                    colour = 0xefdf1020;
                }
                context.drawText(super.textRenderer, costString, x + i * 20 + 34 - costString.length() * 6, y + 16, colour, true);
            }
        });

        context.getMatrices().popMatrix();
    }

    private void drawCategoryTooltip(DrawContext context, int x, int y, BedwarsShopCategory category) {
        TooltipBackgroundRenderer.render(context, x + 12, y - 12, super.textRenderer.getWidth(category.getName()), 8, null);
        context.drawText(super.textRenderer, category.getName(), x + 12, y - 12, 0xffffffff, true);
    }

    //Yeah maybe I fix this some time later ey
    protected void drawDisplayTooltip(DrawContext context, int x, int y, DisplayShopItem shopItem) {}

    protected abstract Identifier getTexture();

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        final int i = (super.width - super.backgroundWidth) / 2;
        final int j = (super.height - super.backgroundHeight) / 2 + this.backgroundYOffset;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, this.getTexture(), i, j, 0.0F, 0.0F, super.backgroundWidth, super.backgroundHeight, super.backgroundWidth, super.backgroundHeight);

        super.render(context, mouseX, mouseY, deltaTicks);

        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        super.drawForeground(context, mouseX, mouseY);
        this.resourceDisplays.forEach(display -> display.render(context, super.textRenderer, this.playerInventory));
    }

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {}
}
