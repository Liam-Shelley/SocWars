package com.soc.blocks;

import com.soc.game.map.DyeColourWithEmpty;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

public class ModifiableColourStateBlock extends ColourStateBlock {
    public ModifiableColourStateBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!player.isCreative()) return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;

        if (player.isSneaking()) {
            world.setBlockState(pos, state.with(COLOUR, DyeColourWithEmpty.EMPTY));

            return ActionResult.SUCCESS;
        }

        final Optional<TagKey<Item>> dyedTag = stack.streamTags().filter(tag -> tag.toString().contains("dyed/")).findFirst();
        if (dyedTag.isEmpty()) return ActionResult.PASS_TO_DEFAULT_BLOCK_ACTION;


        try {
            final String tagString = dyedTag.toString().split("/")[2].replace("]", "");
            world.setBlockState(pos, state.with(COLOUR, DyeColourWithEmpty.fromDyeColour(DyeColor.valueOf(tagString.toUpperCase()))));

            return ActionResult.SUCCESS;
        } catch (IllegalArgumentException e) {
            return ActionResult.FAIL;
        }
    }
}
