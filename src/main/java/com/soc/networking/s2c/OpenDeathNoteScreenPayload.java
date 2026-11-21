package com.soc.networking.s2c;

import com.soc.SocWars;
import com.soc.game.manager.GameType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public record OpenDeathNoteScreenPayload(List<String> names) implements CustomPayload {
    public static final Identifier OPEN_DEATH_NOTE_SCREEN_ID = Identifier.of(SocWars.MOD_ID, "open_death_note_screen");
    public static final Id<OpenDeathNoteScreenPayload> ID = new Id<>(OPEN_DEATH_NOTE_SCREEN_ID);
    public static final PacketCodec<RegistryByteBuf, OpenDeathNoteScreenPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.collection(ArrayList::new, PacketCodecs.STRING), OpenDeathNoteScreenPayload::names,
            OpenDeathNoteScreenPayload::new
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
