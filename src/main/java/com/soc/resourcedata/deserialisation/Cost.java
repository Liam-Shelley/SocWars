package com.soc.resourcedata.deserialisation;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import org.apache.commons.lang3.function.TriConsumer;

import java.util.Map;
import java.util.function.BiConsumer;

import static com.soc.lib.json.JsonHelper.getDefaultedInt;

public record Cost(int iron, int gold, int diamonds, int emeralds) {
    public static final PacketCodec<RegistryByteBuf, Cost> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.INTEGER, Cost::iron, PacketCodecs.INTEGER, Cost::gold, PacketCodecs.INTEGER, Cost::diamonds, PacketCodecs.INTEGER, Cost::emeralds, Cost::new);

    public static final Cost DEFAULT = new Cost(0, 0, 0, 0);
    public static final Cost ERROR_SIGNAL = new Cost(999, 999, 999, 999);

    public static final String KEY = "cost";

    private static final String IRON_COUNT_KEY = "iron";
    private static final String GOLD_COUNT_KEY = "gold";
    private static final String DIAMONDS_COUNT_KEY = "diamonds";
    private static final String EMERALDS_COUNT_KEY = "emeralds";

    public Cost(JsonObject object) {
        this(
                getDefaultedInt(object, IRON_COUNT_KEY),
                getDefaultedInt(object, GOLD_COUNT_KEY),
                getDefaultedInt(object, DIAMONDS_COUNT_KEY),
                getDefaultedInt(object, EMERALDS_COUNT_KEY)
        );
    }

    public boolean canAfford(PlayerEntity player) {
        final PlayerInventory inventory = player.getInventory();

        return
                inventory.count(Items.IRON_INGOT) >= this.iron &&
                inventory.count(Items.GOLD_INGOT) >= this.gold &&
                inventory.count(Items.DIAMOND) >= this.diamonds &&
                inventory.count(Items.EMERALD) >= this.emeralds;
    }

    public boolean takeItems(PlayerEntity player, boolean dryRun) {
        final PlayerInventory inventory = player.getInventory();

        return
                Inventories.remove(inventory, predStack -> predStack.isOf(Items.IRON_INGOT), this.iron, dryRun) >= this.iron &
                Inventories.remove(inventory, predStack -> predStack.isOf(Items.GOLD_INGOT), this.gold, dryRun) >= this.iron &
                Inventories.remove(inventory, predStack -> predStack.isOf(Items.DIAMOND), this.diamonds, dryRun) >= this.iron &
                Inventories.remove(inventory, predStack -> predStack.isOf(Items.EMERALD), this.emeralds, dryRun) >= this.iron;
    }

    public Map<Item, Integer> getAsMap() {
        return Map.of(
                Items.IRON_INGOT, this.iron,
                Items.GOLD_INGOT, this.gold,
                Items.DIAMOND, this.diamonds,
                Items.EMERALD, this.emeralds
        );
    }

    public void forEach(BiConsumer<Item, Integer> function) {
        function.accept(Items.IRON_INGOT, this.iron);
        function.accept(Items.GOLD_INGOT, this.gold);
        function.accept(Items.DIAMOND, this.diamonds);
        function.accept(Items.EMERALD, this.emeralds);
    }

    public void forEach(TriConsumer<Item, Integer, Integer> function) {
        function.accept(Items.IRON_INGOT, this.iron, 0);
        function.accept(Items.GOLD_INGOT, this.gold, 1);
        function.accept(Items.DIAMOND, this.diamonds, 2);
        function.accept(Items.EMERALD, this.emeralds, 3);
    }

    public boolean isFree() {
        return
                this.iron == 0 &&
                this.gold == 0 &&
                this.diamonds == 0 &&
                this.emeralds == 0;
    }
}
