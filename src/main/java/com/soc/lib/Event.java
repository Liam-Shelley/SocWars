package com.soc.lib;

import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public record Event<T>(Consumer<T> function, T context, long time) implements Comparable<Event<?>> {
    @Override
    public int compareTo(@NotNull Event other) {
        return Long.compare(this.time, other.time);
    }

    public Event(Runnable function, long time) {
        this(context -> function.run(), null, time);
    }

    public void run() {
        this.function.accept(this.context);
    }
}
