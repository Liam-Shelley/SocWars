package com.soc.networking.helper;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.util.DyeColor;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record Teams(List<TeamPlayerPair> teams) {
    public static final PacketCodec<RegistryByteBuf, Teams> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.collection(ArrayList::new, TeamPlayerPair.PACKET_CODEC), Teams::teams, Teams::new);

    public Teams(Multimap<DyeColor, UUID> teams) {
        this(teams.entries().stream().map(TeamPlayerPair::new).toList());
    }

    public Multimap<DyeColor, PlayerEntity> getTeams(World world) {
        return teams.stream().collect(Multimaps.toMultimap(TeamPlayerPair::team, pair -> world.getPlayerByUuid(pair.player()), HashMultimap::create));
    }
}
