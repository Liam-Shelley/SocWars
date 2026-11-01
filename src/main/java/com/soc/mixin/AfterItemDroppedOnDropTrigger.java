package com.soc.mixin;

import com.soc.events.ModEvents;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class AfterItemDroppedOnDropTrigger {
	@Inject(at = @At("HEAD"), method = "dropItem", cancellable = true)
	private void socwars_dropItemEvent(ItemStack stack, boolean dropAtSelf, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> cir) {
		if ((Object)this instanceof ServerPlayerEntity serverPlayer) {
			if (!ModEvents.ON_ITEM_DROPPED.invoker().onDropItem(serverPlayer, stack)) {
				if ((Object)this instanceof PlayerEntity player) player.getInventory().insertStack(stack);
				cir.cancel();
			}
		}
	}
}