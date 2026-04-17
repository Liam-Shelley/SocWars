package com.soc.mixin;

import com.soc.game.manager.GamesManager;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.soc.blocks.util.DepositableProperty.DEPOSITABLE;
import static com.soc.lib.SocWarsLib.depositStackIntoInventory;

@Mixin(ChestBlock.class)
public abstract class DepositableChest extends AbstractBlockBreak {
    @Override
	protected void socwars_deposit(BlockState state, World world, BlockPos pos, PlayerEntity player, CallbackInfo ci) {
		if (!world.isClient && state.get(DEPOSITABLE)) {
			GamesManager.getInstance().getGame(player).ifPresentOrElse(manager -> {
				if (manager.onChestOpened((ServerPlayerEntity)player, pos)) {
					this.deposit(state, world, pos, player);
				}
			}, () -> this.deposit(state, world, pos, player));
		}
	}

	@Unique
	private void deposit(BlockState state, World world, BlockPos pos, PlayerEntity player) {
		final ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
		final Inventory inventory = ChestBlock.getInventory((ChestBlock)(Object)this, state, world, pos, true);

		depositStackIntoInventory(stack, inventory, player, player.isSneaking());
	}
}