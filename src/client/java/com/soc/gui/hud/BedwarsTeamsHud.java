package com.soc.gui.hud;

import com.soc.networking.helper.BedwarsTeam;
import com.soc.networking.helper.PerPlayerBedwarsInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.PlayerSkinDrawer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.soc.gui.hud.SidebarHud.BACKGROUND_COLOUR;
import static net.minecraft.util.math.ColorHelper.lerp;

public class BedwarsTeamsHud implements VerticallyStackedHudComponent {
    public static void initialise() {
        SidebarHud.addHudElement(INSTANCE);
    }

    private static final @NotNull Reference<BedwarsTeamsHud> INSTANCE = new Reference<>(null);

    private final Map<DyeColor, BedwarsTeam> teams;
    private final Map<UUID, Identifier> skinTextures = new HashMap<>();
    //private final DyeColor ownTeam;

    private BedwarsTeamsHud(Map<DyeColor, BedwarsTeam> teams) {
        this.teams = teams;
        //this.ownTeam = this.teams.entries().stream().filter(entry -> entry.getValue() == MinecraftClient.getInstance().player.getUuid()).findFirst().map(Map.Entry::getKey).orElse(null);
        this.teams.values().forEach(team -> team.players().forEach(playerInfo -> {
                final PlayerEntity player = MinecraftClient.getInstance().world.getPlayerByUuid(playerInfo.player());
                if (player == null) return;
                MinecraftClient.getInstance().getSkinProvider().fetchSkinTextures(player.getGameProfile()).whenCompleteAsync((optionalTextures, throwable) ->
                        optionalTextures.ifPresent(textures -> this.skinTextures.put(playerInfo.player(), textures.texture()))
                );
        }));
    }

    public static void joinGame(Map<DyeColor, BedwarsTeam> bedwarsTeam) {
        INSTANCE.set(new BedwarsTeamsHud(bedwarsTeam));
    }

    public static void leaveGame() {
        INSTANCE.annul();
    }

    public static void breakBed(DyeColor team) {
        INSTANCE.ifPresent(instance -> instance.teams.get(team).breakBed());
    }

    public static void eliminateTeam(DyeColor team) {
        INSTANCE.ifPresent(instance -> instance.teams.get(team).eliminate());
    }

    @Override
    public void render(DrawContext drawContext, RenderTickCounter renderTickCounter, TextRenderer textRenderer, int x, int y) {
        int i = 0;
        for (DyeColor team : this.teams.keySet()) {
            final int heightStart = y + 40 * i++;

            int teamColour = (team.getSignColor() & 0x00ffffff | 0x99000000);
            if (!this.teams.get(team).isAlive()) teamColour = lerp(0.6f, teamColour, BACKGROUND_COLOUR);

            drawContext.fill(x, heightStart, x + 128, heightStart + 40, teamColour);

            this.drawTeamText(drawContext, team, textRenderer, x + 128, heightStart);
            this.drawTeamHeads(drawContext, team, x + 128, heightStart);
        }
    }

    private void drawTeamText(DrawContext drawContext, DyeColor team, TextRenderer textRenderer, int width, int heightStart) {
        final boolean hasBed = this.teams.get(team).hasBed();

        final Text teamBaseString = Text.translatable("hud.bedwars.team", Text.translatable("color.minecraft." + team.asString()));
        drawContext.drawText(textRenderer, teamBaseString, width - 120, heightStart + 4, 0xffffffff, true);

        final Text hasBedBaseString = Text.translatable("hud.bedwars.has_bed");
        drawContext.drawText(textRenderer, hasBedBaseString, width - 120 + textRenderer.getWidth(teamBaseString), heightStart + 4, 0xffffffff, true);

        drawContext.drawText(textRenderer, Text.translatable(hasBed ? "hud.tick" : "hud.cross"), width - 120 + textRenderer.getWidth(hasBedBaseString) + textRenderer.getWidth(teamBaseString), heightStart + 4, hasBed ? 0xff11ee22 : 0xffee1122, true);
    }

    private void drawTeamHeads(DrawContext drawContext, DyeColor team, int width, int heightStart) {
        final AtomicInteger i = new AtomicInteger();

        this.teams.get(team).players().stream().map(PerPlayerBedwarsInfo::player).forEach(player -> {
            final int x = width - 120 + i.getAndIncrement() * 24;
            final int y = heightStart + 16;
            final Identifier skinTexture = this.skinTextures.get(player);

            if (skinTexture == null) {
                drawContext.fill(x, y, x + 20, y + 20, 0xffff0000);
            } else {
                PlayerSkinDrawer.draw(drawContext, skinTexture, x, y, 20, true, false, 0xffffffff);
            }
        });
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
