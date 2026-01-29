package com.soc.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.soc.blocks.util.DepositableProperty.DEPOSITABLE;
import static com.soc.lib.SocWarsLib.depositStackIntoInventory;

@Mixin(EnderChestBlock.class)
public abstract class DepositableEnderChest extends AbstractBlockBreak {
    @Override
	protected void socwars_deposit(BlockState state, World world, BlockPos pos, PlayerEntity player, CallbackInfo ci) {
		if (!world.isClient && state.get(DEPOSITABLE)) {
			final ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
			final Inventory inventory = player.getEnderChestInventory();

			depositStackIntoInventory(stack, inventory, player, player.isSneaking());
		}
	}
}