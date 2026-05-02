package com.soc.networking.s2c.skywars;

import com.soc.SocWars;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record LeaveSkywarsPayload() implements CustomPayload {
    public static final Identifier END_GAME_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "leave_skywars");
    public static final Id<LeaveSkywarsPayload> ID = new Id<>(END_GAME_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, LeaveSkywarsPayload> CODEC = PacketCodec.unit(new LeaveSkywarsPayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    } 
}
