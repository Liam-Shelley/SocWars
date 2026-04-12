package com.soc.resourcedata.listeners;

import com.soc.SocWars;
import com.soc.game.manager.bedwars.shopitems.*;
import com.soc.resourcedata.containers.BedwarsShopDataContainer;
import com.soc.resourcedata.deserialisation.PreSelectionBedwarsShopCategory;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.io.Reader;
import java.util.Map;
import java.util.function.Function;

import static com.soc.resourcedata.ResourceManager.*;

public class BedwarsShopData implements SimpleSynchronousResourceReloadListener {
    public static final BedwarsShopData INSTANCE = new BedwarsShopData();

    private static final Map<String, Function<Reader, ? extends ShopItem<?>>> SHOP_ITEM_DESEREALISER_MAP = Map.of(
            "bedwars_shop_data/simple_items", SimpleShopItem::new,
            "bedwars_shop_data/upgradeable_items", UpgradeableShopItem::new,
            "bedwars_shop_data/team_items", TeamShopItem::new,
            "bedwars_shop_data/trap_items", TrapShopItem::new,
            "bedwars_shop_data/ability_items", AbilityShopItem::new,
            "bedwars_shop_data/enchantment_upgrade_items", EnchantmentUpgradeShopItem::new,
            "bedwars_shop_data/tick_function_upgrade_items", TickFunctionUpgradeShopItem::new,
            "bedwars_shop_data/generator_upgrade_items", GeneratorUpgradeShopItem::new
    );

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
                case "socwars:bedwars_shop_data/shops/upgrades.json" -> BedwarsShopDataContainer.INSTANCE.setTeamStockCategory(2, new PreSelectionBedwarsShopCategory(reader));
                default -> BedwarsShopDataContainer.INSTANCE.addCategorySlot(id, new PreSelectionBedwarsShopCategory(reader));
            }
        });

        SHOP_ITEM_DESEREALISER_MAP.forEach((path, ctor) -> {
            readResources(manager, path, BASE_PATH_PREDICATE, (reader, id) -> BedwarsShopDataContainer.INSTANCE.addSlotResource(id, ctor.apply(reader)));
        });

        BedwarsShopDataContainer.INSTANCE.cache();
    }
}
