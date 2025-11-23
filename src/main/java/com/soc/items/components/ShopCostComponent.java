package com.soc.items.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;

public record ShopCostComponent(int iron, int gold, int diamonds, int emeralds) {
    public static final Codec<ShopCostComponent> CODEC = RecordCodecBuilder.create(builder -> builder.group(
            Codec.INT.fieldOf("iron").forGetter(ShopCostComponent::iron),
            Codec.INT.fieldOf("gold").forGetter(ShopCostComponent::gold),
            Codec.INT.fieldOf("diamonds").forGetter(ShopCostComponent::diamonds),
            Codec.INT.fieldOf("emeralds").forGetter(ShopCostComponent::emeralds)
    ).apply(builder, ShopCostComponent::new));

    private Map<Item, Integer> getCostMap() {
        return Map.of(
                Items.IRON_INGOT, this.iron,
                Items.GOLD_INGOT, this.gold,
                Items.DIAMOND, this.diamonds,
                Items.EMERALD, this.emeralds
        );
    }

    public int getCost(Item item) {
        return this.getCostMap().getOrDefault(item, 0);
    }

    public List<Pair<Item, Integer>> getCosts() {
        return List.of(
                Pair.of(Items.IRON_INGOT, this.iron),
                Pair.of(Items.GOLD_INGOT, this.gold),
                Pair.of(Items.DIAMOND, this.diamonds),
                Pair.of(Items.EMERALD, this.emeralds)
        );
    }
}
