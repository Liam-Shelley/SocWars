package com.soc.networking.s2c;

import com.soc.SocWars;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record UpdatePreventFreecamPayload(boolean prevent) implements CustomPayload {
    public static final Identifier UPDATE_PREVENT_FREECAM_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "update_prevent_freecam");
    public static final Id<UpdatePreventFreecamPayload> ID = new Id<>(UPDATE_PREVENT_FREECAM_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, UpdatePreventFreecamPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.BOOLEAN,
            UpdatePreventFreecamPayload::prevent,
            UpdatePreventFreecamPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
