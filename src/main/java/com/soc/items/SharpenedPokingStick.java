package com.soc.items;

import com.soc.SocWars;
import com.soc.items.util.ModItems;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public class SharpenedPokingStick extends Item {

    public SharpenedPokingStick(Settings settings) {
        super(settings);
    }

    public static void initialise() {
        com.soc.items.util.ItemGroups.addItemToGroupsAndBaseItemGroup(SHARPENED_POKING_STICK, ItemGroups.COMBAT);
    }

    public static final Item SHARPENED_POKING_STICK = ModItems.register("sharpened_poking_stick", SharpenedPokingStick::new, new Settings().maxDamage(150).rarity(Rarity.UNCOMMON).attributeModifiers(AttributeModifiersComponent.builder()
            .add(EntityAttributes.ENTITY_INTERACTION_RANGE, new EntityAttributeModifier(Identifier.of(SocWars.MOD_ID, "stick_reach"), 3, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.HAND)
            .add(EntityAttributes.ATTACK_DAMAGE, new EntityAttributeModifier(Identifier.of(SocWars.MOD_ID, "stick_damage"), 5, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.HAND)
            .add(EntityAttributes.ATTACK_SPEED, new EntityAttributeModifier(Identifier.of(SocWars.MOD_ID, "stick_damage"), 0.2, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.HAND)
            .build()
    ));
}
