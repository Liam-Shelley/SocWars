package com.soc.game.manager.bedwars.shopitems;

import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.item.Item;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public interface TooltipProvider {
    @Nullable Text getTooltip();

    static Text getEnchantmentTooltip(ItemEnchantmentsComponent enchantments) {
        if (enchantments == null || enchantments.isEmpty()) return null;

        final List<Text> textList = new ArrayList<>();
        enchantments.appendTooltip(Item.TooltipContext.DEFAULT, textList::add, TooltipType.BASIC, null);

        return textList.stream().reduce((a, b) -> ((MutableText)a).append("\n").append(b)).get();
    }
}
