package com.soc.mixin;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.soc.blocks.util.DepositableProperty.DEPOSITABLE;

@Mixin(ChestBlock.class)
public abstract class DepositableChest extends AbstractBlockBreak {
    @Override
	protected void socwars_deposit(BlockState state, World world, BlockPos pos, PlayerEntity player, CallbackInfo ci) {
		if (!world.isClient && state.get(DEPOSITABLE)) {
			final ItemStack stack = player.getStackInHand(Hand.MAIN_HAND);
			final Inventory inventory = ChestBlock.getInventory((ChestBlock)(Object)this, state, world, pos, true);
			if (inventory == null) return;

			final ItemStack deposited = stack.copyWithCount(0);
			int contained = 0;

			for (int i = 0; i < inventory.size() && stack.getCount() > 0; i++) {
				final ItemStack slot = inventory.getStack(i);

				if ((slot.isEmpty() || ItemStack.areItemsAndComponentsEqual(slot, stack))) {
					final int capacity = Math.min(stack.getMaxCount() - slot.getCount(), stack.getCount());
					contained += slot.getCount() + capacity;
					inventory.setStack(i, stack.copyWithCount(slot.getCount() + capacity));
					stack.setCount(stack.getCount() - capacity);

					deposited.setCount(deposited.getCount() + capacity);
				}
			}
			inventory.markDirty();

			if (!deposited.isEmpty()) {
				player.sendMessage(Text.translatable("game.chest.deposit", deposited.getCount(), deposited.toHoverableText(), Text.translatable("game.chest.count", contained).formatted(Formatting.GRAY)).formatted(Formatting.DARK_GREEN), false);
			}
		}
	}
}