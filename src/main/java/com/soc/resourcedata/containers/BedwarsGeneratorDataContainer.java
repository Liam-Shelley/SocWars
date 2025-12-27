package com.soc.resourcedata.containers;

import com.google.gson.JsonObject;
import com.soc.lib.SortedList;
import com.soc.resourcedata.deserialisation.IslandGeneratorUpgrade;
import com.soc.resourcedata.deserialisation.ResourceGeneratorUpgrade;

import java.util.List;

public class BedwarsGeneratorDataContainer implements CachedData {
    private BedwarsGeneratorDataContainer() {}

    public static final BedwarsGeneratorDataContainer INSTANCE = new BedwarsGeneratorDataContainer();

    private final SortedList<IslandGeneratorUpgrade> islandGeneratorUpgrades = new SortedList<>();
    private final SortedList<ResourceGeneratorUpgrade> diamondGeneratorUpgrades = new SortedList<>();
    private final SortedList<ResourceGeneratorUpgrade> emeraldGeneratorUpgrades = new SortedList<>();

    public void addIslandGeneratorUpgrade(IslandGeneratorUpgrade upgrade) {
        this.islandGeneratorUpgrades.add(upgrade);
    }

    public void addDiamondGeneratorUpgrade(ResourceGeneratorUpgrade upgrade) {
        this.diamondGeneratorUpgrades.add(upgrade);
    }

    public void addEmeraldGeneratorUpgrade(ResourceGeneratorUpgrade upgrade) {
        this.emeraldGeneratorUpgrades.add(upgrade);
    }

    public void addIslandGeneratorUpgrade(JsonObject object) {
        this.addIslandGeneratorUpgrade(new IslandGeneratorUpgrade(object));
    }

    public void addDiamondGeneratorUpgrade(JsonObject object) {
        this.addDiamondGeneratorUpgrade(new ResourceGeneratorUpgrade(object));
    }

    public void addEmeraldGeneratorUpgrade(JsonObject object) {
        this.addEmeraldGeneratorUpgrade(new ResourceGeneratorUpgrade(object));
    }

    public List<IslandGeneratorUpgrade> getIslandGeneratorUpgrades() {
        return this.islandGeneratorUpgrades;
    }

    public List<ResourceGeneratorUpgrade> getDiamondGeneratorUpgrades() {
        return this.diamondGeneratorUpgrades;
    }

    public List<ResourceGeneratorUpgrade> getEmeraldGeneratorUpgrades() {
        return this.emeraldGeneratorUpgrades;
    }

    @Override
    public void cache() {

    }

    @Override
    public void clear() {
        this.islandGeneratorUpgrades.clear();
        this.diamondGeneratorUpgrades.clear();
        this.emeraldGeneratorUpgrades.clear();
    }
}
