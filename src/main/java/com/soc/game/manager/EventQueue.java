package com.soc.game.manager;

import com.soc.lib.SocWarsLib;
import com.soc.lib.json.Time;
import net.minecraft.text.Text;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import java.util.*;
import java.util.function.Consumer;

public class EventQueue<T extends AbstractGameManager> {
    private final SortedSet<Event<T>> events;

    public EventQueue() {
        this.events = new TreeSet<>();
    }

    public EventQueue(Set<Event<T>> events) {
        this();
        this.events.addAll(events);
    }

    public void addEvent(int time, Consumer<T> event, Text name) {
        events.add(new Event<>(time, event, name));
    }

    public void addEvent(Time time, Consumer<T> event, Text name) {
        events.add(new Event<>(time.ticks(), event, name));
    }

    public int peekTime() {
        return this.events.getFirst().time();
    }

    public Consumer<T> peekEvent() {
        return this.events.getFirst().callback();
    }

    public Collection<Pair<Integer, Text>> peekEvents(int time) {
        final ArrayList<Pair<Integer, Text>> events = new ArrayList<>();

        while (time >= this.events.getFirst().time()) {
            Event<T> event = this.events.getFirst();
            events.add(Pair.of(event.time(), event.name()));
        }

        return events;
    }

    public Collection<Text> peekEventsNames(int time) {
        final ArrayList<Text> events = new ArrayList<>();

        while (time >= this.events.getFirst().time()) {
            Event<T> event = this.events.getFirst();
            events.add(Text.translatable("event.name_and_time", event.name(), SocWarsLib.getTimeFromTicks(event.time(), false)));
        }

        return events;
    }

    public Collection<Pair<Consumer<T>, Text>> tryPopEvents(int time) {
        final ArrayList<Pair<Consumer<T>, Text>> events = new ArrayList<>();

        while (!this.events.isEmpty() && time >= this.events.getFirst().time()) {
            Event<T> event = this.events.removeFirst();
            events.add(Pair.of(event.callback(), event.name()));
        }

        return events;
    }
}
