package com.soc.networking.s2c.bedwars;

import com.soc.SocWars;
import com.soc.game.manager.bedwars.BedwarsShopContents;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record BedwarsTeamShopDataPayload(BedwarsShopContents shopContents, int syncId) implements CustomPayload {
    public static final Identifier TEAM_SHOP_DATA_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "team_shop_data");
    public static final Id<BedwarsTeamShopDataPayload> ID = new Id<>(TEAM_SHOP_DATA_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, BedwarsTeamShopDataPayload> CODEC = PacketCodec.tuple(BedwarsShopContents.PACKET_CODEC, BedwarsTeamShopDataPayload::shopContents, PacketCodecs.INTEGER, BedwarsTeamShopDataPayload::syncId, BedwarsTeamShopDataPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
