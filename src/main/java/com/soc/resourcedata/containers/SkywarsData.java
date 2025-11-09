package com.soc.resourcedata.containers;

import com.soc.lib.CumulativeWeightList;
import com.soc.resourcedata.ItemDataContainer;
import com.soc.resourcedata.SkywarsItemData;
import net.minecraft.item.Item;
import net.minecraft.util.math.random.Random;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SkywarsData extends ItemDataContainer<SkywarsItemData> {
    private Map<Integer, CumulativeWeightList<Pair<Item, Integer>>[]> cumulativeWeightPools = new HashMap<>();

    private Map<Integer, CumulativeWeightList<Pair<Item, Integer>>[]> getCumulativeWeightPools() {
        return super.itemDataPools.keySet().stream().collect(Collectors.toMap(key -> key, this::getCumulativeWeightsForTiers));
    }

    private CumulativeWeightList<Pair<Item, Integer>>[] getCumulativeWeightsForTiers(Integer poolKey) {
        List<Integer> tiers = List.of(0, 1, 2, 3);
        return tiers.stream().map(tier -> this.getCumulativeWeightsForTier(poolKey, tier)).toArray(CumulativeWeightList[]::new);
    }

    private CumulativeWeightList<Pair<Item, Integer>> getCumulativeWeightsForTier(Integer poolKey, int tier) {
        if (tier < 0 || tier > 3) return null;
        return new CumulativeWeightList<>(super.itemDataPools.get(poolKey), tier);
    }

    public Pair<Item, Integer> getRandomItem(Integer poolKey, int tier, Random random) throws IllegalStateException {
        final CumulativeWeightList<Pair<Item, Integer>>[] pool = this.cumulativeWeightPools.get(poolKey);
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
    }

    @Override
    public void clear() {
        super.clear();
        this.cumulativeWeightPools.clear();
    }
}
