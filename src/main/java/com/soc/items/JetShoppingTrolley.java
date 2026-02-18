package com.soc.items;

import com.soc.entities.JetShoppingTrolleyEntity;
import com.soc.items.util.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.world.World;

import static com.soc.items.util.ItemGroups.addItemToGroupsAndBaseItemGroup;

public class JetShoppingTrolley extends Item {
    public static void initialise() {
        addItemToGroupsAndBaseItemGroup(JET_SHOPPING_TROLLEY, ItemGroups.TOOLS);
    }

    public JetShoppingTrolley(Settings settings) {
        super(settings);
    }

    public static final Item JET_SHOPPING_TROLLEY = ModItems.register("jet_shopping_trolley", JetShoppingTrolley::new, new Settings().rarity(Rarity.RARE).maxCount(1));

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        user.getStackInHand(hand).decrementUnlessCreative(1, user);
        world.spawnEntity(new JetShoppingTrolleyEntity(user));

        return ActionResult.SUCCESS;
    }
}
