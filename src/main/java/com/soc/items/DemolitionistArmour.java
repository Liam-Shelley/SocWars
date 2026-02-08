package com.soc.items;

import com.soc.SocWars;
import com.soc.items.util.ArmourItem;
import com.soc.items.util.ModItems;
import com.soc.items.util.SetBonusArmourItem;
import com.soc.lib.EntityAttributes;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.function.Consumer;

import static com.soc.items.util.ItemGroups.addItemToGroupsAndBaseItemGroup;

public class DemolitionistArmour extends SetBonusArmourItem {
    private static final RegistryKey<EquipmentAsset> DEMOLITIONIST_MODEL_KEY = ArmourItem.registerEquipmentAsset("demolitionist");

    public DemolitionistArmour(Settings settings, EquipmentSlot slot, int armour) {
        super(settings, slot, armour, DEMOLITIONIST_MODEL_KEY, EntityAttributes.EXPLOSION_RESISTANCE, Identifier.of(SocWars.MOD_ID, "demolitionist"));
    }

    public static void initialise() {
        addItemToGroupsAndBaseItemGroup(DEMOLITIONIST_HELMET, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(DEMOLITIONIST_CHESTPLATE, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(DEMOLITIONIST_LEGGINGS, ItemGroups.COMBAT);
        addItemToGroupsAndBaseItemGroup(DEMOLITIONIST_BOOTS, ItemGroups.COMBAT);
    }

    public static final Item DEMOLITIONIST_HELMET = ModItems.register("demolitionist_helmet", settings -> new DemolitionistArmour(settings, EquipmentSlot.HEAD, 4), new Settings().maxDamage(425).rarity(Rarity.UNCOMMON));
    public static final Item DEMOLITIONIST_CHESTPLATE = ModItems.register("demolitionist_chestplate", settings -> new DemolitionistArmour(settings, EquipmentSlot.CHEST, 3), new Settings().maxDamage(500).rarity(Rarity.UNCOMMON));
    public static final Item DEMOLITIONIST_LEGGINGS = ModItems.register("demolitionist_leggings", settings -> new DemolitionistArmour(settings, EquipmentSlot.LEGS, 3), new Settings().maxDamage(450).rarity(Rarity.UNCOMMON));
    public static final Item DEMOLITIONIST_BOOTS = ModItems.register("demolitionist_boots", settings -> new DemolitionistArmour(settings, EquipmentSlot.FEET, 2), new Settings().maxDamage(425).rarity(Rarity.UNCOMMON));

    @Override
    @SuppressWarnings("deprecation")
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        super.appendTooltip(stack, context, displayComponent, textConsumer, type);
    }
}
