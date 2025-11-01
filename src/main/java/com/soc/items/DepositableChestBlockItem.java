package com.soc.items;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;

import static com.soc.blocks.util.DepositableProperty.DEPOSITABLE;

public class DepositableChestBlockItem extends BlockItem {
    public DepositableChestBlockItem(Settings settings) {
        super(Blocks.CHEST, settings);
    }

    @Override
    protected boolean place(ItemPlacementContext context, BlockState state) {
        return super.place(context, state.with(DEPOSITABLE, true));
    }
}
