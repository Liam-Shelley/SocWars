package com.soc.networking.s2c;

import com.soc.SocWars;
import com.soc.blocks.blockentities.KitBlockEntity;
import com.soc.networking.HoldsBlockEntity;
import com.soc.networking.helper.BlockLocation;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record KitBlockEntityAssignment(BlockLocation block) implements CustomPayload {
    public static final Identifier KIT_BLOCK_ENTITY_ASSIGNMENT_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "kit_block_entity_assignment");
    public static final Id<KitBlockEntityAssignment> ID = new Id<>(KIT_BLOCK_ENTITY_ASSIGNMENT_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, KitBlockEntityAssignment> CODEC = PacketCodec.tuple(
            BlockLocation.PACKET_CODEC, KitBlockEntityAssignment::block,
            KitBlockEntityAssignment::new
    );

    public KitBlockEntityAssignment(KitBlockEntity blockEntity) {
        this(new BlockLocation(blockEntity));
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
