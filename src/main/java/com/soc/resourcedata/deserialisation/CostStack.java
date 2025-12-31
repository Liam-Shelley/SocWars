package com.soc.resourcedata.deserialisation;

import com.google.gson.JsonObject;
import net.minecraft.item.ItemStack;

import static com.soc.lib.json.JsonHelper.*;

public record CostStack(Cost cost, ItemStack stack) {
    public CostStack(JsonObject object) {
        this(
                getDefaultedObject(object, Cost.KEY, Cost::new, Cost.ERROR_SIGNAL),
                getDefaultedItem(object, ItemStack.EMPTY)
        );
    }
}
