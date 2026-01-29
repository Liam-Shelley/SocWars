package com.soc.blocks;

import com.mojang.serialization.MapCodec;
import com.soc.game.map.DyeColourWithEmpty;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;

public class ColourStateBlock extends Block {
    public static final MapCodec<ColourStateBlock> CODEC = createCodec(ColourStateBlock::new);
    public static final EnumProperty<DyeColourWithEmpty> COLOUR = EnumProperty.of("colour", DyeColourWithEmpty.class);

    @Override
    public MapCodec<ColourStateBlock> getCodec() {
        return CODEC;
    }

    public ColourStateBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getDefaultState().with(COLOUR, DyeColourWithEmpty.EMPTY));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(COLOUR);
    }
}
