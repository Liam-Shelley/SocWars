package com.soc.game.map;

import java.util.function.BiConsumer;

public record RangedIntField(String name, int minValue, int maxValue, int def, BiConsumer<AbstractGameMap, Integer> applicator) {
    public void apply(AbstractGameMap map, int value) {
        applicator.accept(map, value);
    }
}
