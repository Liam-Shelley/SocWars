package com.soc.networking.s2c;

import com.soc.SocWars;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public record JumpscarePayload(SoundEvent sound, Identifier image) implements CustomPayload {
    public static final Identifier ADD_VELOCITY_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "jumpscare");
    public static final Id<JumpscarePayload> ID = new Id<>(ADD_VELOCITY_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, JumpscarePayload> CODEC = PacketCodec.tuple(SoundEvent.PACKET_CODEC, JumpscarePayload::sound, Identifier.PACKET_CODEC, JumpscarePayload::image, JumpscarePayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
