package com.soc.items.util;

import com.soc.SocWars;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.ArmorMaterials;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.function.Consumer;

import static com.soc.lib.SocWarsLib.getComponentFromSettingsBuilder;

public abstract class ArmourItem extends Item {
    protected final EquipmentSlot slot;
    protected final int armour;

    public ArmourItem(Settings settings, final EquipmentSlot slot, final int armour, final RegistryKey<EquipmentAsset> equipmentAsset) {
        super(settingsWithArmourModifier(settings
                .component(DataComponentTypes.EQUIPPABLE, EquippableComponent.builder(slot)
                .equipSound(ArmorMaterials.DIAMOND.equipSound())
                .model(equipmentAsset).build())
                .maxCount(1), slot, armour)
        );

        this.slot = slot;
        this.armour = armour;
    }

    private static Settings settingsWithArmourModifier(Settings settings, EquipmentSlot slot, int armour) {
        final AttributeModifiersComponent modifiers = getComponentFromSettingsBuilder(settings, DataComponentTypes.ATTRIBUTE_MODIFIERS);
        settings.attributeModifiers(modifiers.with(EntityAttributes.ARMOR, new EntityAttributeModifier(Identifier.of(SocWars.MOD_ID, "armour." + slot.getName()), armour, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.ARMOR));

        return settings;
    }

    public static RegistryKey<EquipmentAsset> registerEquipmentAsset(String name) {
        return RegistryKey.of(RegistryKey.ofRegistry(Identifier.ofVanilla("equipment_asset")), Identifier.of("socwars:" + name));
    }

    @SuppressWarnings("deprecation")
    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        super.appendTooltip(stack, context, displayComponent, textConsumer, type);

        textConsumer.accept(Text.empty());
        textConsumer.accept(Text.translatable("item.modifiers." + this.slot.getName()).formatted(Formatting.GRAY));
        textConsumer.accept(Text.translatable("attribute.modifier.plus.0", this.armour, Text.translatable("attribute.name.armor")).formatted(Formatting.BLUE));
    }
}
