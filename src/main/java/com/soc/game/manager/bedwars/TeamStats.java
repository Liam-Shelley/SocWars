package com.soc.game.manager.bedwars;

import com.soc.game.manager.bedwars.traps.TrapManager;
import com.soc.resourcedata.containers.BedwarsShopDataContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;

public class TeamStats {
    private final DyeColor team;
    private final Map<UUID, PlayerStats> playerStatsMap;
    private final TrapManager trapManager;
    private final BedwarsShopContents teamShopContents;

    private boolean hasBed = true;

    public TeamStats(DyeColor team, Collection<PlayerStats> playerStatsCollection, World world, long shopSeed) {
        this.team = team;
        this.playerStatsMap = playerStatsCollection.stream().collect(PlayerStats.MAP_COLLECTOR);
        this.trapManager = new TrapManager(this.playerStatsMap.keySet(), world);
        this.teamShopContents = BedwarsShopDataContainer.INSTANCE.getTeamBedwarsShop(shopSeed, team);
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

    public boolean hasActiveTrap() {
        return this.trapManager.hasActiveTrap();
    }

    public void onPlayerInTrapRange(Vec3d pos, List<ServerPlayerEntity> players) {
        this.trapManager.trigger(pos, players);
    }

    public BedwarsShopContents getShopContents() {
        this.teamShopContents.setCategory(2, this.trapManager.getTrapsDisplay());
        this.teamShopContents.setCategory(3, this.trapManager.getAbilitiesDisplay());
        return this.teamShopContents;
    }

    public int[] getTrapProgressStats() {
        return this.trapManager.getTrapProgressStats();
    }
}
