package com.soc.resourcedata.deserialisation;

import com.google.gson.JsonObject;
import com.soc.lib.json.Time;
import org.jetbrains.annotations.NotNull;

import static com.soc.lib.json.JsonHelper.*;

public record IslandGeneratorUpgrade(int autoUpgradeTime, int ironTime, int ironCount, int ironMaxCount, int goldTime, int goldCount, int goldMaxCount, int emeraldTime, int emeraldCount, int emeraldMaxCount) implements Comparable<IslandGeneratorUpgrade> {
    public static final String IRON_TIME_KEY = "iron_generation_time";
    public static final String IRON_COUNT_KEY = "iron_generation_count";
    public static final String IRON_MAX_COUNT_KEY = "iron_max_count";
    public static final String GOLD_TIME_KEY = "gold_generation_time";
    public static final String GOLD_COUNT_KEY = "gold_generation_count";
    public static final String GOLD_MAX_COUNT_KEY = "gold_max_count";
    public static final String EMERALD_TIME_KEY = "emerald_generation_time";
    public static final String EMERALD_COUNT_KEY = "emerald_generation_count";
    public static final String EMERALD_MAX_COUNT_KEY = "emerald_max_count";

    public IslandGeneratorUpgrade(JsonObject json) {
        this(
                getDefaultedObject(json, TIME_KEY, Time::new, new Time(0)).ticks(),
                getDefaultedInt(json, IRON_TIME_KEY),
                getDefaultedInt(json, IRON_COUNT_KEY),
                getDefaultedInt(json, IRON_MAX_COUNT_KEY, 64),
                getDefaultedInt(json, GOLD_TIME_KEY),
                getDefaultedInt(json, GOLD_COUNT_KEY),
                getDefaultedInt(json, GOLD_MAX_COUNT_KEY, 64),
                getDefaultedInt(json, EMERALD_TIME_KEY),
                getDefaultedInt(json, EMERALD_COUNT_KEY),
                getDefaultedInt(json, EMERALD_MAX_COUNT_KEY, 64)
        );
    }

    @Override
    public int compareTo(@NotNull IslandGeneratorUpgrade o) {
        return Integer.compare(this.autoUpgradeTime, o.autoUpgradeTime);
    }
}
