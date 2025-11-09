package com.soc.resourcedata;

import com.google.gson.JsonObject;

import static com.soc.lib.JsonHelper.getDefaultedInt;

public record IslandGeneratorUpgrade(int cost, int ironTime, int ironCount, int goldTime, int goldCount, int emeraldTime, int emeraldCount) {
    public static final String COST_KEY = "cost";
    public static final String IRON_TIME_KEY = "iron_generation_time";
    public static final String IRON_COUNT_KEY = "iron_generation_count";
    public static final String GOLD_TIME_KEY = "gold_generation_time";
    public static final String GOLD_COUNT_KEY = "gold_generation_count";
    public static final String EMERALD_TIME_KEY = "emerald_generation_time";
    public static final String EMERALD_COUNT_KEY = "emerald_generation_count";

    public IslandGeneratorUpgrade(JsonObject json) {
        this(
                getDefaultedInt(json, COST_KEY, 1),
                getDefaultedInt(json, IRON_TIME_KEY, 0),
                getDefaultedInt(json, IRON_COUNT_KEY, 0),
                getDefaultedInt(json, GOLD_TIME_KEY, 0),
                getDefaultedInt(json, GOLD_COUNT_KEY, 0),
                getDefaultedInt(json, EMERALD_TIME_KEY, 0),
                getDefaultedInt(json, EMERALD_COUNT_KEY, 0)
        );
    }
}
