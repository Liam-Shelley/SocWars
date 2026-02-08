package com.soc.items.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public abstract class SetBonusArmourItem extends ArmourItem implements OnEquipArmour {
    private final RegistryEntry<EntityAttribute> attribute;
    private final Identifier modifierId;

    public SetBonusArmourItem(Settings settings, EquipmentSlot slot, int armour, RegistryKey<EquipmentAsset> equipmentAsset, RegistryEntry<EntityAttribute> attribute, Identifier modifierId) {
        super(settings, slot, armour, equipmentAsset);
        this.attribute = attribute;
        this.modifierId = modifierId;
    }

    protected BiMap<Integer, Double> getSetBonus() {
        return ImmutableBiMap.of(
                1, 0.2d,
                2, 0.4d,
                3, 0.6d,
                4, 1d
        );
    }

    private double getSetBonus(int stage) {
        return this.getSetBonus().get(stage);
    }

    private int getAttributeStage(double bonus) {
        return this.getSetBonus().inverse().get(bonus);
    }

    private double getCurrentModifier(EntityAttributeInstance attributes, Identifier modifierId) {
        final EntityAttributeModifier modifier = attributes.getModifier(modifierId);
        return modifier == null ? 0d : modifier.value();
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public final void unequip(PlayerEntity player) {
        final EntityAttributeInstance attribute = player.getAttributeInstance(this.attribute);

        final double currentModifier = this.getCurrentModifier(attribute, this.modifierId);
        if (currentModifier == 0d) return;

        final int newStage = this.getAttributeStage(currentModifier) - 1;
        if (newStage == 0) {
            attribute.removeModifier(this.modifierId);
            return;
        }

        final double newValue = this.getSetBonus(newStage);
        attribute.overwritePersistentModifier(new EntityAttributeModifier(this.modifierId, newValue, EntityAttributeModifier.Operation.ADD_VALUE));
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public final void equip(PlayerEntity player) {
        final EntityAttributeInstance attribute = player.getAttributeInstance(this.attribute);

        final double currentModifier = this.getCurrentModifier(attribute, this.modifierId);
        if (currentModifier == 0d) {
            attribute.overwritePersistentModifier(new EntityAttributeModifier(this.modifierId, this.getSetBonus(1), EntityAttributeModifier.Operation.ADD_VALUE));
            return;
        }

        final int newStage = this.getAttributeStage(currentModifier) + 1;
        if (newStage == 5) {
            return;
        }

        final double newValue = this.getSetBonus(newStage);
        attribute.overwritePersistentModifier(new EntityAttributeModifier(this.modifierId, newValue, EntityAttributeModifier.Operation.ADD_VALUE));
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        super.appendTooltip(stack, context, displayComponent, textConsumer, type);

        Text attributeText = Text.translatable("attribute.name." + this.attribute.getIdAsString().split(":")[1]);

        textConsumer.accept(Text.translatable("stat_bonus_modifier", this.getSetBonus(1) * 100d, attributeText).formatted(Formatting.BLUE));
        textConsumer.accept(Text.translatable("full_set_worn").formatted(Formatting.GRAY));
        textConsumer.accept(Text.translatable("stat_bonus_modifier", this.getSetBonus(4) * 100d, attributeText).formatted(Formatting.DARK_GREEN));
    }
}
