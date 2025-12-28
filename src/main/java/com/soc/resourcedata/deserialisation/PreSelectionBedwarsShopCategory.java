package com.soc.resourcedata.deserialisation;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.soc.lib.json.JsonHelper;
import com.soc.screenhandler.BedwarsShopScreenHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.io.Reader;

import static net.minecraft.util.JsonHelper.deserialize;

public record PreSelectionBedwarsShopCategory(Text name, ItemStack icon, int pagePriority, BedwarsShopSlot[][] contents) implements Comparable<PreSelectionBedwarsShopCategory> {
    public static final String NAME_KEY = "name";
    public static final String ICON_KEY = "icon";
    public static final String PAGE_PRIORITY_KEY = "page_priority";
    public static final String CONTENTS_KEY = "contents";

    public PreSelectionBedwarsShopCategory(JsonObject object) {
        this(
                Text.translatable(object.get(NAME_KEY).getAsString()),
                JsonHelper.getDefaultedItem(object, ICON_KEY, ItemStack.EMPTY),
                JsonHelper.getDefaultedInt(object, PAGE_PRIORITY_KEY, 100),
                deserialiseSlots(object.get(CONTENTS_KEY).getAsJsonArray())
        );
    }

    public PreSelectionBedwarsShopCategory(Reader reader) {
        this(deserialize(reader));
    }

    private static BedwarsShopSlot[][] deserialiseSlots(JsonArray array) {
        final BedwarsShopSlot[][] list = new BedwarsShopSlot[BedwarsShopScreenHandler.STOCK_WIDTH][BedwarsShopScreenHandler.STOCK_HEIGHT];
        array.forEach(element -> BedwarsShopSlot.deserialiseAndAddSlot(list, element.getAsJsonObject()));

        return list;
    }

    @Override
    public int compareTo(@NotNull PreSelectionBedwarsShopCategory o) {
        return Integer.compare(this.pagePriority, o.pagePriority);
    }
}
