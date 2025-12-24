package com.soc.networking.helper;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.soc.game.manager.bedwars.TeamStats;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.DyeColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public record Teams(List<PerPlayerBedwarsInfo> teams) {
    public static final PacketCodec<RegistryByteBuf, Teams> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.collection(ArrayList::new, PerPlayerBedwarsInfo.PACKET_CODEC), Teams::teams, Teams::new);

    public Teams(Multimap<DyeColor, UUID> teams, Map<DyeColor, TeamStats> teamStatsMap) {
        this(teams.entries().stream().map(entry -> new PerPlayerBedwarsInfo(entry, teamStatsMap.get(entry.getKey()))).toList());
    }

    public Multimap<DyeColor, UUID> getTeams() {
        return teams.stream().collect(Multimaps.toMultimap(PerPlayerBedwarsInfo::team, PerPlayerBedwarsInfo::player, HashMultimap::create));
    }

    public boolean hasBed(DyeColor team) {
        return this.teams.stream().filter(info -> info.team() == team).findFirst().map(PerPlayerBedwarsInfo::hasBed).orElse(true);
    }
}
