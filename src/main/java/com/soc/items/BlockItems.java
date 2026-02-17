package com.soc.items;

import com.soc.game.map.DyeColourWithEmpty;
import com.soc.items.util.ColourStateBlockItem;
import com.soc.items.util.ModItems;
import com.soc.items.util.TierBlockItem;
import net.minecraft.item.Item;

import static com.soc.blocks.util.ModBlocks.CHEST_PLACEHOLDER;
import static com.soc.blocks.util.ModBlocks.MANY_BEDS_BLOCK;
import static com.soc.items.util.ItemGroups.addItemToBlocksGroup;
import static com.soc.items.util.ItemGroups.addItemToMapMakingGroup;

public class BlockItems {
    public static final Item DEPOSITABLE_CHEST = ModItems.register("depositable_chest", DepositableChestBlockItem::new, new Item.Settings());
    public static final Item DEPOSITABLE_ENDER_CHEST = ModItems.register("depositable_ender_chest", DepositableEnderChestBlockItem::new, new Item.Settings());

    public static void initialise() {
        addItemToMapMakingGroup(DEPOSITABLE_CHEST);
        addItemToMapMakingGroup(DEPOSITABLE_ENDER_CHEST);

        for (int i = 0; i < 4; i++) {
            int finalI = i;
            addItemToMapMakingGroup(ModItems.register("chest_placeholder_tier_" + i, settings -> new TierBlockItem(CHEST_PLACEHOLDER, settings, finalI), new Item.Settings()));
        }

        //This tickles me the right way
        for (DyeColourWithEmpty value : DyeColourWithEmpty.values()) {
            if (value.isPresent()) addItemToBlocksGroup(ModItems.register("many_beds_block_" + value.asString(), settings -> new ColourStateBlockItem(MANY_BEDS_BLOCK, settings, value), new Item.Settings()));
        }
    }
}
