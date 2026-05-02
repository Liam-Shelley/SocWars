package com.soc.networking.s2c.skywars;

import com.soc.SocWars;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

public record SetTeamLivesPayload(DyeColor team, int lives) implements CustomPayload {
    public static final Identifier SET_TEAM_LIVES_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "set_team_lives");
    public static final Id<SetTeamLivesPayload> ID = new Id<>(SET_TEAM_LIVES_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, SetTeamLivesPayload> CODEC = PacketCodec.tuple(
            DyeColor.PACKET_CODEC, SetTeamLivesPayload::team,
            PacketCodecs.INTEGER, SetTeamLivesPayload::lives,
            SetTeamLivesPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    } 
}
