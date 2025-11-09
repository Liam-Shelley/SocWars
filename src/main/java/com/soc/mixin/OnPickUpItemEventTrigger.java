package com.soc.mixin;

import com.soc.events.ModEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public abstract class OnPickUpItemEventTrigger implements Inventory {
	@Shadow public abstract ItemStack getStack(int slot);

	@Shadow @Final public PlayerEntity player;

	@Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;increment(I)V", ordinal = 0), method = "addStack(ILnet/minecraft/item/ItemStack;)I")
	private void socwars_onPickupItem(int slot, ItemStack pickupStack, CallbackInfoReturnable<Integer> cir) {
		if (this.player instanceof ServerPlayerEntity serverPlayerEntity) {
			final ItemStack slotStack = this.getStack(slot);

			final int stackGain = Math.min(pickupStack.getCount(), this.getMaxCount(slotStack) - slotStack.getCount());
			ModEvents.ON_ITEM_PICKUP.invoker().onItemPickup(serverPlayerEntity, pickupStack.copyWithCount(stackGain));
		}
	}
}