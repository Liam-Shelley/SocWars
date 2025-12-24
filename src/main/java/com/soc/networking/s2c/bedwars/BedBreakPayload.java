package com.soc.networking.s2c.bedwars;

import com.soc.SocWars;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

public record BedBreakPayload(DyeColor team) implements CustomPayload {
    public static final Identifier LOSE_BED_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "lose_bed");
    public static final Id<BedBreakPayload> ID = new Id<>(LOSE_BED_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, BedBreakPayload> CODEC = PacketCodec.tuple(DyeColor.PACKET_CODEC, BedBreakPayload::team, BedBreakPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
