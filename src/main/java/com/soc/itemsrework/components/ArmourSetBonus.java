package com.soc.itemsrework.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.text.NumberFormat;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public record ArmourSetBonus(RegistryEntry<EntityAttribute> attribute, Identifier id) implements SocWarsTooltipAppender {
	public static final Codec<ArmourSetBonus> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			EntityAttribute.CODEC.fieldOf("attribute").forGetter(ArmourSetBonus::attribute),
			Identifier.CODEC.fieldOf("id").forGetter(ArmourSetBonus::id)
	).apply(instance, ArmourSetBonus::new));

	private static final NumberFormat NUMBER_FORMAT;

	static {
		NUMBER_FORMAT = (NumberFormat)NumberFormat.getInstance().clone();
		NUMBER_FORMAT.setMaximumFractionDigits(0);
	}

	public ArmourSetBonus(RegistryEntry<EntityAttribute> attribute, Identifier id) {
		this.attribute = attribute;
		this.id = id.withPrefixedPath("armour_set_bonus.");
	}

	@Override
	public void appendTooltip(Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type, ComponentsAccess components) {
		final Text attributeText = Text.translatable("attribute.name." + this.attribute.getIdAsString().split(":")[1]);

		textConsumer.accept(Text.translatable("stat_bonus_modifier", NUMBER_FORMAT.format(20d), attributeText).formatted(Formatting.BLUE));
		textConsumer.accept(Text.empty());
		textConsumer.accept(Text.translatable("full_set_worn").formatted(Formatting.GRAY));
		textConsumer.accept(Text.translatable("stat_bonus_modifier", NUMBER_FORMAT.format(100d), attributeText).formatted(Formatting.DARK_GREEN));
	}

	public void applyAttributeModifiers(BiConsumer<RegistryEntry<EntityAttribute>, EntityAttributeModifier> attributeConsumer, int pieces) {
		attributeConsumer.accept(this.attribute, new EntityAttributeModifier(this.id, pieces == 4 ? 1d : pieces * 0.2d, EntityAttributeModifier.Operation.ADD_VALUE));
	}
}
