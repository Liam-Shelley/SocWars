package com.soc.lib.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.Reader;
import java.util.function.Consumer;

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

    public static int getDefaultedInt(JsonObject json, String key, int def) {
        final JsonElement a = json.get(key);
        try {
            return a == null ? def : a.getAsInt();
        } catch(Exception e) {
            return def;
        }
    }

    public static void runFunctionOverArray(Reader reader, Consumer<JsonObject> function) {
        for (JsonElement jsonElement : net.minecraft.util.JsonHelper.deserializeArray(reader)) {
            function.accept(jsonElement.getAsJsonObject());
        }
    }
}
