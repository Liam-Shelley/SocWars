package com.soc.networking.s2c;

import com.soc.SocWars;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record LeaveGamePayload() implements CustomPayload {
    public static final Identifier LEAVE_GAME_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "leave_game");
    public static final Id<LeaveGamePayload> ID = new Id<>(LEAVE_GAME_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, LeaveGamePayload> CODEC = PacketCodec.unit(new LeaveGamePayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
