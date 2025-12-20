package com.soc.networking.s2c;

import com.soc.SocWars;
import com.soc.game.manager.bedwars.BedwarsShopContents;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ShopDataPayload(BedwarsShopContents shopContents, int syncId) implements CustomPayload {
    public static final Identifier SHOP_DATA_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "shop_data");
    public static final Id<ShopDataPayload> ID = new Id<>(SHOP_DATA_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, ShopDataPayload> CODEC = PacketCodec.tuple(BedwarsShopContents.PACKET_CODEC, ShopDataPayload::shopContents, PacketCodecs.INTEGER, ShopDataPayload::syncId, ShopDataPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
