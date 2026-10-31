package com.soc.items;

import net.minecraft.block.Block;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class DoomBlockItem extends BlockItem {
    public DoomBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    public DoomBlockItem(Block block, RegistryKey<Item> itemKey) {
        this(block, new Settings().registryKey(itemKey));
    }

    @Override
    @SuppressWarnings("deprecation")
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        textConsumer.accept(Text.translatable("tooltip.doom"));
    }
}
