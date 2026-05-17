package com.soc.gui.screen;

import com.soc.SocWars;
import com.soc.blocks.blockentities.KitBlockEntity;
import com.soc.game.manager.GameType;
import com.soc.gui.widget.ToggleButtonWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.soc.lib.SocWarsLib.enumerate;
import static com.soc.lib.SocWarsLib.mapEnumerate;

public class KitBlockSelectionScreen extends Screen {
    public static final ButtonTextures TEXTURES = new ButtonTextures(Identifier.of(SocWars.MOD_ID, ""), Identifier.of(SocWars.MOD_ID, ""));

    private final KitBlockEntity blockEntity;
    private boolean initialised;

    private final Map<GameType, Boolean> selectedGameModes;

    private List<ToggleButtonWidget> gameSelectionButtons;
    private ButtonWidget selectKitButton;


    public KitBlockSelectionScreen(KitBlockEntity blockEntity) {
        super(Text.translatable("screen.kit_block_selection"));
        this.blockEntity = blockEntity;
        this.selectedGameModes = HashMap.newHashMap(GameType.values().length);

        for (GameType value : GameType.values()) {
            this.selectedGameModes.put(value, true);
        }
    }

    @Override
    protected void init() {
        super.init();

        if (!this.initialised) {
            this.createWidgets();
            this.initialised = true;
        }

        enumerate(this.gameSelectionButtons, (i, widget) -> {
            widget.setPosition(this.width / 2 - 31, this.height / 2 - 90 + i * 18);
            this.addDrawableChild(widget);
        });

        this.selectKitButton.setPosition(this.width / 2 - 31, this.height / 2);
        this.addDrawableChild(this.selectKitButton);
    }

    private void createWidgets() {
        this.gameSelectionButtons = mapEnumerate(this.blockEntity.getAllowedGameTypes(), (i, gameType) -> new ToggleButtonWidget(this.width / 2 - 31, this.height / 2 - 90 + i * 18, 16, 16, true, isToggled -> {
            this.selectedGameModes.put(gameType, isToggled);
        }, TEXTURES)).toList();

        this.selectKitButton = ButtonWidget.builder(Text.translatable("aaa"), widget -> {
            MinecraftClient.getInstance().setScreen(null);
        }).dimensions(this.width / 2 - 80, this.height / 2, 50, 16).build();
    }
}
