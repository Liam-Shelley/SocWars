package com.soc.resourcedata.listeners;
import com.soc.SocWars;
import com.soc.game.manager.bedwars.SimpleShopItem;
import com.soc.game.manager.bedwars.TeamShopItem;
import com.soc.game.manager.bedwars.UpgradeableShopItem;
import com.soc.resourcedata.containers.BedwarsShopDataContainer;
import com.soc.resourcedata.deserialisation.PreSelectionBedwarsShopCategory;
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
        BedwarsShopDataContainer.INSTANCE.clear();

        readResources(manager, "bedwars_shop_data/shops", BASE_PATH_PREDICATE, (reader, id) -> {
            switch(id.toString()) {
                case "socwars:bedwars_shop_data/shops/team.json" -> {

                }
                default -> BedwarsShopDataContainer.INSTANCE.addCategorySlot(id, new PreSelectionBedwarsShopCategory(reader));
            }
        });
        readResources(manager, "bedwars_shop_data/simple_items", BASE_PATH_PREDICATE, (reader, id) -> BedwarsShopDataContainer.INSTANCE.addSlotResource(id, new SimpleShopItem(reader)));
        readResources(manager, "bedwars_shop_data/upgradeable_items", BASE_PATH_PREDICATE, (reader, id) -> BedwarsShopDataContainer.INSTANCE.addSlotResource(id, new UpgradeableShopItem(reader)));
        readResources(manager, "bedwars_shop_data/team_items", BASE_PATH_PREDICATE, (reader, id) -> BedwarsShopDataContainer.INSTANCE.addSlotResource(id, new TeamShopItem(reader)));

        BedwarsShopDataContainer.INSTANCE.cache();
    }
}
