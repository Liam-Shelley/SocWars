package com.soc.mixin;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ItemEntity.class)
public abstract class ItemEntityInventoryMixin implements Inventory {
	@Shadow
	public abstract ItemStack getStack();

	@Shadow
	public abstract void setStack(ItemStack stack);

	@Override
	public int size() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return this.getStack().isEmpty();
	}

	@Override
	public ItemStack getStack(int slot) {
		return this.getStack();
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		return this.getStack().split(amount);
	}

	@Override
	public ItemStack removeStack(int slot) {
		return this.getStack().copyAndEmpty();
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		this.setStack(stack);
	}

	@Override
	public void markDirty() {
		
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		return false;
	}

	@Override
	public void clear() {
		this.setStack(ItemStack.EMPTY);
	}
}
