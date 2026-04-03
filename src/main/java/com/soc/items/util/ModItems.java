package com.soc.items.util;

import com.soc.SocWars;
import com.soc.items.*;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ModItems {
    public static void initialise() {
        ItemGroups.initialise();

        BaseWeapon.initialise();
        AttackFunctionWeapon.initialise();
        UseFunctionWeapon.initialise();
        PotionWeapon.initialise();
        DrawableWeapon.initialise();
        BowItem.initialise();
        GreenSword.initialise();
        InvisibilityRing.initialise();
        PotionRing.initialise();
        DiceOfFate.initialise();
        PotionFood.initialise();
        EatFunctionFood.initialise();
        SharpenedPokingStick.initialise();
        GamblerArmour.initialise();
        GamblerSword.initialise();
        SteadfastArmour.initialise();
        CartoonArmour.initialise();
        GlassArmour.initialise();
        DemolitionistArmour.initialise();
        TrainingWeights.initialise();
        SummonersGarb.initialise();
        Expon.initialise();
        ThrowableItem.initialise();
        ExtendoBridge.initialise();
        BlockItems.initialise();
        BaseTool.initialise();
        BaseItem.initialise();
        JetShoppingTrolley.initialise();
        SeekingStick.initialise();
        TauntStick.initialise();
    }

    public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
        // Create the item key.
        final RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SocWars.MOD_ID, name));

        // Create the item instance.
        final Item item = itemFactory.apply(settings.registryKey(itemKey));

        // Register the item.
        Registry.register(Registries.ITEM, itemKey, item);

        return item;
    }
}