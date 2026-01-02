package com.soc.lib.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.soc.SocWars;
import com.soc.game.map.DyeColourWithEmpty;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JsonHelper {
    public static final String ITEM_KEY = "item";
    public static final String ITEM_COUNT_KEY = "count";
    public static final String ITEM_ENCHANTMENTS_KEY = "enchantments";

    private JsonHelper() {}

    public static boolean getDefaultedBoolean(JsonObject json, String key, boolean def) {
        final JsonElement element = json.get(key);
        try {
            return element == null ? def : element.getAsBoolean();
        } catch(Exception ignored) {
            return def;
        }
    }

    public static boolean getDefaultedBoolean(JsonObject json, String key) {
        return getDefaultedBoolean(json, key, false);
    }

    public static int getDefaultedInt(JsonObject json, String key, int def) {
        final JsonElement element = json.get(key);
        try {
            return element == null ? def : element.getAsInt();
        } catch(Exception ignored) {
            return def;
        }
    }

    public static int getDefaultedInt(JsonObject json, String key) {
        return getDefaultedInt(json, key, 0);
    }

    public static float getDefaultedFloat(JsonObject json, String key, float def) {
        final JsonElement element = json.get(key);
        try {
            return element == null ? def : element.getAsFloat();
        } catch(Exception ignored) {
            return def;
        }
    }

    public static float getDefaultedFloat(JsonObject json, String key) {
        return getDefaultedFloat(json, key, 0f);
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

    public static void runFunctionOverArray(JsonObject object, String key, Consumer<JsonObject> function) {
        SocWars.LOGGER.warn("MAKE SURE THIS FUNCTION ACTUALLY WORKS");
        runFunctionOverArray(new StringReader(object.get(key).getAsString()), function);
    }

    public static ItemStack getDefaultedItem(JsonObject object, String key, ItemStack def) {
        final Identifier id = Identifier.of(object.get(key).getAsString());

        if (!Registries.ITEM.containsId(id)) return def;

        final ItemStack stack = new ItemStack(
                Registries.ITEM.get(id),
                getDefaultedInt(object, ITEM_COUNT_KEY, 1)
        );

        return stack;
    }

    public static ItemStack getDefaultedItem(JsonObject object, ItemStack def) {
        return getDefaultedItem(object, ITEM_KEY, def);
    }

    public static ItemStack getDefaultedItem(JsonObject object) {
        return getDefaultedItem(object, ItemStack.EMPTY);
    }

    //Absolute goddamn mess of a function but oh well I don't have to look at it
    public static Map<DyeColourWithEmpty, ItemStack> getDoubleDefaultedDyeColourItemStackMap(JsonObject object, String key, ItemStack doubleDef) {
        final JsonElement element = object.get(key);
        if (!element.isJsonObject()) {
            final ItemStack finalDoubleDef = doubleDef;
            return Arrays.stream(DyeColourWithEmpty.values()).collect(Collectors.toMap(Function.identity(), colour -> finalDoubleDef));
        }

        final int count = getDefaultedInt(object, ITEM_COUNT_KEY, 1);

        final JsonObject mapObject = element.getAsJsonObject();
        try {
            doubleDef = getItemFromString(mapObject.get("default").getAsString(), count, doubleDef);
        } catch (Exception ignored) {
            SocWars.LOGGER.warn("Failed to read default for DyeColour-ItemStack Map, falling back to secondary default: {}", doubleDef.getItemName());
        }

        final ItemStack def = doubleDef;
        return Arrays.stream(DyeColourWithEmpty.values()).collect(Collectors.toMap(Function.identity(), colour -> {
            try {
                return getItemFromString(mapObject.get(colour.toStringWithEmptyAlias("default")).getAsString(), count, def);
            } catch (Exception ignored) {
                return def;
            }
        }));
    }

    private static ItemStack getItemFromString(String string, int count, ItemStack def) {
        final Identifier id = Identifier.of(string);
        return Registries.ITEM.containsId(id) ? new ItemStack(Registries.ITEM.get(id), count) : def;
    }
}
