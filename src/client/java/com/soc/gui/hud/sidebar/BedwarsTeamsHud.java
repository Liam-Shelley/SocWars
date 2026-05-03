package com.soc.gui.hud.sidebar;

import com.soc.gui.hud.Reference;
import com.soc.gui.hud.SidebarHud;
import com.soc.gui.hud.VerticallyStackedHudComponent;
import com.soc.networking.helper.BedwarsTeam;
import com.soc.networking.helper.PerPlayerBedwarsInfo;
import com.soc.networking.helper.TeamPlayersProvider;
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

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.soc.gui.hud.SidebarHud.BACKGROUND_COLOUR;
import static com.soc.gui.hud.SidebarHud.SIDEBAR_WIDTH;
import static net.minecraft.util.math.ColorHelper.lerp;

public class BedwarsTeamsHud implements VerticallyStackedHudComponent {
    public static void initialise() {
        SidebarHud.addHudElement(INSTANCE);
    }

    private static final @NotNull Reference<BedwarsTeamsHud> INSTANCE = new Reference<>(null);

    private final Map<DyeColor, BedwarsTeam> teams;
    private final Map<UUID, Identifier> skinTextures = new HashMap<>();

    private BedwarsTeamsHud(Map<DyeColor, BedwarsTeam> teams) {
        this.teams = teams;
        this.teams.values().stream().flatMap(TeamPlayersProvider::getPlayersStream).forEach(uuid -> {
                final PlayerEntity player = MinecraftClient.getInstance().world.getPlayerByUuid(uuid);
                if (player == null) return;
                MinecraftClient.getInstance().getSkinProvider().fetchSkinTextures(player.getGameProfile()).whenCompleteAsync((optionalTextures, throwable) ->
                        optionalTextures.ifPresent(textures -> this.skinTextures.put(uuid, textures.texture()))
                );
        });
    }

    public static void joinGame(Map<DyeColor, BedwarsTeam> teams) {
        INSTANCE.set(new BedwarsTeamsHud(teams));
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
    public void render(DrawContext context, RenderTickCounter renderTickCounter, TextRenderer textRenderer, int x, int y) {
        int i = 0;
        for (DyeColor team : this.teams.keySet()) {
            final int yOrigin = y + 40 * i++;

            int teamColour = (team.getSignColor() & 0x00ffffff | 0x99000000);
            if (!this.teams.get(team).isAlive()) teamColour = lerp(0.6f, teamColour, BACKGROUND_COLOUR);

            context.fill(x, yOrigin, x + SIDEBAR_WIDTH, yOrigin + 40, teamColour);

            this.drawTeamText(context, team, textRenderer, x + SIDEBAR_WIDTH, yOrigin);
            this.drawTeamHeads(context, team, x + SIDEBAR_WIDTH, yOrigin);
        }
    }

    private void drawTeamText(DrawContext context, DyeColor team, TextRenderer textRenderer, int x, int y) {
        final boolean hasBed = this.teams.get(team).hasBed();

        final Text teamBaseString = Text.translatable("hud.bedwars.team", Text.translatable("color.minecraft." + team.asString()));
        context.drawText(textRenderer, teamBaseString, x - 120, y + 4, 0xffffffff, true);

        final Text hasBedBaseString = Text.translatable("hud.bedwars.has_bed");
        context.drawText(textRenderer, hasBedBaseString, x - 120 + textRenderer.getWidth(teamBaseString), y + 4, 0xffffffff, true);

        context.drawText(textRenderer, Text.translatable(hasBed ? "hud.tick" : "hud.cross"), x - 120 + textRenderer.getWidth(hasBedBaseString) + textRenderer.getWidth(teamBaseString), y + 4, hasBed ? 0xff11ee22 : 0xffee1122, true);
    }

    private void drawTeamHeads(DrawContext context, DyeColor team, int x, int y) {
        final AtomicInteger i = new AtomicInteger();

        this.teams.get(team).players().stream().map(PerPlayerBedwarsInfo::player).forEach(player -> {
            final int headX = x - 120 + i.getAndIncrement() * 24;
            final int headY = y + 16;
            final Identifier skinTexture = this.skinTextures.get(player);

            if (skinTexture == null) {
                context.fill(headX, headY, headX + 20, headY + 20, 0xffff0000);
            } else {
                PlayerSkinDrawer.draw(context, skinTexture, headX, headY, 20, true, false, 0xffffffff);
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
