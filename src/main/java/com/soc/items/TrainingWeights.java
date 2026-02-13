package com.soc.items;

import com.soc.items.components.ModComponents;
import com.soc.items.util.ArmourItem;
import com.soc.items.util.ModItems;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.equipment.EquipmentAsset;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

import static com.soc.lib.SocWarsLib.getTimeFromTicksDynColours;

public class TrainingWeights extends ArmourItem {
    private static final RegistryKey<EquipmentAsset> TRAINING_WEIGHTS_MODEL_KEY = ArmourItem.registerEquipmentAsset("training_weights");

    public TrainingWeights(Settings settings, EquipmentSlot slot, int armour) {
        super(settings.component(ModComponents.TRAINING_WEIGHTS_COMPONENT, 0), slot, armour, TRAINING_WEIGHTS_MODEL_KEY);
    }

    public static void initialise() {
        com.soc.items.util.ItemGroups.addItemToGroupsAndBaseItemGroup(TRAINING_WEIGHTS, ItemGroups.COMBAT);
    }

    public static final Item TRAINING_WEIGHTS = ModItems.register("training_weights", settings -> new TrainingWeights(settings, EquipmentSlot.FEET, 2), new Settings()
            .rarity(Rarity.UNCOMMON)
            .maxDamage(275)
    );

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        if (entity instanceof LivingEntity user) {
            int multiplier = user.isSneaking() ? 2 : 1;
            if (slot == EquipmentSlot.FEET) {
                user.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 2, multiplier - 1, false, false));
            }

            if (world.getTime() % 20 > 0) return;

            int storedTime = stack.getOrDefault(ModComponents.TRAINING_WEIGHTS_COMPONENT, 0);

            if (slot == EquipmentSlot.FEET) {
                storedTime += multiplier;
            } else {
                if (storedTime <= 0) return;

                storedTime--;
                user.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 20, 0, false, false));
            }

            stack.applyChanges(ComponentChanges.builder().add(ModComponents.TRAINING_WEIGHTS_COMPONENT, storedTime).build());
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        final int value = Math.max(0, stack.getOrDefault(ModComponents.TRAINING_WEIGHTS_COMPONENT, 0) - 1);

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
