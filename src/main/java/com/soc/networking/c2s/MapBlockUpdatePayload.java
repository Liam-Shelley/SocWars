package com.soc.networking.c2s;

import com.soc.SocWars;
import com.soc.networking.HoldsBlockEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record MapBlockUpdatePayload(long pos, long regionSize, String mapName, int mapType, boolean blockProtection) implements CustomPayload, HoldsBlockEntity {
    public static final Identifier MAP_BLOCK_UPDATE_ID = Identifier.of(SocWars.MOD_ID, "map_block_update");
    public static final Id<MapBlockUpdatePayload> ID = new Id<>(MAP_BLOCK_UPDATE_ID);
    public static final PacketCodec<RegistryByteBuf, MapBlockUpdatePayload> CODEC = PacketCodec.tuple(PacketCodecs.LONG, MapBlockUpdatePayload::pos, PacketCodecs.LONG, MapBlockUpdatePayload::regionSize, PacketCodecs.STRING, MapBlockUpdatePayload::mapName, PacketCodecs.INTEGER, MapBlockUpdatePayload::mapType, PacketCodecs.BOOLEAN, MapBlockUpdatePayload::blockProtection, MapBlockUpdatePayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }

    @Override
    public BlockEntity getBlockEntity(ServerPlayNetworking.Context context) {
        return this.getBlockEntity(context, this.pos);
    }
}
