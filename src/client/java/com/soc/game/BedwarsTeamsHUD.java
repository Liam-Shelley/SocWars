package com.soc.game;

import com.google.common.collect.Multimap;
import com.soc.SocWars;
import com.soc.networking.helper.Teams;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class BedwarsTeamsHUD {
    public static void initialise() {
        HudElementRegistry.addFirst(Identifier.of(SocWars.MOD_ID, "bedwars_teams_hud"), BedwarsTeamsHUD::render);
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> INSTANCE = null);
    }

    @Nullable
    private static BedwarsTeamsHUD INSTANCE = null;
    public static Optional<BedwarsTeamsHUD> getInstance() {
        return Optional.ofNullable(INSTANCE);
    }

    private final Multimap<DyeColor, UUID> teams;
    private final Map<DyeColor, Boolean> bedMap = new HashMap<>();
    private final Map<UUID, Identifier> skinTextures = new HashMap<>();
    private final DyeColor ownTeam;

    private BedwarsTeamsHUD(Teams teams) {
        this.teams = teams.getTeams();
        this.ownTeam = this.teams.entries().stream().filter(entry -> entry.getValue() == MinecraftClient.getInstance().player.getUuid()).findFirst().map(Map.Entry::getKey).orElse(null);
        this.teams.forEach((team, uuid) -> {
                this.bedMap.put(team, teams.hasBed(team));

                final PlayerEntity player = MinecraftClient.getInstance().world.getPlayerByUuid(uuid);
                if (player == null) return;
                MinecraftClient.getInstance().getSkinProvider().fetchSkinTextures(player.getGameProfile()).whenCompleteAsync((optionalTextures, throwable) ->
                        optionalTextures.ifPresent(textures -> this.skinTextures.put(uuid, textures.texture()))
                );
        });
    }

    public static void joinGame(Teams teams) {
        INSTANCE = new BedwarsTeamsHUD(teams);
    }

    public static void leaveGame() {
        INSTANCE = null;
    }

    public static void breakBed(DyeColor team) {
        getInstance().ifPresent(instance -> instance.bedMap.put(team, false));
    }

    public Optional<Identifier> getSkinTexture(UUID uuid) {
        return Optional.ofNullable(this.skinTextures.get(uuid));
    }

    public static void render(DrawContext drawContext, RenderTickCounter renderTickCounter) {
        if (getInstance().isEmpty()) return;

        final BedwarsTeamsHUD instance = getInstance().get();
        final TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        final int width = drawContext.getScaledWindowWidth();
        final int height = drawContext.getScaledWindowHeight();

        drawContext.fill(width - 130, height / 2 - 100, width, height / 2 + 100, 0x38000000);

        int i = 0;
        for (DyeColor team : instance.teams.keySet()) {
            final int heightStart = height / 2 - 60 + 40 * i++;

            drawContext.fill(width - 130, heightStart, width, heightStart + 40, team.getSignColor() & 0x00ffffff | 0x99000000);

            draw(instance, drawContext, team, textRenderer, width, heightStart - 40);
            drawTeamText(instance, drawContext, team, textRenderer, width, heightStart);
            drawTeamHeads(instance, drawContext, team, width, heightStart);
        }
    }

    private static void draw(BedwarsTeamsHUD instance, DrawContext drawContext, DyeColor team, TextRenderer textRenderer, int width, int heightStart) {

    }

    private static void drawTeamText(BedwarsTeamsHUD instance, DrawContext drawContext, DyeColor team, TextRenderer textRenderer, int width, int heightStart) {
        final Boolean hasBedBoxed = instance.bedMap.get(team);
        final boolean hasBed = hasBedBoxed != null && hasBedBoxed;

        final String teamBaseString = String.format(Language.getInstance().get("hud.bedwars.team"), Language.getInstance().get("color.minecraft." + team.asString()));
        drawContext.drawText(textRenderer, teamBaseString, width - 120, heightStart + 4, 0xffffffff, true);

        final String hasBedBaseString = Language.getInstance().get("hud.bedwars.has_bed");
        drawContext.drawText(textRenderer, hasBedBaseString, width - 120 + textRenderer.getWidth(teamBaseString), heightStart + 4, 0xffffffff, true);

        drawContext.drawText(textRenderer, Language.getInstance().get(hasBed ? "hud.tick" : "hud.cross"), width - 120 + textRenderer.getWidth(hasBedBaseString) + textRenderer.getWidth(teamBaseString), heightStart + 4, hasBed ? 0xff11ee22 : 0xffee1122, true);
    }

    private static void drawTeamHeads(BedwarsTeamsHUD instance, DrawContext drawContext, DyeColor team, int width, int heightStart) {
        final Collection<UUID> players = instance.teams.get(team);

        int i = 0;
        for (UUID uuid : players) {
            final int x = width - 120 + i++ * 24;
            final int y = heightStart + 16;
            instance.getSkinTexture(uuid).ifPresentOrElse(
                    texture -> PlayerSkinDrawer.draw(drawContext, texture, x, y, 20, true, false, 0xffffffff),
                    () -> drawContext.fill(x, y, x + 20, y + 20, 0xffff0000)
            );
        }
    }
}
