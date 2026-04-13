package com.soc.networking.s2c;

import com.soc.SocWars;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SetAnglesPayload(int entityId, float yaw, float pitch) implements CustomPayload {
    public static final Identifier SET_ANGLES_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "set_angles");
    public static final CustomPayload.Id<SetAnglesPayload> ID = new CustomPayload.Id<>(SET_ANGLES_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, SetAnglesPayload> CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, SetAnglesPayload::entityId, PacketCodecs.FLOAT, SetAnglesPayload::yaw, PacketCodecs.FLOAT, SetAnglesPayload::pitch, SetAnglesPayload::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
