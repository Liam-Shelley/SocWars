package com.soc.gui.hud;

import com.google.common.collect.Multimap;
import com.soc.networking.helper.Teams;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BedwarsTeamsHud implements VerticallyStackedHudComponent {
    public static void initialise() {
        SidebarHud.addHudElement(INSTANCE);
    }

    private static final @NotNull Reference<BedwarsTeamsHud> INSTANCE = new Reference<>(null);

    private final Multimap<DyeColor, UUID> teams;
    private final Map<DyeColor, Boolean> bedMap = new HashMap<>();
    private final Map<UUID, Identifier> skinTextures = new HashMap<>();
    private final DyeColor ownTeam; //idk I may use this at some point

    private BedwarsTeamsHud(Teams teams) {
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
        INSTANCE.set(new BedwarsTeamsHud(teams));
    }

    public static void leaveGame() {
        INSTANCE.annul();
    }

    public static void breakBed(DyeColor team) {
        INSTANCE.ifPresent(instance -> instance.bedMap.put(team, false));
    }

    @Override
    public void render(DrawContext drawContext, RenderTickCounter renderTickCounter, TextRenderer textRenderer, int x, int y) {
        int i = 0;
        for (DyeColor team : this.teams.keySet()) {
            final int heightStart = y + 40 * i++;

            drawContext.fill(x, heightStart, x + 128, heightStart + 40, team.getSignColor() & 0x00ffffff | 0x99000000);

            this.drawTeamText(drawContext, team, textRenderer, x + 128, heightStart);
            this.drawTeamHeads(drawContext, team, x + 128, heightStart);
        }
    }

    private void drawTeamText(DrawContext drawContext, DyeColor team, TextRenderer textRenderer, int width, int heightStart) {
        final Boolean hasBedBoxed = this.bedMap.get(team);
        final boolean hasBed = hasBedBoxed != null && hasBedBoxed;

        final String teamBaseString = String.format(Language.getInstance().get("hud.bedwars.team"), Language.getInstance().get("color.minecraft." + team.asString()));
        drawContext.drawText(textRenderer, teamBaseString, width - 120, heightStart + 4, 0xffffffff, true);

        final String hasBedBaseString = Language.getInstance().get("hud.bedwars.has_bed");
        drawContext.drawText(textRenderer, hasBedBaseString, width - 120 + textRenderer.getWidth(teamBaseString), heightStart + 4, 0xffffffff, true);

        drawContext.drawText(textRenderer, Language.getInstance().get(hasBed ? "hud.tick" : "hud.cross"), width - 120 + textRenderer.getWidth(hasBedBaseString) + textRenderer.getWidth(teamBaseString), heightStart + 4, hasBed ? 0xff11ee22 : 0xffee1122, true);
    }

    private void drawTeamHeads(DrawContext drawContext, DyeColor team, int width, int heightStart) {
        final Collection<UUID> players = this.teams.get(team);

        int i = 0;
        for (UUID uuid : players) {
            final int x = width - 120 + i++ * 24;
            final int y = heightStart + 16;
            final Identifier skinTexture = this.skinTextures.get(uuid);

            if (skinTexture == null) {
                drawContext.fill(x, y, x + 20, y + 20, 0xffff0000);
            } else {
                PlayerSkinDrawer.draw(drawContext, skinTexture, x, y, 20, true, false, 0xffffffff);
            }
        }
    }

    @Override
    public int getSize() {
        return this.teams.keySet().size() * 40;
    }

    @Override
    public int priority() {
        return 0;
    }
}
