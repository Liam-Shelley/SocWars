package com.soc.networking.s2c;

import com.soc.SocWars;
import com.soc.game.manager.GameType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

public record TeamEliminatedPayload(DyeColor team, GameType gameType) implements CustomPayload {
    public static final Identifier TEAM_ELIMINATED_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "team_eliminated");
    public static final Id<TeamEliminatedPayload> ID = new Id<>(TEAM_ELIMINATED_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, TeamEliminatedPayload> CODEC = PacketCodec.tuple(
            DyeColor.PACKET_CODEC, TeamEliminatedPayload::team,
            GameType.PACKET_CODEC, TeamEliminatedPayload::gameType,
            TeamEliminatedPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

