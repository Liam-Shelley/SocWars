package com.soc.game.manager.bedwars;

import com.soc.game.manager.AbstractGameManager;
import com.soc.game.manager.bedwars.tickfunctions.AbstractTickFunction;
import com.soc.game.manager.bedwars.traps.AbstractTrap;
import com.soc.game.manager.bedwars.traps.TrapManager;
import com.soc.resourcedata.containers.BedwarsShopDataContainer;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;

import static com.soc.game.manager.AbstractGameManager.mapUuidsToPlayers;

public class TeamStats {
    private final DyeColor team;
    private final Map<UUID, PlayerStats> playerStatsMap;
    private final TrapManager trapManager;
    private final Object2IntMap<AbstractTickFunction> tickFunctions;
    private final BedwarsShopContents teamShopContents;
    private final Collection<Vec3d> spawnPositions;

    private boolean hasBed = true;

    public TeamStats(DyeColor team, Collection<PlayerStats> playerStatsCollection, World world, long shopSeed, Collection<BlockPos> spawnPositions) {
        this.team = team;
        this.playerStatsMap = playerStatsCollection.stream().collect(PlayerStats.MAP_COLLECTOR);
        this.trapManager = new TrapManager(this.playerStatsMap.keySet(), world);
        this.tickFunctions = new Object2IntOpenHashMap<>();
        this.spawnPositions = spawnPositions.stream().map(BlockPos::toCenterPos).toList();
        this.teamShopContents = BedwarsShopDataContainer.INSTANCE.getTeamBedwarsShop(shopSeed, team, world);
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
        this.teamShopContents.setCategory(3, this.trapManager.getTrapsDisplay());
        this.teamShopContents.setCategory(4, this.trapManager.getAbilitiesDisplay());
        return this.teamShopContents;
    }

    public int[] getTrapProgressStats() {
        return this.trapManager.getTrapProgressStats();
    }

    public boolean buyTrap(AbstractTrap trap, World world) {
        boolean success = this.trapManager.buyTrap(trap);
        //if (success) this.resendPlayerShops(world);
        return success;
    }

    public boolean buyAbility(AbstractTrap ability, World world) {
        return this.trapManager.buyAbility(ability);
    }

    public void buyEnchantmentUpgrade(RegistryEntry<Enchantment> enchantment, World world, int tier) {
        this.playerStatsMap.forEach(((uuid, playerStats) -> playerStats.buyEnchantmentUpgrade(enchantment, world, tier)));
    }

    public void buyTickFunctionUpgrade(AbstractTickFunction function, int tier) {
        this.tickFunctions.put(function, tier);
    }

    public void tick(int time, World world) {
        final List<ServerPlayerEntity> players = mapUuidsToPlayers(world, this.playerStatsMap.keySet()); //So bad I really need to cache this
        for (Vec3d spawnPosition : this.spawnPositions) {
            this.tickFunctions.forEach((function, tier) -> {
                function.tick(spawnPosition, players, tier, world);
            });
        }

        if (time % 20 == 0) {
            for (Vec3d spawnPosition : this.spawnPositions) {
                this.tickFunctions.forEach((function, tier) -> {
                    function.slowTick(spawnPosition, players, tier, world);
                });
            }
        }
    }

    //Goddamn this stupid visual bug
    //private void resendPlayerShops(World world) {
    //    mapUuidsToPlayers(world, this.playerStatsMap.keySet()).forEach(player -> ServerPlayNetworking.send(player, new BedwarsTeamShopDataPayload(this.getShopContents(), player.currentScreenHandler.syncId)));
    //}
}
