package com.soc.game;

import com.soc.SocWars;
import com.soc.lib.SparseVoxelOctree;
import com.soc.networking.s2c.BlockProtectionPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.text.DecimalFormat;

import static com.soc.lib.SocWarsLib.mapIfNotNull;
import static org.joml.Math.lerp;

public class BlockProtectionManagerAndHud {
    public static void initialise() {
        HudElementRegistry.addFirst(Identifier.of(SocWars.MOD_ID, "block_protection_hud"), BlockProtectionManagerAndHud::render);
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> INSTANCE.clearBlockProtection());
    }

    public static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("00");

    public static final Identifier MINI_PLAYER = Identifier.of(SocWars.MOD_ID, "textures/gui/hud/height_indicator_player.png");
    public static final Identifier BAR = Identifier.of(SocWars.MOD_ID, "textures/gui/hud/height_indicator_bar.png");

    public static final BlockProtectionManagerAndHud INSTANCE = new BlockProtectionManagerAndHud();

    private SparseVoxelOctree<Boolean> overlay = null;
    private BlockPos origin = null;
    private double minHeight = Integer.MIN_VALUE; //Yeah this is a bit gross but eh
    private double maxHeight = Integer.MIN_VALUE;

    private BlockProtectionManagerAndHud() {}

    public void setBlockProtection(BlockProtectionPayload packet) {
        this.overlay = packet.blockProtectionOverlay().orElse(null);
        this.origin = packet.origin().orElse(null);
        this.minHeight = packet.minHeight();
        this.maxHeight = packet.maxHeight();
    }

    public boolean isBlockProtected(BlockPos pos) {
        if (this.overlay == null) return false;

        return this.overlay.get(pos, this.origin);
    }

    public void clearBlockProtection() {
        this.overlay = null;
        this.origin = null;
        this.minHeight = Integer.MIN_VALUE;
        this.maxHeight = Integer.MIN_VALUE;
    }

    private static void render(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        if (INSTANCE.minHeight == Integer.MIN_VALUE) return;

        final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        final double playerHeight = mapIfNotNull(MinecraftClient.getInstance().player, Entity::getY, 0d);
        final double reverseLerp = (playerHeight - INSTANCE.minHeight - 0.25d) / (INSTANCE.maxHeight - INSTANCE.minHeight + 1d);

        final int yStart = 188;
        final int yEnd = 60;
        final int yHeight = (int)lerp(yStart, yEnd, reverseLerp);

        drawContext.drawTexturedQuad(BAR, 8, yEnd, 16, yStart, 0, 1, 0, 1);
        drawContext.drawTexturedQuad(MINI_PLAYER, 10, yHeight - 16, 18, yHeight, 0, 1, 0, 1);

        final int dTop = (int)Math.floor(INSTANCE.maxHeight - playerHeight + 1.5d);
        final int dBottom = (int)Math.floor(playerHeight - INSTANCE.minHeight + 0.5d);

        final int yTopText = yHeight > 46 ? Math.min(50, yHeight - 26) : 50;

        drawContext.drawText(textRenderer, Text.literal(NUMBER_FORMAT.format(dTop)).formatted(getColourForDistance(dTop)), 8, yTopText, 0xffffffff, true);
        drawContext.drawText(textRenderer, Text.literal(NUMBER_FORMAT.format(dBottom)).formatted(getColourForDistance(dBottom)), 8, 190, 0xffffffff, true);
    }

    private static Formatting getColourForDistance(int distance) {
        if (distance < 0) return Formatting.DARK_RED;
        return switch (distance) {
            case 0 -> Formatting.RED;
            case 1 -> Formatting.GOLD;
            case 2, 3 -> Formatting.YELLOW;
            case 4, 5 -> Formatting.GREEN;
            default -> Formatting.WHITE;
        };
    }
}
