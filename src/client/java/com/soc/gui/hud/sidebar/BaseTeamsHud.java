package com.soc.gui.hud.sidebar;

import com.soc.gui.hud.VerticallyStackedHudComponent;
import com.soc.networking.helper.TeamPlayersProvider;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class BaseTeamsHud<TEAM extends TeamPlayersProvider> implements VerticallyStackedHudComponent {
    protected final Map<DyeColor, TEAM> teams;
    protected final Map<UUID, Identifier> skinTextures = new HashMap<>();

    protected BaseTeamsHud(Map<DyeColor, TEAM> teams) {
        this.teams = teams;
        this.teams.values().stream().flatMap(TeamPlayersProvider::getPlayersStream).forEach(uuid -> {
            final PlayerEntity player = MinecraftClient.getInstance().world.getPlayerByUuid(uuid);
            if (player == null) return;
            MinecraftClient.getInstance().getSkinProvider().fetchSkinTextures(player.getGameProfile()).whenCompleteAsync((optionalTextures, throwable) ->
                    optionalTextures.ifPresent(textures -> this.skinTextures.put(uuid, textures.texture()))
            );
        });
    }

    @Override
    public int priority() {
        return 0;
    }
}
