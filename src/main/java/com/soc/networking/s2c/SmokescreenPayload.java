package com.soc.networking.s2c;

import com.soc.SocWars;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public record SmokescreenPayload(BlockPos pos) implements CustomPayload {
    public static final Identifier SMOKESCREEN_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "smokescreen");
    public static final Id<SmokescreenPayload> ID = new Id<>(SMOKESCREEN_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, SmokescreenPayload> CODEC = PacketCodec.tuple(BlockPos.PACKET_CODEC, SmokescreenPayload::pos, SmokescreenPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}

