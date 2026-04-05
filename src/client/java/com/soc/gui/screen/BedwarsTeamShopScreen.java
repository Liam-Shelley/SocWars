package com.soc.gui.screen;

import com.soc.SocWars;
import com.soc.game.manager.bedwars.shopitems.DisplayShopItem;
import com.soc.screenhandler.BedwarsTeamShopScreenHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.tooltip.TooltipBackgroundRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BedwarsTeamShopScreen extends AbstractShopScreen<BedwarsTeamShopScreenHandler> {
    private static final Identifier TEXTURE = Identifier.of(SocWars.MOD_ID, "textures/gui/container/bedwars_team_shop_base.png");
    private static final Text PAGE_TITLE = Text.translatable("shop.title.pages");
    private static final int PAGE_TITLE_X = 62;
    private static final int PAGE_TITLE_Y = 12;
    private static final int STOCK_TITLE_X = 150;
    private static final int STOCK_TITLE_Y = 12;

    public BedwarsTeamShopScreen(BedwarsTeamShopScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        super.client = MinecraftClient.getInstance();

        super.backgroundHeight = 202;
        super.backgroundWidth = 216;
        super.backgroundYOffset = -10;
        super.titleX = 66;
        super.titleY = 10000; //How about I get off of my bum and write a proper way to disable rendering the title
        super.playerInventoryTitleX = 48;
        super.playerInventoryTitleY = 98;
    }

    @Override
    protected Identifier getTexture() {
        return TEXTURE;
    }

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        final World world = super.client.world;
        this.drawFillBar(context, deltaTicks, 14, super.handler.getTrapProgress(world), 0xff22ee22);
        this.drawFillBar(context, deltaTicks, 36, super.handler.getAbilityProgress(world), 0xffeeee22);
    }

    private void drawFillBar(DrawContext context, float deltaTicks, int x, float fill, int colour) {
        if (fill >= 1f) deltaTicks = 0f;
        context.fill(super.x + x, super.y + 90, super.x + x + 16, super.y + 90 - (int)(86 * fill + deltaTicks), colour);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        super.drawForeground(context, mouseX, mouseY);
        context.drawText(super.textRenderer, PAGE_TITLE, PAGE_TITLE_X, PAGE_TITLE_Y, Colors.DARK_GRAY, false);
        context.drawText(super.textRenderer, super.handler.getCurrentCategoryName(), STOCK_TITLE_X, STOCK_TITLE_Y, Colors.DARK_GRAY, false);
    }
}