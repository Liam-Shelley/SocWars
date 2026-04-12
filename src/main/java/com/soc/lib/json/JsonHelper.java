package com.soc.lib.json;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.soc.SocWars;
import com.soc.game.manager.bedwars.tickfunctions.AbstractTickFunction;
import com.soc.game.manager.bedwars.tickfunctions.TickFunctions;
import com.soc.game.manager.bedwars.traps.Abilities;
import com.soc.game.manager.bedwars.traps.AbstractAbility;
import com.soc.game.manager.bedwars.traps.AbstractTrap;
import com.soc.game.manager.bedwars.traps.Traps;
import com.soc.game.map.DyeColourWithEmpty;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Reader;
import java.io.StringReader;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class JsonHelper {
    public static final String ITEM_KEY = "item";
    public static final String ITEM_COUNT_KEY = "count";
    public static final String ITEM_ENCHANTMENTS_KEY = "enchantments";
    public static final String TIME_KEY = "time";

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
        if (json.get(key) == null || !json.get(key).isJsonObject()) return def;

        final JsonObject objectJson = json.getAsJsonObject(key);
        return objectJson == null ? def : constructor.apply(objectJson);
    }

    @Nullable
    public static <T> T getDefaultedObject(JsonObject json, String key, Function<JsonObject, T> constructor) {
        return getDefaultedObject(json, key, constructor, null);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T> List<T> getDefaultedObjectList(JsonObject json, String key, Function<JsonObject, T> constructor, @NotNull Optional<T> def) {
        if (json.get(key) == null || !json.get(key).isJsonArray()) return List.of();

        final JsonArray jsonList = json.getAsJsonArray(key);
        final List<T> destList = new ArrayList<>();
        jsonList.forEach(element -> {
            if (!element.isJsonObject()) {
                SocWars.LOGGER.warn("Failed to deserialise element: {} from JsonObject: {}", element, json);
                def.ifPresent(destList::add);
            } else {
                destList.add(constructor.apply(element.getAsJsonObject()));
            }
        });

        return destList;
    }

    public static <T> List<T> getDefaultedObjectList(JsonObject json, String key, Function<JsonObject, T> constructor) {
        return getDefaultedObjectList(json, key, constructor, Optional.empty());
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
        final JsonElement element = object.get(key);
        if (element == null) return def;

        final Identifier id = Identifier.of(element.getAsString());
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

    public static AbstractTrap getDefaultedTrap(JsonObject json, String key) {
        final JsonElement element = json.get(key);
        final Identifier id = Identifier.of(element.getAsString());

        return Traps.REGISTRY.containsId(id) ? Traps.REGISTRY.get(id) : null; //I know that this is currently useless but I will put an empty trapitem here at some point
    }

    public static AbstractAbility getDefaultedAbility(JsonObject json, String key) {
        final JsonElement element = json.get(key);
        final Identifier id = Identifier.of(element.getAsString());

        return Abilities.REGISTRY.containsId(id) ? Abilities.REGISTRY.get(id) : null;
    }

    public static AbstractTickFunction getDefaultedTickFunction(JsonObject json, String key) {
        final JsonElement element = json.get(key);
        final Identifier id = Identifier.of(element.getAsString());

        return TickFunctions.REGISTRY.containsId(id) ? TickFunctions.REGISTRY.get(id) : null; //Ditto
    }

    public static Text getDefaultedText(JsonObject json, String key, Text def) {
        //TODO: Maybe write this function at some point instead of it being a bunch of experimental garbage
        ((JsonElement)null).isJsonObject(); //Deliberately causing an error so that I remember to fix this at some point instead of wondering why something is weird

        final JsonElement element = json.get(key);

        final Text text = Text.literal("hello this should be blue").formatted(Formatting.BLUE);

        final DataResult<JsonElement> a = TextCodecs.CODEC.encodeStart(JsonOps.INSTANCE, text);

        a.ifSuccess(success -> {
            String asString = new Gson().toJson(success);
            SocWars.LOGGER.info(asString);
            DataResult<Pair<Text, JsonElement>> b = TextCodecs.CODEC.decode(JsonOps.INSTANCE, new Gson().fromJson(asString, JsonElement.class));
            b.ifSuccess(success2 -> {
                SocWars.LOGGER.info(success2.getFirst().toString());
                SocWars.LOGGER.info(success2.getSecond().toString());
            });
        });
        if (element == null) return def;

        return null;
    }

    public static Text getDefaultedText(JsonObject json, String key) {
        return getDefaultedText(json, key, Text.empty());
    }
}
