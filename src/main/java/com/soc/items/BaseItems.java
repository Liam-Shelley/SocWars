package com.soc.items;

import com.soc.items.components.ModComponents;
import com.soc.items.util.ItemGroups;
import com.soc.items.util.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Settings;

public class BaseItems {
    public static void initialise() {
        ItemGroups.addItemToItemsGroup(PORTABLE_STEPPING_STOOL);
    }

    public static final Item PORTABLE_STEPPING_STOOL = ModItems.register("portable_stepping_stool", Item::new, new Settings().component(ModComponents.DOUBLE_JUMP, true));
}
