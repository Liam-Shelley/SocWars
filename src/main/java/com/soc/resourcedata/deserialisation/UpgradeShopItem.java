package com.soc.resourcedata.deserialisation;

import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

import static com.soc.lib.json.JsonHelper.getDefaultedObject;

public record UpgradeShopItem(Cost cost, Identifier upgradeableItem) {
    public static final String COST_KEY = "cost";
    public static final String UPGRADEABLE_ITEM_KEY = "upgradeable_item";

    public UpgradeShopItem(JsonObject object) {
        this(
                getDefaultedObject(object, COST_KEY, Cost::new, Cost.ERROR_SIGNAL),
                getDefaultedObject(object, UPGRADEABLE_ITEM_KEY, path -> Identifier.of(path.getAsString()), Identifier.of("default"))
        );
    }
}
