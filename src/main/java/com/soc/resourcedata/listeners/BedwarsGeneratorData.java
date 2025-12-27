package com.soc.resourcedata.listeners;

import com.soc.SocWars;
import com.soc.lib.json.JsonHelper;
import com.soc.resourcedata.containers.BedwarsGeneratorDataContainer;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import static com.soc.resourcedata.ResourceManager.BASE_PATH_PREDICATE;
import static com.soc.resourcedata.ResourceManager.readResources;

public class BedwarsGeneratorData implements SimpleSynchronousResourceReloadListener {
    public static final BedwarsGeneratorData INSTANCE = new BedwarsGeneratorData();

    //Maybe change to options Path -> Consumer<Reader> Map to control how each resource is loaded

    private BedwarsGeneratorData() {}

    @Override
    public Identifier getFabricId() {
        return Identifier.of(SocWars.MOD_ID, "bedwars_generator_resources");
    }

    @Override
    public void reload(ResourceManager manager) {
        BedwarsGeneratorDataContainer.INSTANCE.clear();

        readResources(manager, "bedwars_generators", BASE_PATH_PREDICATE, (reader, id) -> {
            switch (id.getPath()) {
                case "game_data/island_generator_upgrades.json" -> JsonHelper.runFunctionOverArray(reader, BedwarsGeneratorDataContainer.INSTANCE::addIslandGeneratorUpgrade);
                case "game_data/diamond_generator_upgrades.json" -> JsonHelper.runFunctionOverArray(reader, BedwarsGeneratorDataContainer.INSTANCE::addDiamondGeneratorUpgrade);
                case "game_data/emerald_generator_upgrades.json" -> JsonHelper.runFunctionOverArray(reader, BedwarsGeneratorDataContainer.INSTANCE::addEmeraldGeneratorUpgrade);
            }
        });

        BedwarsGeneratorDataContainer.INSTANCE.cache();
    }
}
