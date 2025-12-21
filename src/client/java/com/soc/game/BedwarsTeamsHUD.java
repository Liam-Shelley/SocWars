package com.soc.game;

import com.google.common.collect.Multimap;
import com.soc.SocWars;
import com.soc.networking.helper.TeamPlayerPair;
import com.soc.networking.helper.Teams;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BedwarsTeamsHUD {
    public static void initialise() {
        HudElementRegistry.addFirst(Identifier.of(SocWars.MOD_ID, ""), BedwarsTeamsHUD::render);
    }

    @Nullable
    private static BedwarsTeamsHUD INSTANCE = null;
    public static Optional<BedwarsTeamsHUD> getInstance() {
        return Optional.ofNullable(INSTANCE);
    }

    private final Multimap<DyeColor, PlayerEntity> teams;

    private BedwarsTeamsHUD(Teams teams) {
        this.teams = teams.getTeams(MinecraftClient.getInstance().world);
    }

    public static void joinGame(Teams teams) {
        INSTANCE = new BedwarsTeamsHUD(teams);
    }

    public static void leaveGame() {
        INSTANCE = null;
    }

    public static void render(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        INSTANCE = new BedwarsTeamsHUD(new Teams(List.of(new TeamPlayerPair(DyeColor.PURPLE, UUID.fromString("86a8a785-8209-42c1-9c30-d219fb019db2")))));

        if (getInstance().isEmpty()) return;
        final BedwarsTeamsHUD instance = getInstance().get();
        final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        final int width = drawContext.getScaledWindowWidth();
        final int height = drawContext.getScaledWindowHeight();

        drawContext.fill(width - 130, height / 2 - 100, width, height / 2 + 100, 0x44000000);

        int i = 0;
        for (DyeColor team : instance.teams.keySet()) {
            final int heightStart = height / 2 - 60 + 20 * i;
            i++;

            drawContext.fill(width - 130, heightStart, width, heightStart + 40, team.getSignColor() & 0x00ffffff | 0x99000000);

            drawTeamText(drawContext, team, textRenderer, width, heightStart);
            drawTeamHeads(drawContext, team, instance, width, heightStart);
        }
    }

    private static void drawTeamText(DrawContext drawContext, DyeColor team, TextRenderer textRenderer, int width, int heightStart) {
        final boolean hasBed = true;

        final String teamBaseString = String.format(Language.getInstance().get("hud.bedwars.team"), Language.getInstance().get("color.minecraft." + team.asString()));
        drawContext.drawText(textRenderer, teamBaseString, width - 120, heightStart + 4, 0xffffffff, true);

        final String hasBedBaseString = Language.getInstance().get("hud.bedwars.has_bed");
        drawContext.drawText(textRenderer, hasBedBaseString, width - 120 + textRenderer.getWidth(teamBaseString), heightStart + 4, 0xffffffff, true);

        drawContext.drawText(textRenderer, Language.getInstance().get(hasBed ? "hud.tick" : "hud.cross"), width - 120 + textRenderer.getWidth(hasBedBaseString) + textRenderer.getWidth(teamBaseString), heightStart + 4, hasBed ? 0xff11ee22 : 0xffee1122, true);
    }

    private static void drawTeamHeads(DrawContext drawContext, DyeColor team, BedwarsTeamsHUD instance, int width, int heightStart) {
        final List<PlayerEntity> players = List.copyOf(instance.teams.get(team));
        for (int j = 0; j < players.size(); j++) {
            if (players.get(j) instanceof ClientPlayerEntity clientPlayer) {
                PlayerSkinDrawer.draw(drawContext, clientPlayer.getSkinTextures(), width - 120 + j * 24, heightStart + 16, 20);
            }
        }
    }
}
