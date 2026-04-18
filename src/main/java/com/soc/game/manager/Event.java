package com.soc.game.manager;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public record Event<T>(int time, Consumer<T> callback, Text name) implements Comparable<Event<T>> {
    public record ClientDisplayEvent(int time, Text name) {
        public static final PacketCodec<RegistryByteBuf, ClientDisplayEvent> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, ClientDisplayEvent::time, TextCodecs.PACKET_CODEC, ClientDisplayEvent::name, ClientDisplayEvent::new);
    }

    @Override
    public int compareTo(@NotNull Event<T> o) {
        return Integer.compare(this.time, o.time);
    }

    public ClientDisplayEvent getDisplayCopy() {
        return new ClientDisplayEvent(this.time, this.name);
    }
}
