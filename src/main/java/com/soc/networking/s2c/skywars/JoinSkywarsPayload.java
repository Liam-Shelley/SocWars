package com.soc.networking.s2c.skywars;

import com.soc.SocWars;
import com.soc.networking.helper.SkywarsTeam;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public record JoinSkywarsPayload(int gameId, Map<DyeColor, SkywarsTeam> teams) implements CustomPayload {
    public static final Identifier JOIN_SKYWARS_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "join_skywars");
    public static final Id<JoinSkywarsPayload> ID = new Id<>(JOIN_SKYWARS_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, JoinSkywarsPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.INTEGER, JoinSkywarsPayload::gameId,
            PacketCodecs.map(HashMap::new, DyeColor.PACKET_CODEC, SkywarsTeam.PACKET_CODEC), JoinSkywarsPayload::teams,
            JoinSkywarsPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    } 
}
