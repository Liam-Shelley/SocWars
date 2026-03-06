package com.soc.resourcedata.listeners;
import com.soc.SocWars;
import com.soc.game.manager.bedwars.shopitems.*;
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
                case "socwars:bedwars_shop_data/shops/traps.json" -> BedwarsShopDataContainer.INSTANCE.setTeamStockCategory(0, new PreSelectionBedwarsShopCategory(reader));
                case "socwars:bedwars_shop_data/shops/abilities.json" -> BedwarsShopDataContainer.INSTANCE.setTeamStockCategory(1, new PreSelectionBedwarsShopCategory(reader));
                default -> BedwarsShopDataContainer.INSTANCE.addCategorySlot(id, new PreSelectionBedwarsShopCategory(reader));
            }
        });
        readResources(manager, "bedwars_shop_data/simple_items", BASE_PATH_PREDICATE, (reader, id) -> BedwarsShopDataContainer.INSTANCE.addSlotResource(id, new SimpleShopItem(reader)));
        readResources(manager, "bedwars_shop_data/upgradeable_items", BASE_PATH_PREDICATE, (reader, id) -> BedwarsShopDataContainer.INSTANCE.addSlotResource(id, new UpgradeableShopItem(reader)));
        readResources(manager, "bedwars_shop_data/team_items", BASE_PATH_PREDICATE, (reader, id) -> BedwarsShopDataContainer.INSTANCE.addSlotResource(id, new TeamShopItem(reader)));
        readResources(manager, "bedwars_shop_data/trap_items", BASE_PATH_PREDICATE, (reader, id) -> BedwarsShopDataContainer.INSTANCE.addSlotResource(id, new TrapShopItem(reader)));
        readResources(manager, "bedwars_shop_data/enchantment_upgrade_items", BASE_PATH_PREDICATE, (reader, id) -> BedwarsShopDataContainer.INSTANCE.addSlotResource(id, new EnchantmentUpgradeShopItem(reader)));

        BedwarsShopDataContainer.INSTANCE.cache();
    }
}
