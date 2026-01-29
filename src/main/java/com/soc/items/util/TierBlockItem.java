package com.soc.items.util;

import com.soc.blocks.TierBlock;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;

public class TierBlockItem extends BlockItem {
    private final int tier;

    public TierBlockItem(TierBlock block, Settings settings, int tier) {
        super(block, settings);
        this.tier = tier;
    }

    @Override
    protected boolean place(ItemPlacementContext context, BlockState state) {
        return super.place(context, state.with(TierBlock.TIER, this.tier));
    }
}
