package com.soc.resourcedata;

import net.minecraft.item.Item;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class ItemDataContainer<T extends ItemDataAccess<T>> implements CachedData {
    protected HashMap<Integer, HashMap<Item, T>> itemDataPools = new HashMap<>();

    @Override
    public void clear() {
        this.itemDataPools.clear();
    }

    public void acceptData(T data, Integer pool, Item key) {
        data.acceptFunction(pool, key).accept(this.itemDataPools);
    }

    public Optional<HashMap<Item, T>> getPool(Integer pool) {
        return Optional.ofNullable(this.itemDataPools.get(pool));
    }

    public Map<Integer, T> getPoolsForKey(Item key) {
        return this.itemDataPools
                .entrySet()
                .stream()
                .map(entry -> Pair.of(entry.getKey(), entry.getValue().get(key)))
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(Pair::getLeft, Pair::getRight)
        );
    }

    @Override
    public void cache() {}
}
