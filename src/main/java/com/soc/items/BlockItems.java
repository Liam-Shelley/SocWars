package com.soc.items;

import com.soc.game.map.DyeColourWithEmpty;
import com.soc.items.util.ColourStateBlockItem;
import com.soc.items.util.TierBlockItem;
import com.soc.itemsrework.ModItemsReworked;
import net.minecraft.item.Item;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.soc.blocks.util.ModBlocks.CHEST_PLACEHOLDER;
import static com.soc.blocks.util.ModBlocks.MANY_BEDS_BLOCK;
import static com.soc.items.util.ModItemGroups.addItemToBlocksGroup;
import static com.soc.items.util.ModItemGroups.addItemToMapMakingGroup;
import static com.soc.lib.SocWarsLib.mapFromRange;

public class BlockItems {
    public static final Item DEPOSITABLE_CHEST = ModItemsReworked.register("depositable_chest", DepositableChestBlockItem::new, new Item.Settings());
    public static final Item DEPOSITABLE_ENDER_CHEST = ModItemsReworked.register("depositable_ender_chest", DepositableEnderChestBlockItem::new, new Item.Settings());

    public static final List<Item> CHEST_PLACEHOLDERS = mapFromRange(0, 4, i -> addItemToMapMakingGroup(ModItemsReworked.register("chest_placeholder_tier_" + i, settings -> new TierBlockItem(CHEST_PLACEHOLDER, settings, i), new Item.Settings())));

    public static final Map<DyeColourWithEmpty, Item> MANY_BEDS_BLOCKS = Arrays.stream(DyeColourWithEmpty.values()).filter(DyeColourWithEmpty::isPresent).collect(Collectors.toMap(Function.identity(), colour -> addItemToBlocksGroup(ModItemsReworked.register("many_beds_block_" + colour.asString(), settings -> new ColourStateBlockItem(MANY_BEDS_BLOCK, settings, colour), new Item.Settings()))));

    public static void initialise() {
        addItemToMapMakingGroup(DEPOSITABLE_CHEST);
        addItemToMapMakingGroup(DEPOSITABLE_ENDER_CHEST);
    }
}
