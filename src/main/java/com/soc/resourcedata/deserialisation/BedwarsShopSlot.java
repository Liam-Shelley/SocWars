package com.soc.resourcedata.deserialisation;

import com.google.gson.JsonObject;
import net.minecraft.util.Identifier;

import java.util.List;

public record BedwarsShopSlot (List<Identifier> a) {
    public BedwarsShopSlot(JsonObject object) {
        this(List.of() );
    }
}
