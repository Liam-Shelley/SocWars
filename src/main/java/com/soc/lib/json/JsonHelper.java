package com.soc.lib.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.Reader;
import java.util.function.Consumer;
import java.util.function.Function;

public class JsonHelper {
    private JsonHelper() {}

    public static float getDefaultedFloat(JsonObject json, String key, float def) {
        final JsonElement a = json.get(key);
        try {
            return a == null ? def : a.getAsFloat();
        } catch(Exception e) {
            return def;
        }
    }

    public static float getDefaultedFloat(JsonObject json, String key) {
        return getDefaultedFloat(json, key, 0f);
    }

    public static int getDefaultedInt(JsonObject json, String key, int def) {
        final JsonElement a = json.get(key);
        try {
            return a == null ? def : a.getAsInt();
        } catch(Exception e) {
            return def;
        }
    }

    public static int getDefaultedInt(JsonObject json, String key) {
        return getDefaultedInt(json, key, 0);
    }

    public static <T> T getDefaultedObject(JsonObject json, String key, Function<JsonObject, T> constructor, T def) {
        final JsonObject timeJson = json.getAsJsonObject(key);
        return timeJson == null ? def : constructor.apply(timeJson);
    }

    public static void runFunctionOverArray(Reader reader, Consumer<JsonObject> function) {
        for (JsonElement jsonElement : net.minecraft.util.JsonHelper.deserializeArray(reader)) {
            function.accept(jsonElement.getAsJsonObject());
        }
    }
}
