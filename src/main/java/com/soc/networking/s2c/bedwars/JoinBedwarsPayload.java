package com.soc.networking.s2c.bedwars;

import com.soc.SocWars;
import com.soc.game.manager.bedwars.TeamStats;
import com.soc.networking.helper.BedwarsTeam;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public record JoinBedwarsPayload(int gameId, Map<DyeColor, BedwarsTeam> teams) implements CustomPayload {
    public static final Identifier START_GAME_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "join_bedwars");
    public static final Id<JoinBedwarsPayload> ID = new Id<>(START_GAME_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, JoinBedwarsPayload> CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, JoinBedwarsPayload::gameId, PacketCodecs.map(HashMap::new, DyeColor.PACKET_CODEC, BedwarsTeam.PACKET_CODEC), JoinBedwarsPayload::teams, JoinBedwarsPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    } 
}
