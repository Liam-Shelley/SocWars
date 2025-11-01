package com.soc.resourcedata;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public record SkywarsItemData(float weightT1, float weightT2, float weightT3, float weightT4, int count) {
    public static final String WEIGHT_T1_KEY = "tier_1_weight";
    public static final String WEIGHT_T2_KEY = "tier_2_weight";
    public static final String WEIGHT_T3_KEY = "tier_3_weight";
    public static final String WEIGHT_T4_KEY = "tier_4_weight";
    public static final String COUNT_KEY = "count";

    public SkywarsItemData(JsonObject json) {
        this(getDefaultedFloat(json, WEIGHT_T1_KEY), getDefaultedFloat(json, WEIGHT_T2_KEY), getDefaultedFloat(json, WEIGHT_T3_KEY), getDefaultedFloat(json, WEIGHT_T4_KEY), getDefaultedInt(json, COUNT_KEY));
    }

    private static float getDefaultedFloat(JsonObject json, String key) {
        final JsonElement a = json.get(key);
        try {
            return a == null ? 0f : a.getAsFloat();
        } catch(Exception e) {
            return 0f;
        }
    }

    private static int getDefaultedInt(JsonObject json, String key) {
        final JsonElement a = json.get(key);
        try {
            return a == null ? 0 : a.getAsInt();
        } catch(Exception e) {
            return 0;
        }
    }
}