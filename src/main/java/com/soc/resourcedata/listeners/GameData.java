package com.soc.resourcedata.listeners;

import com.soc.SocWars;
import com.soc.lib.json.JsonHelper;
import com.soc.resourcedata.deserialisation.IslandGeneratorUpgrade;
import com.soc.resourcedata.containers.BedwarsData;
import com.soc.resourcedata.deserialisation.ResourceGeneratorUpgrade;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.BufferedReader;

public class GameData implements SimpleSynchronousResourceReloadListener {
    public static final GameData INSTANCE = new GameData();

    //Maybe change to a Path -> Consumer<Reader> Map to control how each resource is loaded

    private final BedwarsData bedwarsData = new BedwarsData();
    public BedwarsData getBedwarsData() { return this.bedwarsData; }

    private GameData() {}

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
                    case "game_data/island_generator_upgrades.json" -> JsonHelper.runFunctionOverArray(reader, object -> this.bedwarsData.addIslandGeneratorUpgrade(new IslandGeneratorUpgrade(object)));
                    case "game_data/diamond_generator_upgrades.json" -> JsonHelper.runFunctionOverArray(reader, object -> this.bedwarsData.addDiamondGeneratorUpgrade(new ResourceGeneratorUpgrade(object)));
                    case "game_data/emerald_generator_upgrades.json" -> JsonHelper.runFunctionOverArray(reader, object -> this.bedwarsData.addEmeraldGeneratorUpgrade(new ResourceGeneratorUpgrade(object)));
                }
            } catch(Exception e) {
                SocWars.LOGGER.error("Error occurred while loading resource json {}:\n{}", id.toString(), e);
            }
        }

        this.bedwarsData.cache();
    }
}
