package com.soc.itemsrework;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.soc.itemsrework.components.ItemTickComponent;
import com.soc.itemsrework.components.ModItemComponentsReworked;
import com.soc.itemsrework.components.SocWarsTooltipAppender;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentsAccess;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static com.soc.lib.SocWarsLib.getTimeFromTicksDynColours;

public record TrainingWeightsComponent(int timeStored) implements ItemTickComponent, SocWarsTooltipAppender {
	public static final TrainingWeightsComponent INSTANCE = new TrainingWeightsComponent(0);

	public static final Codec<TrainingWeightsComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.INT.fieldOf("time_stored").orElse(0).forGetter(trainingWeights -> trainingWeights.timeStored)
	).apply(instance, TrainingWeightsComponent::new));

	@Override
	public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) { //TODO: Rewrite this piece of garbage
		if (!(entity instanceof LivingEntity user)) return;

		final int multiplier = user.isSneaking() ? 2 : 1;

		if (slot == EquipmentSlot.FEET) {
			user.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 2, multiplier - 1, false, false));
		}

		if (world.getTime() % 20 == 0) {
			int newTimeStored = this.timeStored;

			if (slot == EquipmentSlot.FEET) {
				newTimeStored += multiplier;
			} else if (this.timeStored > 0) {
				newTimeStored--;
				user.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 1000, 2, false, false));
			}

			stack.set(ModItemComponentsReworked.TRAINING_WEIGHTS, new TrainingWeightsComponent(newTimeStored));

			stack.applyChanges(ComponentChanges.builder().add(ModItemComponentsReworked.TRAINING_WEIGHTS, new TrainingWeightsComponent(newTimeStored)).build());
		}
	}

	@Override
	public void appendTooltip(Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type, ComponentsAccess components) {
		final int value = Math.max(0, this.timeStored);

		final boolean over1min = value >= 60;
		textConsumer.accept(Text.empty());
		textConsumer.accept(Text.translatable("tooltip.training_weights.stored_time", getTimeFromTicksDynColours(value, false, minutes -> minutes > 0 ? 0xff14992c : 0xff1d59c2, seconds -> {
			if (over1min) return 0xff14992c;
			if (seconds > 30) return 0xff14992c;
			if (seconds > 10) return 0xfff2e41b;
			return 0xffe32b1e;
		})));
	}
}
