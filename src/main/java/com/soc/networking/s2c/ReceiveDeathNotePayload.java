package com.soc.networking.s2c;

import com.soc.SocWars;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.stream.Collectors;

public record ReceiveDeathNotePayload() implements CustomPayload {
    public static final Identifier RECEIVE_DEATH_NOTE_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "receive_death_note");
    public static final Id<ReceiveDeathNotePayload> ID = new Id<>(RECEIVE_DEATH_NOTE_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, ReceiveDeathNotePayload> CODEC = PacketCodec.unit(new ReceiveDeathNotePayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
