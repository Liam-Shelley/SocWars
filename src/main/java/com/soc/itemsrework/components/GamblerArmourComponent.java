package com.soc.itemsrework.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.soc.lib.SocWarsLib.mapIfNotNull;
import static net.minecraft.util.Util.getRandom;

public record GamblerArmourComponent(int armour, long nextChangeTime) implements ItemTickComponent, SocWarsTooltipAppender, ComponentWithAttributeModifiers {
	public static final GamblerArmourComponent DEFAULT = new GamblerArmourComponent(0, 0L);

	public static final Identifier ATTRIBUTE_ID = Identifier.of("gambler_armour");
	private static final Map<EquipmentSlot, int[]> ARMOUR_VALUES = Map.of(
			EquipmentSlot.HEAD, new int[]{7, 5, 3, 1, 0},
			EquipmentSlot.CHEST, new int[]{9 ,7, 5, 3, 1},
			EquipmentSlot.LEGS, new int[]{8, 6, 4, 2, 0},
			EquipmentSlot.FEET, new int[]{5, 4, 2, 1, 0}
	);

	public static final Codec<GamblerArmourComponent> CODEC = RecordCodecBuilder.create(builder -> builder.group(
			Codec.INT.fieldOf("armour").forGetter(GamblerArmourComponent::armour),
			Codec.LONG.fieldOf("next_time_change").forGetter(GamblerArmourComponent::nextChangeTime)
	).apply(builder, GamblerArmourComponent::new));

	@Override
	public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
		if (this.nextChangeTime <= world.getTime()) {
			final int armourValue = mapIfNotNull(stack.get(DataComponentTypes.EQUIPPABLE), equippableComponent -> getRandom(ARMOUR_VALUES.get(equippableComponent.slot()), world.random), 0);
			stack.set(ModItemComponentsReworked.GAMBLER_ARMOUR, new GamblerArmourComponent(armourValue, world.getTime() + (long)world.random.nextBetween(6, 16)));
		}
	}

	@Override
	public void appendTooltip(Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type, ComponentsAccess components) {
		final Formatting formatting = switch(this.armour) {
			case 0 -> Formatting.DARK_RED;
			case 1 -> Formatting.RED;
			case 2, 3 -> Formatting.GOLD;
			case 4, 5 -> Formatting.YELLOW;
			case 6, 7 -> Formatting.GREEN;
			case 8, 9 -> Formatting.DARK_GREEN;
			default -> Formatting.BLACK;
		};

		textConsumer.accept(Text.empty());
		textConsumer.accept(Text.translatable("item.modifiers.armor").formatted(Formatting.GRAY));
		textConsumer.accept(Text.translatable("gambler_armour_amount", this.armour).formatted(formatting));
	}

	@Override
	public List<AttributeModifiersComponent.Entry> getModifiers(EquipmentSlot slot) {
		return List.of(new AttributeModifiersComponent.Entry(EntityAttributes.ARMOR, new EntityAttributeModifier(ATTRIBUTE_ID.withSuffixedPath("." + slot.getName()), this.armour, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.ARMOR));
	}
}
