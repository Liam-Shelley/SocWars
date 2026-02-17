package com.soc.items;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;

import static com.soc.blocks.util.DepositableProperty.DEPOSITABLE;

public class DepositableEnderChestBlockItem extends BlockItem {
    public DepositableEnderChestBlockItem(Settings settings) {
        super(Blocks.ENDER_CHEST, settings);
    }

    @Override
    protected boolean place(ItemPlacementContext context, BlockState state) {
        return super.place(context, state.with(DEPOSITABLE, true));
    }
}
