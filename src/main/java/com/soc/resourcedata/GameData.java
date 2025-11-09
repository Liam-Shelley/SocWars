package com.soc.resourcedata;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.soc.SocWars;
import com.soc.resourcedata.containers.BedwarsData;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.io.BufferedReader;
import java.io.Reader;

public class GameData implements SimpleSynchronousResourceReloadListener {
    //Maybe change to a Path -> Consumer<Reader> Map to control how each resource is loaded

    private final BedwarsData bedwarsData = new BedwarsData();
    public BedwarsData bedwarsData() { return this.bedwarsData; }

    @Override
    public Identifier getFabricId() {
        return Identifier.of(SocWars.MOD_ID, "game_data_resources");
    }

    @Override
    public void reload(ResourceManager manager) {
        this.bedwarsData.clear();

        for(Identifier id : manager.findResources("game_data", path -> path.toString().endsWith(".json")).keySet()) {
            try(BufferedReader reader = manager.getResource(id).get().getReader()) {
                switch (id.getPath()) {
                    case "game_data/island_generator_upgrades.json" -> this.loadIslandGenUpgrades(reader);
                }
            } catch(Exception e) {
                SocWars.LOGGER.error("Error occurred while loading resource json {}:\n{}", id.toString(), e);
            }
        }

        this.bedwarsData.cache();
    }

    private void loadIslandGenUpgrades(Reader reader) {
        for (JsonElement jsonElement : JsonHelper.deserializeArray(reader)) {
            final JsonObject object = jsonElement.getAsJsonObject();
            this.bedwarsData.addGeneratorUpgrade(new IslandGeneratorUpgrade(object));
        }
    }
}
