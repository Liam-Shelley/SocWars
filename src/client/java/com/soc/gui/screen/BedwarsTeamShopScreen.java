package com.soc.gui.screen;

import com.soc.SocWars;
import com.soc.screenhandler.BedwarsTeamShopScreenHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.joml.Matrix3x2fStack;

import static com.soc.lib.SocWarsLib.ifNotNull;
import static net.minecraft.util.math.ColorHelper.lerp;

public class BedwarsTeamShopScreen extends AbstractShopScreen<BedwarsTeamShopScreenHandler> {
    private static final Identifier TEXTURE = Identifier.of(SocWars.MOD_ID, "textures/gui/container/bedwars_team_shop_base.png");
    private static final Text PAGE_TITLE = Text.translatable("shop.title.pages");
    private static final int PAGE_TITLE_X = 62;
    private static final int PAGE_TITLE_Y = 12;
    private static final int STOCK_TITLE_X = 150;
    private static final int STOCK_TITLE_Y = 12;

    public BedwarsTeamShopScreen(BedwarsTeamShopScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.client = MinecraftClient.getInstance();

        this.backgroundHeight = 202;
        this.backgroundWidth = 216;
        this.backgroundYOffset = -10;
        this.titleX = 66;
        this.titleY = 10000; //How about I get off of my bum and write a proper way to disable rendering the title
        this.playerInventoryTitleX = 48;
        this.playerInventoryTitleY = 98;
    }

    @Override
    protected Identifier getTexture() {
        return TEXTURE;
    }

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        final World world = this.client.world;
        this.drawFillBar(context, deltaTicks, 14, this.handler.getTrapProgress(world), 0xff22ee22);
        this.drawFillBar(context, deltaTicks, 36, this.handler.getAbilityProgress(world), 0xffeeee22);
    }

    private void drawFillBar(DrawContext context, float deltaTicks, int x, float fill, int colour) {
        if (fill >= 1f) deltaTicks = 0f;
        context.fill(this.x + x, this.y + 90, this.x + x + 16, this.y + 90 - (int)(86 * fill + deltaTicks), colour);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        super.drawForeground(context, mouseX, mouseY);
        context.drawText(this.textRenderer, PAGE_TITLE, PAGE_TITLE_X, PAGE_TITLE_Y, Colors.DARK_GRAY, false);
        context.drawText(this.textRenderer, this.handler.getCurrentCategoryName(), STOCK_TITLE_X, STOCK_TITLE_Y, Colors.DARK_GRAY, false);

        ifNotNull(this.handler.getDisplayCategory(), category -> {
            final Matrix3x2fStack matrices = context.getMatrices();
            matrices.pushMatrix();
            matrices.scale(0.8f);

            final int capacity = category.size();
            final int usedCapacity = this.handler.getStacksInDisplay();

            assert Formatting.DARK_RED.getColorValue() != null;
            assert Formatting.DARK_BLUE.getColorValue() != null;

            final int emptyColour = Formatting.DARK_RED.getColorValue();
            final int fullColour = Formatting.DARK_BLUE.getColorValue();

            context.drawText(this.textRenderer, String.valueOf(usedCapacity), 235, 94, lerp((float)usedCapacity / (float)capacity, emptyColour, fullColour) | 0xff000000, false);
            context.drawText(this.textRenderer, String.valueOf(capacity), 235, 104, fullColour | 0xff000000, false);
            context.drawHorizontalLine(234, 240, 102, fullColour | 0xff000000);

            matrices.popMatrix();
        });
    }
}