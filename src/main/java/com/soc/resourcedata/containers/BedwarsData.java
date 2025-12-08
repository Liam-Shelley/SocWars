package com.soc.resourcedata.containers;

import com.soc.resourcedata.deserialisation.IslandGeneratorUpgrade;
import com.soc.resourcedata.deserialisation.ResourceGeneratorUpgrade;

import java.util.ArrayList;
import java.util.List;

public class BedwarsData implements CachedData {
    private final List<IslandGeneratorUpgrade> islandGeneratorUpgrades = new ArrayList<>();
    private final List<ResourceGeneratorUpgrade> diamondGeneratorUpgrades = new ArrayList<>();
    private final List<ResourceGeneratorUpgrade> emeraldGeneratorUpgrades = new ArrayList<>();

    public void addIslandGeneratorUpgrade(IslandGeneratorUpgrade upgrade) {
        this.islandGeneratorUpgrades.add(upgrade);
    }

    public void addDiamondGeneratorUpgrade(ResourceGeneratorUpgrade upgrade) {
        this.diamondGeneratorUpgrades.add(upgrade);
    }

    public void addEmeraldGeneratorUpgrade(ResourceGeneratorUpgrade upgrade) {
        this.emeraldGeneratorUpgrades.add(upgrade);
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
