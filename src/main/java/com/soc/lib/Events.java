package com.soc.lib;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.world.World;

import java.util.function.Consumer;

public class Events {
    private final static Events INSTANCE = new Events();
    public static Events getInstance() {
        return INSTANCE;
    }

    private final SortedList<Event<?>> events = new SortedList<>();
    private World world;

    private Events() {}

    public static void initialise() {
        final Events instance = getInstance();
        ServerLifecycleEvents.SERVER_STARTED.register(server -> instance.world = server.getOverworld());
        ServerTickEvents.START_SERVER_TICK.register(server -> instance.tryRunEvents());
    }

    private void tryRunEvents() {
        while (!this.events.isEmpty() && this.events.getFirst().time() <= this.world.getTime()) {
            this.events.removeFirst().run();
        }
    }

    public <T> void scheduleEvent(Consumer<T> function, T context, long time) {
        this.events.add(new Event<>(function, context, world.getTime() + time));
    }

    public <T> void scheduleEvent(Runnable function, long time) {
        this.events.add(new Event<>(function, world.getTime() + time));
    }
}
