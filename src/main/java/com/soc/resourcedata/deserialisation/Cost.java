package com.soc.resourcedata.deserialisation;

import com.google.gson.JsonObject;

import static com.soc.lib.json.JsonHelper.getDefaultedInt;

public record Cost(int iron, int gold, int diamonds, int emeralds) {
    public static final Cost DEFAULT = new Cost(0, 0, 0, 0);
    public static final Cost ERROR_SIGNAL = new Cost(999, 999, 999, 999);

    public static final String KEY = "cost";

    private static final String IRON_COUNT_KEY = "iron";
    private static final String GOLD_COUNT_KEY = "gold";
    private static final String DIAMONDS_COUNT_KEY = "diamonds";
    private static final String EMERALDS_COUNT_KEY = "emeralds";

    public Cost(JsonObject object) {
        this(
                getDefaultedInt(object, IRON_COUNT_KEY),
                getDefaultedInt(object, GOLD_COUNT_KEY),
                getDefaultedInt(object, DIAMONDS_COUNT_KEY),
                getDefaultedInt(object, EMERALDS_COUNT_KEY)
        );
    }
}
