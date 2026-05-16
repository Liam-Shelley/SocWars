package com.soc.gui.screen;

import com.soc.SocWars;
import com.soc.game.manager.GameType;
import com.soc.screenhandler.KitScreenHandler;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.soc.gui.screen.KitBlockSelectionScreen.TEXTURES;
import static com.soc.lib.SocWarsLib.enumerate;
import static com.soc.lib.SocWarsLib.mapFromArray;

public class KitScreen extends HandledScreen<KitScreenHandler> {
    public static final Identifier TEXTURE = Identifier.of(SocWars.MOD_ID, "textures/gui/container/kit.png");

    private boolean initialised;

    private final Map<GameType, Boolean> allowedGameModes = HashMap.newHashMap(GameType.values().length);

    private Map<GameType, ToggleButtonWidget> gameSelectionButtons;

    public KitScreen(KitScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundHeight = 218;
        this.playerInventoryTitleY = 98;

        for (GameType value : GameType.values()) {
            this.allowedGameModes.put(value, true);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        final int i = (this.width - this.backgroundWidth) / 2;
        final int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0.0F, 0.0F, this.backgroundWidth, this.backgroundHeight, this.backgroundWidth, this.backgroundHeight);
    }

    @Override
    protected void init() {
        if (!this.initialised) {
            this.createWidgets();
            this.initialised = true;
        }

        enumerate(this.gameSelectionButtons.values(), (i, widget) -> {
            widget.setPosition(this.width / 2 + 80, this.height / 2 + i * 18);
            this.addDrawableChild(widget);
        });
    }

    private void createWidgets() {
        final AtomicInteger i = new AtomicInteger(this.height / 2);

        this.gameSelectionButtons = mapFromArray(GameType.values(), gameType -> {
            final ToggleButtonWidget toggleButtonWidget = new ToggleButtonWidget(this.width / 2 + 80, i.getAndAdd(18), 16, 16, false) {
                @Override
                public void setToggled(boolean toggled) {
                    super.setToggled(toggled);
                    KitScreen.this.allowedGameModes.put(gameType, toggled);
                }
            };
            toggleButtonWidget.setTextures(TEXTURES);
            return toggleButtonWidget;
        });
    }
}