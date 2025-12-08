package com.soc.resourcedata.containers;

import net.minecraft.item.Item;

import java.util.HashMap;
import java.util.function.Consumer;

public interface ItemDataAccess<T extends ItemDataAccess<T>> {
    Consumer<HashMap<Integer, HashMap<Item, T>>> acceptFunction(Integer pool, Item key);
}
