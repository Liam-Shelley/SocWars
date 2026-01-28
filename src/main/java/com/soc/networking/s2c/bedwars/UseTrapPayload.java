package com.soc.networking.s2c.bedwars;

import com.soc.SocWars;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record UseTrapPayload() implements CustomPayload {
    public static final Identifier USE_TRAP_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "use_trap");
    public static final Id<UseTrapPayload> ID = new Id<>(USE_TRAP_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, UseTrapPayload> CODEC = PacketCodec.unit(new UseTrapPayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
