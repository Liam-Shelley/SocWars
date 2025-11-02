package com.soc.resourcedata.containers;

import com.soc.lib.CumulativeWeightList;
import com.soc.resourcedata.ItemDataContainer;
import com.soc.resourcedata.SkywarsItemData;
import net.minecraft.item.Item;
import net.minecraft.util.math.random.Random;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SkywarsData extends ItemDataContainer<SkywarsItemData> {
    private Map<Integer, CumulativeWeightList<Item>[]> cumulativeWeightPools = new HashMap<>();

    private Map<Integer, CumulativeWeightList<Item>[]> getCumulativeWeightPools() {
        return super.itemDataPools.keySet().stream().collect(Collectors.toMap(key -> key, this::getCumulativeWeightsForTiers));
    }

    private CumulativeWeightList<Item>[] getCumulativeWeightsForTiers(Integer poolKey) {
        List<Integer> tiers = List.of(0, 1, 2, 3);
        return tiers.stream().map(tier -> getCumulativeWeightsForTier(poolKey, tier)).toArray(CumulativeWeightList[]::new);
    }

    private CumulativeWeightList<Item> getCumulativeWeightsForTier(Integer poolKey, int tier) {
        if (tier < 0 || tier > 3) return null;
        return new CumulativeWeightList<>(super.itemDataPools.get(poolKey), tier);
    }

    public Item getRandomItem(Integer poolKey, int tier, Random random) throws IllegalStateException {
        final CumulativeWeightList<Item>[] pool = this.cumulativeWeightPools.get(poolKey);
        if (pool == null) throw new IllegalStateException("No pool found for " + poolKey);

        try {
            return pool[tier].getWeightedRandom(random);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalStateException("No tier found for " + tier);
        }
    }

    @Override
    public void cache() {
        this.cumulativeWeightPools = this.getCumulativeWeightPools();
        var a = this.cumulativeWeightPools;
    }

    @Override
    public void clear() {
        super.clear();
        this.cumulativeWeightPools.clear();
    }
}
