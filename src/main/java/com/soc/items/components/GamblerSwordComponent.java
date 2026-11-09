package com.soc.items.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.ComponentsAccess;
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
import java.util.function.Consumer;

import static net.minecraft.util.Util.getRandom;

public record GamblerSwordComponent(int damage, long nextChangeTime) implements ItemTickComponent, SocWarsTooltipAppender, ComponentWithAttributeModifiers {
	public static final GamblerSwordComponent DEFAULT = new GamblerSwordComponent(0, 0L);

	public static final Identifier ATTRIBUTE_ID = Identifier.of("gambler_damage");
	private static final int[] DAMAGE_VALUES = {2, 4, 6, 8, 10};

	public static final Codec<GamblerSwordComponent> CODEC = RecordCodecBuilder.create(builder -> builder.group(
			Codec.INT.fieldOf("damage").forGetter(GamblerSwordComponent::damage),
			Codec.LONG.fieldOf("next_time_change").forGetter(GamblerSwordComponent::nextChangeTime)
	).apply(builder, GamblerSwordComponent::new));

	@Override
	public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
		if (this.nextChangeTime <= world.getTime()) stack.set(ModItemComponents.GAMBLER_SWORD, new GamblerSwordComponent(getRandom(DAMAGE_VALUES, world.random), world.getTime() + (long)world.random.nextBetween(6, 16)));
	}

	@Override
	public void appendTooltip(Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type, ComponentsAccess components) {
		final Formatting formatting = switch(this.damage) {
			case 2 -> Formatting.RED;
			case 4 -> Formatting.GOLD;
			case 6 -> Formatting.YELLOW;
			case 8 -> Formatting.GREEN;
			case 10 -> Formatting.DARK_GREEN;
			default -> Formatting.BLACK;
		};

		textConsumer.accept(Text.empty());
		textConsumer.accept(Text.translatable("item.modifiers.mainhand").formatted(Formatting.GRAY));
		textConsumer.accept(Text.translatable("gambler_attack_damage", this.damage).formatted(formatting)); //TODO: Move these to the actual minecraft translations
		textConsumer.accept(Text.translatable("attack_speed", 1.6f).formatted(Formatting.DARK_GREEN));
	}

	@Override
	public List<AttributeModifiersComponent.Entry> getModifiers(EquipmentSlot slot) {
		return List.of(new AttributeModifiersComponent.Entry(EntityAttributes.ATTACK_DAMAGE, new EntityAttributeModifier(ATTRIBUTE_ID, this.damage - 1, EntityAttributeModifier.Operation.ADD_VALUE), AttributeModifierSlot.MAINHAND));
	}
}
