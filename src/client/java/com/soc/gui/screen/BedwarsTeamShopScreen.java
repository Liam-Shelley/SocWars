package com.soc.gui.screen;

import com.soc.SocWars;
import com.soc.screenhandler.BedwarsTeamShopScreenHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class BedwarsTeamShopScreen extends AbstractShopScreen<BedwarsTeamShopScreenHandler> {
    private static final Identifier TEXTURE = Identifier.of(SocWars.MOD_ID, "textures/gui/container/bedwars_team_shop_base.png");
    private static final Text TRAPS_TITLE = Text.translatable("shop.title.traps");
    private static final int TRAPS_TITLE_X = 26;
    private static final int TRAPS_TITLE_Y = 12;
    private static final Text ABILITIES_TITLE = Text.translatable("shop.title.abilities");
    private static final int ABILITIES_TITLE_X = 140;
    private static final int ABILITIES_TITLE_Y = 12;

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
        super.handler.getTrapProgress(world);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        super.drawForeground(context, mouseX, mouseY);
        context.drawText(this.textRenderer, TRAPS_TITLE, TRAPS_TITLE_X, TRAPS_TITLE_Y, Colors.DARK_GRAY, false);
        context.drawText(this.textRenderer, ABILITIES_TITLE, ABILITIES_TITLE_X, ABILITIES_TITLE_Y, Colors.DARK_GRAY, false);
    }
}