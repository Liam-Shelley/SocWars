package com.soc.items;

import com.soc.items.util.ModItems;
import com.soc.items.util.RingItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.util.Rarity;

import static com.soc.items.util.ItemGroups.addItemToGroupsAndBaseItemGroup;

public class InvisibilityRing extends RingItem {

    public InvisibilityRing(Settings settings) {
        super(settings);
    }

    public static void initialise() {
        addItemToGroupsAndBaseItemGroup(INVISIBILITY_RING, ItemGroups.TOOLS);
    }

    public static final Item INVISIBILITY_RING = ModItems.register("invisibility_ring", InvisibilityRing::new, new Settings().maxDamage(20 * 40).rarity(Rarity.UNCOMMON));

    @Override
    protected void ringUse(LivingEntity user) {
        user.setInvisible(true);
    }

    @Override
    protected void ringFinishUse(LivingEntity user) {
        user.setInvisible(false);
    }
}
