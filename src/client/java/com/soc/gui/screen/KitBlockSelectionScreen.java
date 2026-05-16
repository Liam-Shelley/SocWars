package com.soc.gui.screen;

import com.soc.SocWars;
import com.soc.blocks.blockentities.KitBlockEntity;
import com.soc.game.manager.GameType;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.soc.lib.SocWarsLib.mapFromArray;

public class KitBlockSelectionScreen extends Screen {
    public static final ButtonTextures TEXTURES = new ButtonTextures(Identifier.of(SocWars.MOD_ID, ""), Identifier.of(SocWars.MOD_ID, ""));
    private Map<GameType, ToggleButtonWidget> gameSelectionButtons;

    private boolean initialised;

    public KitBlockSelectionScreen(KitBlockEntity blockEntity) {
        super(Text.translatable("screen.kit_block_selection"));
    }

    @Override
    protected void init() {
        if (!this.initialised) {
            this.createWidgets();
            this.initialised = true;
        }
    }

    private void createWidgets() {
        final AtomicInteger i = new AtomicInteger(100);

        this.gameSelectionButtons = mapFromArray(GameType.values(), gameType -> {
            final ToggleButtonWidget toggleButtonWidget = new ToggleButtonWidget(100, i.getAndAdd(18), 16, 16, false);
            toggleButtonWidget.setTextures(TEXTURES);
            return toggleButtonWidget;
        });
    }
}
