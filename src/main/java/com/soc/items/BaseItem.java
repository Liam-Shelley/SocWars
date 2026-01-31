package com.soc.items;

import com.soc.items.components.ModComponents;
import com.soc.items.util.ItemGroups;
import com.soc.items.util.ModItems;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class BaseItem extends Item {
    private final BiConsumer<ItemStack, Consumer<Text>> tooltipFunction;

    public BaseItem(Settings settings, BiConsumer<ItemStack, Consumer<Text>> tooltipFunction) {
        super(settings);
        this.tooltipFunction = tooltipFunction;
    }

    public BaseItem(Settings settings, Text simpleTooltip) {
        this(settings, (a, b) -> b.accept(simpleTooltip));
    }

    public BaseItem(Settings settings) {
        this(settings, (a, b) -> {});
    }

    public static void initialise() {
        ItemGroups.addItemToItemsGroup(PORTABLE_STEPPING_STOOL);
    }

    public static final Item PORTABLE_STEPPING_STOOL = ModItems.register("portable_stepping_stool", settings -> new BaseItem(settings, Text.translatable("tooltip.portable_stepping_stool")), new Settings().component(ModComponents.DOUBLE_JUMP, true).rarity(Rarity.RARE).maxCount(1));

    @Override
    @SuppressWarnings("deprecation")
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        this.tooltipFunction.accept(stack, textConsumer);
    }
}
