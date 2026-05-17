package com.soc.gui.screen;

import com.soc.SocWars;
import com.soc.blocks.blockentities.KitBlockEntity;
import com.soc.game.manager.GameType;
import com.soc.gui.widget.ToggleButtonWidget;
import com.soc.networking.c2s.KitBlockUpdatePayload;
import com.soc.networking.helper.BlockLocation;
import com.soc.screenhandler.KitBlockCreationScreenHandler;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.*;

import static com.soc.gui.screen.KitBlockSelectionScreen.TEXTURES;
import static com.soc.lib.SocWarsLib.enumerate;
import static com.soc.lib.SocWarsLib.mapFromArrayEnumerate;

public class KitBlockCreationScreen extends HandledScreen<KitBlockCreationScreenHandler> {
    public static final Identifier TEXTURE = Identifier.of(SocWars.MOD_ID, "textures/gui/container/kit.png");

    private boolean initialised;

    private final Map<GameType, Boolean> allowedGameTypes = HashMap.newHashMap(GameType.values().length);

    private Map<GameType, ToggleButtonWidget> gameSelectionButtons;

    public KitBlockCreationScreen(KitBlockCreationScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundHeight = 218;
        this.playerInventoryTitleY = 98;

        for (GameType value : GameType.values()) {
            this.allowedGameTypes.put(value, false);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks) {
        super.render(context, mouseX, mouseY, deltaTicks);
        this.drawMouseoverTooltip(context, mouseX, mouseY);

        enumerate(GameType.values(), (i, gameType) -> {
            final MutableText variantName = gameType.getCompactVariantName();
            final boolean enabled = this.allowedGameTypes.get(gameType);
            variantName.append(Text.translatable(enabled ? "hud.tick" : "hud.cross"));

            context.drawText(this.textRenderer, variantName, this.width / 2 - 78, this.height / 2 - 86 + i * 18, enabled ? 0xff11ee22 : 0xffee1122, true);
        });
    }

    @Override
    protected void drawBackground(DrawContext context, float deltaTicks, int mouseX, int mouseY) {
        final int i = (this.width - this.backgroundWidth) / 2;
        final int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0.0F, 0.0F, this.backgroundWidth, this.backgroundHeight, this.backgroundWidth, this.backgroundHeight);
    }
    @Override
    protected void init() {
        super.init();

        if (!this.initialised) {
            this.createWidgets();
            this.initialised = true;
        }

        enumerate(this.gameSelectionButtons.values(), (i, widget) -> {
            widget.setPosition(this.width / 2 - 31, this.height / 2 - 90 + i * 18);
            this.addDrawableChild(widget);
        });
    }

    private void createWidgets() {
        this.gameSelectionButtons = mapFromArrayEnumerate(GameType.values(), (i, gameType) -> new ToggleButtonWidget(this.width / 2 - 31, this.height / 2 - 90 + i * 18, 16, 16, false, isToggled -> {
            this.allowedGameTypes.put(gameType, isToggled);
            this.sync();
        }, TEXTURES));
    }

    private void sync() {
        ClientPlayNetworking.send(new KitBlockUpdatePayload(new BlockLocation(this.handler.getBlockEntity()), this.getAllowedGameTypes()));
    }

    private List<GameType> getAllowedGameTypes() {
        return this.allowedGameTypes.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).toList();
    }

    public void setBlockEntity(KitBlockEntity blockEntity) {
        this.getScreenHandler().setBlockEntity(blockEntity);

        for (GameType gameType : GameType.values()) {
            this.gameSelectionButtons.get(gameType).setToggled(blockEntity.allowsGameType(gameType));
        }
    }
}

