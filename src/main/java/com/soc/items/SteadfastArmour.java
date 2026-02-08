package com.soc.items;

import com.soc.SocWars;
import com.soc.items.util.ArmourItem;
import com.soc.items.util.ModItems;
import com.soc.items.util.SetBonusArmourItem;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.function.Consumer;

import static com.soc.items.util.ItemGroups.addItemToGroupsAndBaseItemGroup;

public class SteadfastArmour extends SetBonusArmourItem {
    private static final RegistryKey<EquipmentAsset> STEADFAST_MODEL_KEY = ArmourItem.registerEquipmentAsset("steadfast");

    public SteadfastArmour(final Settings settings, final EquipmentSlot slot, final int armour) {
        super(settings, slot, armour, STEADFAST_MODEL_KEY, EntityAttributes.KNOCKBACK_RESISTANCE, Identifier.of(SocWars.MOD_ID, "steadfast"));
    }

    public static void initialise() {
        addItemToGroupsAndBaseItemGroup(STEADFAST_HELMET, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(STEADFAST_CHESTPLATE, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(STEADFAST_LEGGINGS, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(STEADFAST_BOOTS, ItemGroups.COMBAT);
    }

    public static final Item STEADFAST_HELMET = ModItems.register("steadfast_helmet", (settings) -> new SteadfastArmour(settings, EquipmentSlot.HEAD, 2), new Settings().maxDamage(325).rarity(Rarity.RARE));
    public static final Item STEADFAST_CHESTPLATE = ModItems.register("steadfast_chestplate", (settings) -> new SteadfastArmour(settings, EquipmentSlot.CHEST, 6), new Settings().maxDamage(400).rarity(Rarity.RARE));
    public static final Item STEADFAST_LEGGINGS = ModItems.register("steadfast_leggings", (settings) -> new SteadfastArmour(settings, EquipmentSlot.LEGS, 5), new Settings().maxDamage(375).rarity(Rarity.RARE));
    public static final Item STEADFAST_BOOTS = ModItems.register("steadfast_boots", (settings) -> new SteadfastArmour(settings, EquipmentSlot.FEET, 2), new Settings().maxDamage(325).rarity(Rarity.RARE));
}
