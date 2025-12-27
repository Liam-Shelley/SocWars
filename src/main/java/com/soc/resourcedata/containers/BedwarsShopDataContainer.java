package com.soc.resourcedata.containers;

import com.soc.game.manager.bedwars.BaseShopItem;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.Map;

public class BedwarsShopDataContainer implements CachedData {

    private BedwarsShopDataContainer() {}

    public static final BedwarsShopDataContainer INSTANCE = new BedwarsShopDataContainer();

    private final Map<Identifier, ? extends BaseShopItem> resourceItemMap = new HashMap<>();

    @Override
    public void cache() {

    }

    @Override
    public void clear() {
        this.resourceItemMap.clear();
    }
}
