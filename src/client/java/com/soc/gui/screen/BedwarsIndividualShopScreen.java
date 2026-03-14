package com.soc.gui.screen;

import com.soc.SocWars;
import com.soc.game.manager.bedwars.BedwarsShopCategory;
import com.soc.screenhandler.BedwarsIndividualShopScreenHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipBackgroundRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;

public class BedwarsIndividualShopScreen extends AbstractShopScreen<BedwarsIndividualShopScreenHandler> {
    private static final Identifier TEXTURE = Identifier.of(SocWars.MOD_ID, "textures/gui/container/bedwars_individual_shop_base.png");
    private static final Text CATEGORIES_TITLE = Text.translatable("shop.title.pages");
    private static final int CATEGORIES_TITLE_X = 8;
    private static final int CATEGORIES_TITLE_Y = 6;

    public BedwarsIndividualShopScreen(BedwarsIndividualShopScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        super.client = MinecraftClient.getInstance();

        super.backgroundHeight = 168;
        super.backgroundWidth = 216;
        super.titleX = 66;
        super.titleY = 6;
        super.playerInventoryTitleX = 48;
        super.playerInventoryTitleY = 74;
    }

    @Override
    protected Identifier getTexture() {
        return TEXTURE;
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        super.drawForeground(context, mouseX, mouseY);
        context.drawText(this.textRenderer, CATEGORIES_TITLE, CATEGORIES_TITLE_X, CATEGORIES_TITLE_Y, Colors.DARK_GRAY, false);

        this.resourceDisplays.forEach(display -> display.render(context, BedwarsIndividualShopScreen.super.textRenderer, BedwarsIndividualShopScreen.this.playerInventory));
    }
}