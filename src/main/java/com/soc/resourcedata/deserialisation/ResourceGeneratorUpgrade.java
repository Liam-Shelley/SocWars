package com.soc.resourcedata.deserialisation;

import com.google.gson.JsonObject;
import com.soc.game.map.GeneratorStats;
import com.soc.lib.json.Time;
import org.jetbrains.annotations.NotNull;

import static com.soc.lib.json.JsonHelper.getDefaultedInt;
import static com.soc.lib.json.JsonHelper.getDefaultedObject;

public record ResourceGeneratorUpgrade(int time, int generationTime, int generationCount) implements Comparable<ResourceGeneratorUpgrade> {
    public static final String TIME_KEY = "time";
    public static final String GENERATION_TIME_KEY = "generation_time";
    public static final String COUNT_KEY = "generation_count";

    public ResourceGeneratorUpgrade(JsonObject json) {
        this(
                getDefaultedObject(json, TIME_KEY, Time::new, new Time(0)).ticks(),
                getDefaultedObject(json, GENERATION_TIME_KEY, Time::new, new Time(0)).ticks(),
                getDefaultedInt(json, COUNT_KEY)
        );
    }

    public GeneratorStats getStats() {
        return new GeneratorStats(this.generationTime, this.generationCount);
    }

    @Override
    public int compareTo(@NotNull ResourceGeneratorUpgrade o) {
        final int timeCompare = Integer.compare(this.time, o.time);
        return timeCompare == 0 ? -Integer.compare(this.generationTime, o.generationTime) : timeCompare;
    }
}