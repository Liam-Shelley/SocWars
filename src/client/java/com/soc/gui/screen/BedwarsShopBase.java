package com.soc.gui.screen;

import com.soc.SocWars;
import com.soc.gui.ShopResourceDisplay;
import com.soc.items.components.ModComponents;
import com.soc.items.components.ShopCostComponent;
import com.soc.screenhandler.BedwarsShopScreenHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.TooltipBackgroundRenderer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Vector2ic;

import java.util.ArrayList;
import java.util.List;

public class BedwarsShopBase extends HandledScreen<BedwarsShopScreenHandler> {
    private static final Identifier TEXTURE = Identifier.of(SocWars.MOD_ID, "textures/gui/container/bedwars_shop_base.png");
    private static final Text CATEGORIES_TITLE = Text.translatable("shop.title.pages");
    private static final int CATEGORIES_TITLE_X = 8;
    private static final int CATEGORIES_TITLE_Y = 6;
    private static final Item[] RESOURCE_DISPLAY_ITEMS = new Item[] {Items.IRON_INGOT, Items.GOLD_INGOT, Items.DIAMOND, Items.EMERALD};

    private final PlayerInventory playerInventory;
    private final List<ShopResourceDisplay> resourceDisplays = new ArrayList<>(RESOURCE_DISPLAY_ITEMS.length);

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
            this.resourceDisplays.add(new ShopResourceDisplay(RESOURCE_DISPLAY_ITEMS[i], 8, i * 18 + 104));
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
        if (this.focusedSlot != null && this.focusedSlot.hasStack()) {
            final ItemStack stack = this.focusedSlot.getStack();

            final ShopCostComponent costComponent = stack.get(ModComponents.SHOP_COST_COMPONENT);
            if (costComponent != null) {
                this.drawCostComponentTooltip(context, x, y, stack, costComponent);
                return;
            }

            super.drawMouseoverTooltip(context, x, y);
        }
    }

    private void drawCostComponentTooltip(DrawContext context, int x, int y, ItemStack stack, ShopCostComponent costComponent) {
        TooltipBackgroundRenderer.render(context, x + 12, y - 12, 80, 30, stack.get(DataComponentTypes.TOOLTIP_STYLE));
        context.drawText(super.textRenderer, this.getItemNameText(stack), x + 12, y - 12, 0xffffffff, true);

        context.getMatrices().pushMatrix();
        context.getMatrices().scaleAround(0.8f, x, y);

        final List<Pair<Item, Integer>> costs = costComponent.getCosts();
        for (int i = 0; i < costs.size(); i++) {
            final Pair<Item, Integer> cost = costs.get(i);
            context.drawItem(cost.getLeft().getDefaultStack(), x + 16 + i * 18, y + 2);
        }
        context.getMatrices().popMatrix();
    }

    private Text getItemNameText(ItemStack stack) {
        return Text.literal(stack.getCount() + "x ").formatted(Formatting.DARK_PURPLE).append(stack.getItemName().copy().formatted(stack.getRarity().getFormatting()));
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        super.drawForeground(context, mouseX, mouseY);
        context.drawText(this.textRenderer, CATEGORIES_TITLE, CATEGORIES_TITLE_X, CATEGORIES_TITLE_Y, Colors.DARK_GRAY, false);

        this.resourceDisplays.forEach(display -> display.render(context, BedwarsShopBase.super.textRenderer, BedwarsShopBase.this.playerInventory));
    }

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {

    }
}
