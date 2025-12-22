package com.soc.game.manager.bedwars;

import net.minecraft.util.DyeColor;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class TeamStats {
    private final DyeColor team;
    private final Map<UUID, PlayerStats> playerStatsMap;

    private boolean hasBed = true;

    public TeamStats(DyeColor team, Collection<PlayerStats> playerStatsCollection) {
        this.team = team;
        this.playerStatsMap = playerStatsCollection.stream().collect(PlayerStats.MAP_COLLECTOR);
    }

    public boolean hasBed() {
        return this.hasBed;
    }

    public boolean breakBed() {
        if (!this.hasBed) return false;

        this.hasBed = false;
        return true;
    }

    public int getPlayersAlive() {
        return (int)this.playerStatsMap.values().stream().filter(PlayerStats::isAlive).count();
    }

    public boolean isAlive() {
        return this.getPlayersAlive() > 0;
    }

    public DyeColor getTeam() {
        return this.team;
    }
}
