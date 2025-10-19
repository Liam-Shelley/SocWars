package com.soc.networking.s2c;

import com.soc.SocWars;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record StartGamePayload(int gameId) implements CustomPayload {
    public static final Identifier START_GAME_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "start_game");
    public static final Id<StartGamePayload> ID = new Id<>(START_GAME_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, StartGamePayload> CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, StartGamePayload::gameId, StartGamePayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    } 
}
