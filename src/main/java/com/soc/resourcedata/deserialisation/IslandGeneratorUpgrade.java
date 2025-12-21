package com.soc.resourcedata.deserialisation;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import static com.soc.lib.json.JsonHelper.getDefaultedInt;

public record IslandGeneratorUpgrade(int cost, int ironTime, int ironCount, int goldTime, int goldCount, int emeraldTime, int emeraldCount) implements Comparable<IslandGeneratorUpgrade> {
    public static final String COST_KEY = "cost";
    public static final String IRON_TIME_KEY = "iron_generation_time";
    public static final String IRON_COUNT_KEY = "iron_generation_count";
    public static final String GOLD_TIME_KEY = "gold_generation_time";
    public static final String GOLD_COUNT_KEY = "gold_generation_count";
    public static final String EMERALD_TIME_KEY = "emerald_generation_time";
    public static final String EMERALD_COUNT_KEY = "emerald_generation_count";

    public IslandGeneratorUpgrade(JsonObject json) {
        this(
                getDefaultedInt(json, COST_KEY),
                getDefaultedInt(json, IRON_TIME_KEY),
                getDefaultedInt(json, IRON_COUNT_KEY),
                getDefaultedInt(json, GOLD_TIME_KEY),
                getDefaultedInt(json, GOLD_COUNT_KEY),
                getDefaultedInt(json, EMERALD_TIME_KEY),
                getDefaultedInt(json, EMERALD_COUNT_KEY)
        );
    }

    @Override
    public int compareTo(@NotNull IslandGeneratorUpgrade o) {
        return Integer.compare(this.cost, o.cost);
    }
}
