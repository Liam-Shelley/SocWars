package com.soc.gui.hud;

import com.soc.networking.helper.SkywarsTeam;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.soc.gui.hud.SidebarHud.BACKGROUND_COLOUR;
import static com.soc.gui.hud.SidebarHud.SIDEBAR_WIDTH;
import static net.minecraft.util.math.ColorHelper.lerp;

public class SkywarsTeamsHud implements VerticallyStackedHudComponent { //Maybe I'll subclass this and BedwarsTeamsHud under GameTeamsHud or something and make a lof of the drawing code common
    public static void initialise() {
        SidebarHud.addHudElement(INSTANCE);
    }

    private static final @NotNull Reference<SkywarsTeamsHud> INSTANCE = new Reference<>(null);

    private final Map<DyeColor, SkywarsTeam> teams;
    private final Map<UUID, Identifier> skinTextures = new HashMap<>();

    private SkywarsTeamsHud(Map<DyeColor, SkywarsTeam> teams) {
        this.teams = teams;
        this.teams.values().forEach(team -> {
                final PlayerEntity player = MinecraftClient.getInstance().world.getPlayerByUuid(team.getPlayer());
                if (player == null) return;
                MinecraftClient.getInstance().getSkinProvider().fetchSkinTextures(player.getGameProfile()).whenCompleteAsync((optionalTextures, throwable) ->
                        optionalTextures.ifPresent(textures -> this.skinTextures.put(team.getPlayer(), textures.texture()))
                );
        });
    }

    public static void joinGame(Map<DyeColor, SkywarsTeam> teams) {
        INSTANCE.set(new SkywarsTeamsHud(teams));
    }

    public static void leaveGame() {
        INSTANCE.annul();
    }

    public static void eliminateTeam(DyeColor team) {
        INSTANCE.ifPresent(instance -> instance.teams.get(team).eliminate());
    }

    @Override
    public void render(DrawContext drawContext, RenderTickCounter renderTickCounter, TextRenderer textRenderer, int x, int y) {
        final int halfSidebarWidth = SIDEBAR_WIDTH >> 1;

        int i = 0;
        for (DyeColor team : this.teams.entrySet().stream().sorted(Comparator.comparingInt(entry -> entry.getValue().getLives())).map(Map.Entry::getKey).toList()) {
            final int xOrigin = x + halfSidebarWidth * (i % 2);
            final int yOrigin = y + 40 * (i++ >> 1);

            int teamColour = (team.getSignColor() & 0x00ffffff | 0x99000000);
            if (!this.teams.get(team).isAlive()) teamColour = lerp(0.6f, teamColour, BACKGROUND_COLOUR);

            drawContext.fill(xOrigin, yOrigin, xOrigin + halfSidebarWidth, yOrigin + 40, teamColour);

            this.drawTeamText(drawContext, team, textRenderer, xOrigin + halfSidebarWidth, yOrigin);
            this.drawTeamHeads(drawContext, team, xOrigin + halfSidebarWidth, yOrigin);
        }
    }

    private void drawTeamText(DrawContext drawContext, DyeColor team, TextRenderer textRenderer, int x, int y) {
        final Text teamBaseString = Text.translatable("color.minecraft." + team.asString());
        drawContext.drawText(textRenderer, teamBaseString, x - (SIDEBAR_WIDTH >> 1) + 8, y + 4, 0xffffffff, true);

        final Matrix3x2fStack matrices = drawContext.getMatrices();
        matrices.pushMatrix();
        matrices.scale(1.5f);

        drawContext.drawText(textRenderer, String.valueOf(this.teams.get(team).getLives()), ((x - (SIDEBAR_WIDTH >> 1) + 42) * 2 / 3), (y + 20) * 2 / 3, 0xffffffff, true);

        matrices.popMatrix();
    }

    private void drawTeamHeads(DrawContext drawContext, DyeColor team, int x, int y) {
        final int headX = x - (SIDEBAR_WIDTH >> 1) + 8;
        final int headY = y + 16;

        final SkywarsTeam skywarsTeam = this.teams.get(team);

        final Identifier skinTexture = this.skinTextures.get(skywarsTeam.getPlayer());

        if (skinTexture == null) {
            drawContext.fill(headX, headY, headX + 20, headY + 20, 0xffff0000);
        } else {
            PlayerSkinDrawer.draw(drawContext, skinTexture, headX, headY, 20, true, false, 0xffffffff);
        }
    }

    @Override
    public int getSize() {
        return ((this.teams.size() + 1) >> 1) * 40;
    }

    @Override
    public int priority() {
        return 0;
    }
}
