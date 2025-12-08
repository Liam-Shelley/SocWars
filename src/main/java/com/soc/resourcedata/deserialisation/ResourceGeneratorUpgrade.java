package com.soc.resourcedata.deserialisation;

import com.google.gson.JsonObject;

import static com.soc.lib.json.JsonHelper.getDefaultedInt;

public record ResourceGeneratorUpgrade(int generationTime, int generationCount) {
    public static final String TIME_KEY = "generation_time";
    public static final String COUNT_KEY = "generation_count";

    public ResourceGeneratorUpgrade(JsonObject json) {
        this(
                getDefaultedInt(json, TIME_KEY, 0),
                getDefaultedInt(json, COUNT_KEY, 0)
        );
    }
}