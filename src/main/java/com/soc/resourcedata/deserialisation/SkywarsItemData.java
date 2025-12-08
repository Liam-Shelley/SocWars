package com.soc.resourcedata.deserialisation;

import com.google.gson.JsonObject;
import com.soc.resourcedata.containers.ItemDataAccess;
import net.minecraft.item.Item;

import java.util.HashMap;
import java.util.function.Consumer;

import static com.soc.lib.json.JsonHelper.getDefaultedFloat;
import static com.soc.lib.json.JsonHelper.getDefaultedInt;

public record SkywarsItemData(float weightT1, float weightT2, float weightT3, float weightT4, int count) implements ItemDataAccess<SkywarsItemData> {
    public static final String WEIGHT_T1_KEY = "tier_1_weight";
    public static final String WEIGHT_T2_KEY = "tier_2_weight";
    public static final String WEIGHT_T3_KEY = "tier_3_weight";
    public static final String WEIGHT_T4_KEY = "tier_4_weight";
    public static final String COUNT_KEY = "count";
    public static final String POOL_KEY = "pool";

    public SkywarsItemData(JsonObject json) {
        this(
                getDefaultedFloat(json, WEIGHT_T1_KEY, 0f),
                getDefaultedFloat(json, WEIGHT_T2_KEY, 0f),
                getDefaultedFloat(json, WEIGHT_T3_KEY, 0f),
                getDefaultedFloat(json, WEIGHT_T4_KEY, 0f),
                getDefaultedInt(json, COUNT_KEY, 1)
        );
    }

    public float getWeight(int tier) {
        return switch (tier) {
            case 0 -> this.weightT1;
            case 1 -> this.weightT2;
            case 2 -> this.weightT3;
            case 3 -> this.weightT4;
            default -> throw new IllegalArgumentException("Unexpected value: " + tier);
        };
    }

    @Override
    public Consumer<HashMap<Integer, HashMap<Item, SkywarsItemData>>> acceptFunction(Integer pool, Item key) {
        return map -> {
            if (!map.containsKey(pool)) map.put(pool, new HashMap<>());
            map.get(pool).put(key, this);
        };
    }
}