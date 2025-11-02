package com.soc.resourcedata;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.soc.SocWars;
import com.soc.resourcedata.containers.SkywarsData;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.BufferedReader;

import static com.soc.resourcedata.SkywarsItemData.POOL_KEY;

public class ItemData implements SimpleSynchronousResourceReloadListener {
    public static final String ITEM_ID_KEY = "id";

    private final SkywarsData skywarsItemDataContainer = new SkywarsData();
    public SkywarsData getSkywarsItemData() { return this.skywarsItemDataContainer; }

    @Override
    public Identifier getFabricId() {
        return Identifier.of(SocWars.MOD_ID, "item_data_resources");
    }

    @Override
    public void reload(ResourceManager manager) {
        this.skywarsItemDataContainer.clear();

        for(Identifier id : manager.findResources("item_data", path -> path.toString().endsWith(".json")).keySet()) {
            try(BufferedReader reader = manager.getResource(id).get().getReader()) {
                for (JsonElement jsonElement : JsonHelper.deserializeArray(reader)) {
                    final JsonObject object = jsonElement.getAsJsonObject();
                    final Item item = Registries.ITEM.get(Identifier.of(object.get(ITEM_ID_KEY).getAsString()));
                    final Integer pool = object.has(POOL_KEY) ? object.get(POOL_KEY).getAsInt() : 0;

                    if (item == Items.AIR) SocWars.LOGGER.warn("Skipped loading weights for {} as there is no corresponding registered item", object.get(ITEM_ID_KEY).getAsString());

                    final SkywarsItemData skywarsItemData = new SkywarsItemData(jsonElement.getAsJsonObject());

                    this.skywarsItemDataContainer.acceptData(skywarsItemData, pool, item);
                }
            } catch(Exception e) {
                SocWars.LOGGER.error("Error occurred while loading resource json {}:\n{}", id.toString(), e);
            }
        }

        this.skywarsItemDataContainer.cache();
    }
}
