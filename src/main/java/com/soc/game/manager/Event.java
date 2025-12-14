package com.soc.game.manager;

import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public record Event<T>(int time, Consumer<T> callback, Text name) implements Comparable<Event<T>> {


    @Override
    public int compareTo(@NotNull Event<T> o) {
        return Integer.compare(this.time, o.time);
    }
}
