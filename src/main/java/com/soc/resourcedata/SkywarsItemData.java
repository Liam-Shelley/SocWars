package com.soc.resourcedata;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public record SkywarsItemData(float weightT1, float weightT2, float weightT3, float weightT4, int count, int pool) {
    public static final String WEIGHT_T1_KEY = "tier_1_weight";
    public static final String WEIGHT_T2_KEY = "tier_2_weight";
    public static final String WEIGHT_T3_KEY = "tier_3_weight";
    public static final String WEIGHT_T4_KEY = "tier_4_weight";
    public static final String COUNT_KEY = "count";
    public static final String POOL_KEY = "pool";

    public SkywarsItemData(JsonObject json) {
        this(getDefaultedFloat(json, WEIGHT_T1_KEY, 0f), getDefaultedFloat(json, WEIGHT_T2_KEY, 0f), getDefaultedFloat(json, WEIGHT_T3_KEY, 0f), getDefaultedFloat(json, WEIGHT_T4_KEY, 0f), getDefaultedInt(json, COUNT_KEY, 1), getDefaultedInt(json, POOL_KEY, 0));
    }

    private static float getDefaultedFloat(JsonObject json, String key, float def) {
        final JsonElement a = json.get(key);
        try {
            return a == null ? def : a.getAsFloat();
        } catch(Exception e) {
            return def;
        }
    }

    private static int getDefaultedInt(JsonObject json, String key, int def) {
        final JsonElement a = json.get(key);
        try {
            return a == null ? def : a.getAsInt();
        } catch(Exception e) {
            return def;
        }
    }
}