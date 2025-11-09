package com.soc.resourcedata.containers;

import com.soc.resourcedata.CachedData;
import com.soc.resourcedata.IslandGeneratorUpgrade;

import java.util.ArrayList;
import java.util.List;

public class BedwarsData implements CachedData {
    private final List<IslandGeneratorUpgrade> islandGeneratorUpgrades = new ArrayList<>();

    public void addGeneratorUpgrade(IslandGeneratorUpgrade upgrade) {
        this.islandGeneratorUpgrades.add(upgrade);
    }

    public List<IslandGeneratorUpgrade> getGeneratorUpgrades() {
        return this.islandGeneratorUpgrades;
    }

    @Override
    public void cache() {

    }

    @Override
    public void clear() {

    }
}
