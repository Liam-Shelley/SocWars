package com.soc.game.manager;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.soc.database.stats.BedwarsTable;
import com.soc.database.stats.SkywarsTable;
import com.soc.game.manager.bedwarsshopitem.BaseShopItem;
import com.soc.game.map.BedwarsGameMap;
import com.soc.game.map.SpreadRules;
import com.soc.items.components.ModComponents;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.soc.game.map.AbstractGameMap.getRandomPlayerStack;

public class BedwarsGameManager extends AbstractGameManager {
    private class PlayerStats {
        private int pickaxeTier;
        private int axeTier;
        private int shearsTier;
        private int armourTier;

        public PlayerStats() {}

        public void onDeath() {
            if (this.pickaxeTier > 0) this.pickaxeTier--;
            if (this.axeTier > 0) this.axeTier--;
            if (this.shearsTier > 0) this.shearsTier--;
            if (this.armourTier > 0) this.armourTier--;
        }
    }

    private class TeamStats {

    }

    private final Map<ServerPlayerEntity, PlayerStats> playerStatsMap;
    private final Map<DyeColor, TeamStats> teamStatsMap;
    private final BedwarsShopContents shopContents;

    protected BedwarsGameManager(ServerWorld world, Set<ServerPlayerEntity> players, @NotNull SpreadRules spreadRules, int gameId) {
        super(world, players, spreadRules, gameId);
        this.playerStatsMap = players.stream().collect(Collectors.toMap(key -> key, key -> new PlayerStats()));
        this.teamStatsMap = super.teams.keySet().stream().collect(Collectors.toMap(key -> key, key -> new TeamStats()));
        this.shopContents = new BedwarsShopContents();
    }

    protected Map<Identifier, List<BaseShopItem>> makeShopContents() {
        return Map.of();
    }

    @Override
    protected BedwarsGameMap getMap() {
        return (BedwarsGameMap) super.map;
    }

    @Override
    protected BedwarsGameMap buildMap() {
        return BedwarsGameMap.loadRandomMap(super.world, super.generateCentrePosition()).get();
    }

    @Override
    public Multimap<DyeColor, ServerPlayerEntity> buildTeams(Set<ServerPlayerEntity> players, SpreadRules spreadRules) {
        //Probably rewrite this at some point it's a bit gross

        final Stack<ServerPlayerEntity> playerStack = getRandomPlayerStack(players);

        final Set<DyeColor> teamColours = this.getMap().getTeamColours();
        final int numTeams = Math.min(spreadRules.numTeams(), teamColours.size());

        final ImmutableMultimap.Builder<DyeColor, ServerPlayerEntity> builder = ImmutableMultimap.builder();

        final ArrayList<DyeColor> teamsUnlimited = new ArrayList<>(teamColours);
        Collections.shuffle(teamsUnlimited);
        final List<DyeColor> teams = teamsUnlimited.stream().limit(numTeams).toList();

        for (int i = 0; i < players.size(); i++) {
            builder.put(teams.get(i % numTeams), playerStack.pop());
        }

        return builder.build();
    }

    @Override
    protected @Nullable EventQueue buildEventQueue() {
        final EventQueue queue = new EventQueue();

        queue.addEventMinutesSeconds(3, 30, (manager) -> {}, "events.bedwars.diamond2");

        return queue;
    }

    @Override
    protected Function<ServerPlayerEntity, BedwarsTable> dbTableBuilder() {
        return BedwarsTable::new;
    }

    @Override
    public boolean onPlayerDeath(ServerPlayerEntity player, DamageSource source, float amount) {
        super.onPlayerDeath(player, source, amount);
        if (source.isOf(DamageTypes.OUT_OF_WORLD)) ((SkywarsTable)this.dbTables.get(player)).fallInVoid();

        PrescheduledEvents.playCountdown(() -> this.respawnPlayer(player), this, 5, 20, SoundEvents.BLOCK_FUNGUS_STEP, player);

        //Drop resources and whatnot
        return true;
    }

    protected void respawnPlayer(ServerPlayerEntity player) {

    }

    @Override
    public void onItemPickup(ServerPlayerEntity player, ItemStack stack) {
        if (stack.get(ModComponents.RESOURCE_COUNTED) == null) return;

        player.sendMessage(Text.of(stack.toString()), false);
    }

    public BedwarsShopContents getShopContents() {
        return this.shopContents;
    }

    @Nullable
    public static BedwarsGameManager getBedwarsGameManager(PlayerEntity player) {
        return GamesManager.getInstance().getGame(player).map(a -> a instanceof BedwarsGameManager bedwarsGameManager ? bedwarsGameManager : null).orElse(null);
    }
}
