package com.soc.networking.c2s;

import com.soc.SocWars;
import com.soc.networking.ModPacketCodecs;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.UUID;

public record DeathNoteKillPayload(UUID uuid) implements CustomPayload {
    public static final Identifier DEATH_NOTE_KILL_PAYLOAD_ID = Identifier.of(SocWars.MOD_ID, "death_note_kill");
    public static final Id<DeathNoteKillPayload> ID = new Id<>(DEATH_NOTE_KILL_PAYLOAD_ID);
    public static final PacketCodec<RegistryByteBuf, DeathNoteKillPayload> CODEC = PacketCodec.tuple(
            ModPacketCodecs.UUID, DeathNoteKillPayload::uuid,
            DeathNoteKillPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
