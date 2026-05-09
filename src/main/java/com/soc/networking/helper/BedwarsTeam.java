package com.soc.networking.helper;

import com.soc.game.manager.bedwars.TeamStats;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class BedwarsTeam implements GameTeam {
    public static final PacketCodec<RegistryByteBuf, BedwarsTeam> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.collection(ArrayList::new, PerPlayerBedwarsInfo.PACKET_CODEC), BedwarsTeam::players,
            PacketCodecs.BOOLEAN, BedwarsTeam::hasBed,
            PacketCodecs.BOOLEAN, BedwarsTeam::isAlive,
            BedwarsTeam::new
    );

    private final List<PerPlayerBedwarsInfo> players;
    private boolean hasBed;
    private boolean isAlive;

    public BedwarsTeam(List<PerPlayerBedwarsInfo> players, boolean hasBed, boolean isAlive) {
        this.players = players;
        this.hasBed = hasBed;
        this.isAlive = isAlive;
    }

    public BedwarsTeam(TeamStats teamStats) {
        this(
                teamStats.getPlayersStats().stream().map(PerPlayerBedwarsInfo::new).toList(),
                teamStats.hasBed(),
                teamStats.isAlive()
        );
    }

    public List<PerPlayerBedwarsInfo> players() {
        return this.players;
    }

    public boolean hasBed() {
        return this.hasBed;
    }

    public void breakBed() {
        this.hasBed = false;
    }

    @Override
    public boolean isAlive() {
        return this.isAlive;
    }

    public void eliminate() {
        this.isAlive = false;
    }

    @Override
    public Stream<UUID> getPlayersStream() {
        return this.players.stream().map(PerPlayerBedwarsInfo::player);
    }
}
