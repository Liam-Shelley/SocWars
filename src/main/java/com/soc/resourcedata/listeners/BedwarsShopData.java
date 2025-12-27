package com.soc.resourcedata.listeners;

import com.soc.SocWars;
import com.soc.resourcedata.containers.BedwarsGeneratorDataContainer;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import static com.soc.resourcedata.ResourceManager.*;

public class BedwarsShopData implements SimpleSynchronousResourceReloadListener {
    public static final BedwarsShopData INSTANCE = new BedwarsShopData();

    private BedwarsShopData() {}

    @Override
    public Identifier getFabricId() {
        return Identifier.of(SocWars.MOD_ID, "bedwars_shop_data");
    }

    @Override
    public void reload(ResourceManager manager) {
        BedwarsGeneratorDataContainer.INSTANCE.clear();

        readResources(manager, "bedwars_shop_data/shops", endsWithStringPredicate("team.json", "individual.json"), (reader, id) -> {
            switch(id.toString()) {
                case "socwars:bedwars_shop_data/shops/team.json" -> SocWars.LOGGER.info("team.json found");
                case "socwars:bedwars_shop_data/shops/individual.json" -> SocWars.LOGGER.info("individual.json found");
                case "socwars:bedwars_shop_data/shops/a.json" -> SocWars.LOGGER.info("a.json should not have been found");
            }
        });
        readResources(manager, "bedwars_shop_data/simple_items", BASE_PATH_PREDICATE, (reader, id) -> {

        });
        readResources(manager, "bedwars_shop_data/upgradeable_items", BASE_PATH_PREDICATE, (reader, id) -> {

        });

        BedwarsGeneratorDataContainer.INSTANCE.cache();
    }
}
