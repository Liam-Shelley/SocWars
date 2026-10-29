package com.soc.networking.c2s;

import com.soc.SocWars;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record UpdateDoubleJumpPayload(boolean allow) implements CustomPayload {
    public static final Identifier UPDATE_DOUBLE_JUMP_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "update_double_jump");
    public static final Id<UpdateDoubleJumpPayload> ID = new Id<>(UPDATE_DOUBLE_JUMP_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, UpdateDoubleJumpPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOLEAN, UpdateDoubleJumpPayload::allow,
            UpdateDoubleJumpPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
