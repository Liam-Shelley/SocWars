package com.soc.networking.s2c.bedwars;

import com.soc.SocWars;
import com.soc.game.manager.bedwars.BedwarsShopContents;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record BedwarsShopDataPayload(BedwarsShopContents shopContents, int syncId) implements CustomPayload {
    public static final Identifier INDIVIDUAL_SHOP_DATA_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "individual_shop_data");
    public static final Id<BedwarsShopDataPayload> ID = new Id<>(INDIVIDUAL_SHOP_DATA_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, BedwarsShopDataPayload> CODEC = PacketCodec.tuple(BedwarsShopContents.PACKET_CODEC, BedwarsShopDataPayload::shopContents, PacketCodecs.INTEGER, BedwarsShopDataPayload::syncId, BedwarsShopDataPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
