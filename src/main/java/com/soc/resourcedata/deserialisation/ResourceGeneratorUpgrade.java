package com.soc.resourcedata.deserialisation;

import com.google.gson.JsonObject;
import com.soc.game.map.GeneratorStats;
import com.soc.lib.json.Time;

import static com.soc.lib.json.JsonHelper.getDefaultedInt;

public record ResourceGeneratorUpgrade(int time, int generationTime, int generationCount) {
    public static final String TIME_KEY = "time";
    public static final String GENERATION_TIME_KEY = "generation_time";
    public static final String COUNT_KEY = "generation_count";

    public ResourceGeneratorUpgrade(JsonObject json) {
        this(
                new Time(json.getAsJsonObject(TIME_KEY)).ticks(),
                getDefaultedInt(json, GENERATION_TIME_KEY, 0),
                getDefaultedInt(json, COUNT_KEY, 0)
        );
    }

    public GeneratorStats getStats() {
        return new GeneratorStats(this.generationTime, this.generationCount);
    }
}