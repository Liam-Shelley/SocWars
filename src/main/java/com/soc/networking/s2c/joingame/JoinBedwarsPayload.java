package com.soc.networking.s2c.joingame;

import com.soc.SocWars;
import com.soc.networking.helper.Teams;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record JoinBedwarsPayload(int gameId, Teams teams) implements CustomPayload {
    public static final Identifier START_GAME_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "start_game");
    public static final Id<JoinBedwarsPayload> ID = new Id<>(START_GAME_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, JoinBedwarsPayload> CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, JoinBedwarsPayload::gameId, Teams.PACKET_CODEC, JoinBedwarsPayload::teams, JoinBedwarsPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    } 
}
