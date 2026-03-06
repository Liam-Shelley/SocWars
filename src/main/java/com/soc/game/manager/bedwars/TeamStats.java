package com.soc.game.manager.bedwars;

import com.soc.game.manager.AbstractGameManager;
import com.soc.game.manager.bedwars.traps.Trap;
import com.soc.game.manager.bedwars.traps.TrapManager;
import com.soc.networking.s2c.bedwars.BedwarsTeamShopDataPayload;
import com.soc.resourcedata.containers.BedwarsShopDataContainer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;

import static com.soc.game.manager.AbstractGameManager.mapUuidsToPlayers;

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

    public void onPlayerInTrapRange(AbstractGameManager<?, ?, ?> manager, Vec3d pos, List<ServerPlayerEntity> players) {
        this.trapManager.trigger(manager, pos, players);
    }

    public BedwarsShopContents getShopContents() {
        this.teamShopContents.setCategory(2, this.trapManager.getTrapsDisplay());
        this.teamShopContents.setCategory(3, this.trapManager.getAbilitiesDisplay());
        return this.teamShopContents;
    }

    public int[] getTrapProgressStats() {
        return this.trapManager.getTrapProgressStats();
    }

    public boolean buyTrap(Trap trap, World world) {
        boolean success = this.trapManager.buyTrap(trap);
        //if (success) this.resendPlayerShops(world);
        return success;
    }

    public boolean buyAbility(Trap ability, World world) {
        return this.trapManager.buyAbility(ability);
    }

    public void buyEnchantmentUpgrade(RegistryEntry<Enchantment> enchantment, World world, int tier) {
        this.playerStatsMap.forEach(((uuid, playerStats) -> playerStats.buyEnchantmentUpgrade(enchantment, world, tier)));
    }

    //Goddamn this stupid visual bug
    //private void resendPlayerShops(World world) {
    //    mapUuidsToPlayers(world, this.playerStatsMap.keySet()).forEach(player -> ServerPlayNetworking.send(player, new BedwarsTeamShopDataPayload(this.getShopContents(), player.currentScreenHandler.syncId)));
    //}
}
