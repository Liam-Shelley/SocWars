package com.soc.items;

import com.soc.game.map.DyeColourWithEmpty;
import com.soc.items.util.ColourStateBlockItem;
import com.soc.items.util.ModItems;
import com.soc.items.util.TierBlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.soc.blocks.util.ModBlocks.CHEST_PLACEHOLDER;
import static com.soc.blocks.util.ModBlocks.MANY_BEDS_BLOCK;
import static com.soc.items.util.ItemGroups.addItemToBlocksGroup;
import static com.soc.items.util.ItemGroups.addItemToMapMakingGroup;
import static com.soc.lib.SocWarsLib.mapFromRange;

public class BlockItems {
    public static final Item DEPOSITABLE_CHEST = ModItems.register("depositable_chest", DepositableChestBlockItem::new, new Item.Settings());
    public static final Item DEPOSITABLE_ENDER_CHEST = ModItems.register("depositable_ender_chest", DepositableEnderChestBlockItem::new, new Item.Settings());

    public static final List<Item> CHEST_PLACEHOLDERS = mapFromRange(0, 4, i -> addItemToMapMakingGroup(ModItems.register("chest_placeholder_tier_" + i, settings -> new TierBlockItem(CHEST_PLACEHOLDER, settings, i), new Item.Settings())));

    public static final Map<DyeColourWithEmpty, Item> MANY_BEDS_BLOCKS = Arrays.stream(DyeColourWithEmpty.values()).filter(DyeColourWithEmpty::isPresent).collect(Collectors.toMap(Function.identity(), colour -> addItemToBlocksGroup(ModItems.register("many_beds_block_" + colour.asString(), settings -> new ColourStateBlockItem(MANY_BEDS_BLOCK, settings, colour), new Item.Settings()))));

    public static void initialise() {
        addItemToMapMakingGroup(DEPOSITABLE_CHEST);
        addItemToMapMakingGroup(DEPOSITABLE_ENDER_CHEST);
    }
}
