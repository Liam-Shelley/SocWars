package com.soc.items;

import com.soc.effects.util.ModEffects;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.SplashPotionItem;

import static com.soc.items.util.ItemGroups.addItemToItemsGroup;
import static com.soc.items.util.ModItems.register;

public class HardCodes {
    //TODO: Redo things so that this class isn't necessary
    public static void initialise() {
        addItemToItemsGroup(PERPLEXITY_2_SPLASH_POTION);
    }

    public static final Item PERPLEXITY_2_SPLASH_POTION = register("perplexity_2_splash_potion", SplashPotionItem::new, new Item.Settings().maxCount(1).component(DataComponentTypes.POTION_CONTENTS, new PotionContentsComponent(ModEffects.PERPLEXITY_STRONG)));
}
