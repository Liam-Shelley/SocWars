package com.soc.resourcedata.deserialisation;

import com.google.gson.JsonObject;
import com.soc.SocWars;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

import static com.soc.lib.json.JsonHelper.getDefaultedInt;

public record BedwarsShopSlot (List<Identifier> options) {
    public static final String X_KEY = "x";
    public static final String Y_KEY = "y";
    public static final String OPTIONS_KEY = "options";


    public BedwarsShopSlot(JsonObject object) {
        this(deserialise(object));
    }

    private static List<Identifier> deserialise(JsonObject object) {
        final List<Identifier> list = new ArrayList<>();
        object.get(OPTIONS_KEY).getAsJsonArray().forEach(element -> list.add(Identifier.of(element.getAsString()).withSuffixedPath(".json")));

        return list;
    }

    public static void deserialiseAndAddSlot(BedwarsShopSlot[][] list, JsonObject object) {
        final int x = getDefaultedInt(object, X_KEY, -1);
        final int y = getDefaultedInt(object, Y_KEY, -1);

        if (x >= list.length || x < 0 || y > list[x].length || y < 0) {
            SocWars.LOGGER.warn("Failed to load list Bedwars shop slot due to invalid location");
            return;
        }

        if (list[x][y] != null) SocWars.LOGGER.warn("Duplicate Bedwars shop slot at position ({}, {})", x, y);

        list[x][y] = new BedwarsShopSlot(object);
    }
}
