package com.soc.networking.s2c;

import com.soc.SocWars;
import com.soc.items.DiceOfFate;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public record AddVelocityPayload(Vec3d velocity) implements CustomPayload {
    public static final Identifier ADD_VELOCITY_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "add_velocity");
    public static final Id<AddVelocityPayload> ID = new Id<>(ADD_VELOCITY_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, AddVelocityPayload> CODEC = PacketCodec.tuple(Vec3d.PACKET_CODEC, AddVelocityPayload::velocity, AddVelocityPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
