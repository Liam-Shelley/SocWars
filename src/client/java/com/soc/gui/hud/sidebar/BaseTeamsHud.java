package com.soc.gui.hud.sidebar;

import com.soc.gui.hud.VerticallyStackedHudComponent;
import com.soc.networking.helper.GameTeam;
import com.soc.networking.helper.PerPlayerBedwarsInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static com.soc.gui.hud.SidebarHud.BACKGROUND_COLOUR;
import static net.minecraft.util.math.ColorHelper.lerp;

public abstract class BaseTeamsHud<TEAM extends GameTeam> implements VerticallyStackedHudComponent {
    protected final Map<DyeColor, TEAM> teams;
    protected final Map<UUID, Identifier> skinTextures = new HashMap<>();

    protected BaseTeamsHud(Map<DyeColor, TEAM> teams) {
        this.teams = teams;
        this.teams.values().stream().flatMap(GameTeam::getPlayersStream).forEach(uuid -> {
            final PlayerEntity player = MinecraftClient.getInstance().world.getPlayerByUuid(uuid);
            if (player == null) return;
            MinecraftClient.getInstance().getSkinProvider().fetchSkinTextures(player.getGameProfile()).whenCompleteAsync((optionalTextures, throwable) ->
                    optionalTextures.ifPresent(textures -> this.skinTextures.put(uuid, textures.texture()))
            );
        });
    }

    protected void drawTeamPanel(DrawContext context, TextRenderer textRenderer, DyeColor team, int x, int y, int width, int height) {
        this.drawBackground(context, team, x, y, width, height);
        this.drawTeamHeads(context, team, x, y);
        this.drawTeamText(context, team, textRenderer, x, y);
    }

    protected void drawBackground(DrawContext context, DyeColor team, int x, int y, int width, int height) {
        int teamColour = (team.getSignColor() & 0x00ffffff | 0x99000000);
        if (!this.teams.get(team).isAlive()) teamColour = lerp(0.6f, teamColour, BACKGROUND_COLOUR);

        context.fill(x, y, x + width, y + height, teamColour);
    }

    protected void drawTeamHeads(DrawContext context, DyeColor team, int x, int y) {
        final AtomicInteger i = new AtomicInteger();

        this.teams.get(team).getPlayersStream().forEach(player -> {
            final int headX = x + 8 + i.getAndIncrement() * 24;
            final int headY = y + 16;
            final Identifier skinTexture = this.skinTextures.get(player);

            if (skinTexture == null) {
                context.fill(headX, headY, headX + 20, headY + 20, 0xffff0000);
            } else {
                PlayerSkinDrawer.draw(context, skinTexture, headX, headY, 20, true, false, 0xffffffff);
            }
        });
    }

    protected void drawTeamText(DrawContext context, DyeColor team, TextRenderer textRenderer, int x, int y) {
        final Text teamBaseString = Text.translatable("color.minecraft." + team.asString());
        context.drawText(textRenderer, teamBaseString, x + 8, y + 4, 0xffffffff, true);
    }

    @Override
    public int priority() {
        return 0;
    }
}
