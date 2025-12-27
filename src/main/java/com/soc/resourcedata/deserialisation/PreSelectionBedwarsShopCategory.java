package com.soc.resourcedata.deserialisation;

import com.google.gson.JsonObject;
import com.soc.lib.json.JsonHelper;
import com.soc.screenhandler.BedwarsShopScreenHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

import java.io.Reader;
import java.io.StringReader;

import static net.minecraft.util.JsonHelper.deserialize;
import static net.minecraft.util.JsonHelper.deserializeArray;

public record PreSelectionBedwarsShopCategory(Text name, ItemStack icon, BedwarsShopSlot[][] contents) {
    public static final String NAME_KEY = "name";
    public static final String ICON_KEY = "icon";
    public static final String CONTENTS_KEY = "contents";

    public PreSelectionBedwarsShopCategory(JsonObject object) {
        this(
                Text.translatable(object.get(NAME_KEY).getAsString()),
                JsonHelper.getDefaultedItem(object, ICON_KEY, ItemStack.EMPTY),
                deserialiseSlots(new StringReader(object.get(CONTENTS_KEY).getAsString()))
        );
    }

    public PreSelectionBedwarsShopCategory(Reader reader) {
        this(deserialize(reader));
    }

    private static BedwarsShopSlot[][] deserialiseSlots(Reader reader) {
        final BedwarsShopSlot[][] list = new BedwarsShopSlot[BedwarsShopScreenHandler.STOCK_HEIGHT][BedwarsShopScreenHandler.STOCK_WIDTH];
        deserializeArray(reader).forEach(element -> BedwarsShopSlot.deserialiseAndAddSlot(list, element.getAsJsonObject()));



        return null;
    }
}
