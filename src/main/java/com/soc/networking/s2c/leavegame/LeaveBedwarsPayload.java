package com.soc.networking.s2c.leavegame;

import com.soc.SocWars;
import com.soc.game.manager.GameType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record LeaveBedwarsPayload() implements CustomPayload {
    public static final Identifier END_GAME_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "end_game");
    public static final Id<LeaveBedwarsPayload> ID = new Id<>(END_GAME_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, LeaveBedwarsPayload> CODEC = PacketCodec.unit(new LeaveBedwarsPayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    } 
}
