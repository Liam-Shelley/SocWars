package com.soc.networking.s2c;

import com.soc.SocWars;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record EndGamePayload() implements CustomPayload {
    public static final Identifier END_GAME_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "end_game");
    public static final Id<EndGamePayload> ID = new Id<>(END_GAME_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, EndGamePayload> CODEC = PacketCodec.unit(new EndGamePayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    } 
}
