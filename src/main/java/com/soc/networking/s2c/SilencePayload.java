package com.soc.networking.s2c;

import com.soc.SocWars;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public record SilencePayload(long time) implements CustomPayload {
    public static final Identifier SILENCE_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "silence");
    public static final Id<SilencePayload> ID = new Id<>(SILENCE_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, SilencePayload> CODEC = PacketCodec.tuple(PacketCodecs.LONG, SilencePayload::time, SilencePayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
