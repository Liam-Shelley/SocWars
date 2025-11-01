package com.soc.items;

import com.soc.items.util.ModItems;
import com.soc.items.util.TierBlockItem;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;

import static com.soc.blocks.util.ModBlocks.CHEST_PLACEHOLDER;
import static com.soc.items.util.ModItems.addItemToGroups;

public class BlockItems {
    public static final Item CHEST_PLACEHOLDER_TIER_1 = ModItems.register("chest_placeholder_tier_1", settings -> new TierBlockItem(CHEST_PLACEHOLDER, settings, 1), new Item.Settings());
    public static final Item CHEST_PLACEHOLDER_TIER_2 = ModItems.register("chest_placeholder_tier_2", settings -> new TierBlockItem(CHEST_PLACEHOLDER, settings, 2), new Item.Settings());
    public static final Item CHEST_PLACEHOLDER_TIER_3 = ModItems.register("chest_placeholder_tier_3", settings -> new TierBlockItem(CHEST_PLACEHOLDER, settings, 3), new Item.Settings());
    public static final Item CHEST_PLACEHOLDER_TIER_4 = ModItems.register("chest_placeholder_tier_4", settings -> new TierBlockItem(CHEST_PLACEHOLDER, settings, 4), new Item.Settings());

    public static final Item DEPOSITABLE_CHEST = ModItems.register("depositable_chest", DepositableChestBlockItem::new, new Item.Settings());

    public static void initialise() {
        addItemToGroups(CHEST_PLACEHOLDER_TIER_1);
        addItemToGroups(CHEST_PLACEHOLDER_TIER_2);
        addItemToGroups(CHEST_PLACEHOLDER_TIER_3);
        addItemToGroups(CHEST_PLACEHOLDER_TIER_4);
        addItemToGroups(DEPOSITABLE_CHEST);
    }
}
