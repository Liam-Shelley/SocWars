package com.soc.items.util;

import com.soc.blocks.ColourStateBlock;
import com.soc.game.map.DyeColourWithEmpty;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;

public class ColourStateBlockItem extends BlockItem {
    private final DyeColourWithEmpty colour;

    public ColourStateBlockItem(ColourStateBlock block, Settings settings, DyeColourWithEmpty colour) {
        super(block, settings);
        this.colour = colour;
    }

    @Override
    protected boolean place(ItemPlacementContext context, BlockState state) {
        return super.place(context, state.with(ColourStateBlock.COLOUR, this.colour));
    }
}
