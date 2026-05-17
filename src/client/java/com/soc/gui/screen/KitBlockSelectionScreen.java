package com.soc.gui.screen;

import com.soc.SocWars;
import com.soc.blocks.blockentities.KitBlockEntity;
import com.soc.game.manager.GameType;
import com.soc.gui.widget.ToggleButtonWidget;
import com.soc.networking.c2s.KitSelectionPayload;
import com.soc.networking.helper.BlockLocation;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.soc.lib.SocWarsLib.enumerate;
import static com.soc.lib.SocWarsLib.mapEnumerate;

public class KitBlockSelectionScreen extends Screen {
    public static final Identifier TEXTURE = Identifier.of(SocWars.MOD_ID, "textures/gui/container/kit_block_selection.png");
    public static final ButtonTextures TEXTURES = new ButtonTextures(Identifier.of(SocWars.MOD_ID, ""), Identifier.of(SocWars.MOD_ID, ""));
    public static final ButtonTextures INSTANT_TEXTURES = new ButtonTextures(Identifier.of(SocWars.MOD_ID, "widget/leave_button"), Identifier.ofVanilla("widget/button_disabled"), Identifier.ofVanilla("widget/button_highlighted"));

    private final KitBlockEntity blockEntity;
    private boolean initialised;

    private final Map<GameType, Boolean> selectedGameTypes;

    private List<ToggleButtonWidget> gameSelectionButtons;
    private List<TexturedButtonWidget> gameInstantSelectionButtons;
    private ButtonWidget selectKitButton;


    public KitBlockSelectionScreen(KitBlockEntity blockEntity) {
        super(Text.translatable("screen.kit_block_selection"));
        this.blockEntity = blockEntity;
        this.selectedGameTypes = HashMap.newHashMap(GameType.values().length);

        for (GameType value : blockEntity.getAllowedGameTypesList()) {
            this.selectedGameTypes.put(value, true);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        {
            final int i = (this.width - 176) / 2;
            final int j = (this.height - 218) / 2;
            context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0.0F, 0.0F, 176, 218, 176, 218);
        }

        super.render(context, mouseX, mouseY, deltaTicks);

        enumerate(this.blockEntity.getAllowedGameTypesList(), (i, gameType) -> {
            final MutableText variantName = gameType.getCompactVariantName();
            final boolean enabled = this.selectedGameTypes.get(gameType);
            variantName.append(Text.translatable(enabled ? "hud.tick" : "hud.cross"));

            context.drawText(this.textRenderer, variantName, this.width / 2 - 78, this.height / 2 - 86 + i * 18, enabled ? 0xff11ee22 : 0xffee1122, true);
        });
    }

    @Override
    protected void init() {
        super.init();

        if (!this.initialised) {
            this.createWidgets();
            this.initialised = true;
        }

        enumerate(this.gameSelectionButtons, (i, widget) -> {
            widget.setPosition(this.width / 2 - 49, this.height / 2 - 90 + i * 18);
            this.addDrawableChild(widget);
        });
        enumerate(this.gameInstantSelectionButtons, (i, widget) -> {
            widget.setPosition(this.width / 2 - 31, this.height / 2 - 90 + i * 18);
            this.addDrawableChild(widget);
        });

        this.selectKitButton.setPosition(this.width / 2 - 31, this.height / 2);
        this.addDrawableChild(this.selectKitButton);
    }

    private void createWidgets() {
        this.gameSelectionButtons = mapEnumerate(this.blockEntity.getAllowedGameTypesList(), (i, gameType) -> new ToggleButtonWidget(this.width / 2 - 49, this.height / 2 - 90 + i * 18, 16, 16, true, isToggled -> {
            this.selectedGameTypes.put(gameType, isToggled);
        }, TEXTURES)).toList();
        this.gameInstantSelectionButtons = mapEnumerate(this.blockEntity.getAllowedGameTypesList(), (i, gameType) -> new TexturedButtonWidget(this.width / 2 - 31, this.height / 2 - 90 + i * 18, 16, 16, INSTANT_TEXTURES, button -> {
            ClientPlayNetworking.send(new KitSelectionPayload(new BlockLocation(this.blockEntity), List.of(gameType)));
            MinecraftClient.getInstance().setScreen(null);
        })).toList();

        this.selectKitButton = ButtonWidget.builder(Text.translatable("aaa"), widget -> {
            ClientPlayNetworking.send(new KitSelectionPayload(new BlockLocation(this.blockEntity), this.selectedGameTypes.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList()));
            MinecraftClient.getInstance().setScreen(null);
        }).dimensions(this.width / 2 - 31, this.height / 2, 50, 16).build();
    }
}
