package com.soc.items.util;

import com.soc.SocWars;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.equipment.ArmorMaterials;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import static com.soc.lib.SocWarsLib.getComponentFromSettingsBuilder;

public abstract class ArmourItem extends Item {
    protected final EquipmentSlot slot;
    protected final int armour;

    public ArmourItem(Settings settings, final EquipmentSlot slot, final int armour, final RegistryKey<EquipmentAsset> equipmentAsset) {
        super(settings
                .component(DataComponentTypes.EQUIPPABLE, EquippableComponent.builder(slot)
                .equipSound(ArmorMaterials.DIAMOND.equipSound())
                .model(equipmentAsset).build())
                .maxCount(1)
        );
        final AttributeModifiersComponent modifiers = getComponentFromSettingsBuilder(settings, DataComponentTypes.ATTRIBUTE_MODIFIERS);
        modifiers.with(EntityAttributes.ARMOR, new EntityAttributeModifier(Identifier.of(SocWars.MOD_ID, "armour." + slot.getName()), armour, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.ARMOR);
        settings.attributeModifiers(modifiers);

        this.slot = slot;
        this.armour = armour;
    }

    public static RegistryKey<EquipmentAsset> registerEquipmentAsset(String name) {
        return RegistryKey.of(RegistryKey.ofRegistry(Identifier.ofVanilla("equipment_asset")), Identifier.of("socwars:" + name));
    }
}
