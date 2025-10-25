package com.soc.items;

import com.soc.blocks.util.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class FeatherBlockItem extends BlockItem {
    public FeatherBlockItem(Block block, Settings settings) {
        super(block, settings);
    }

    public FeatherBlockItem(Block block, RegistryKey<Item> itemKey) {
        this(block, new Settings().registryKey(itemKey));
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (user.getGameMode() == GameMode.ADVENTURE) return ActionResult.PASS;

        final BlockPos pos = BlockPos.ofFloored(user.getEyePos().add(user.getRotationVector().multiply(user.getBlockInteractionRange())));
        world.setBlockState(pos, ModBlocks.FEATHER_BLOCK.getDefaultState());
        user.getStackInHand(hand).decrementUnlessCreative(1, user);
        return ActionResult.SUCCESS;
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        super.inventoryTick(stack, world, entity, slot);
    }
}
