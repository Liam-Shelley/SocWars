package com.soc.mixin;

import com.soc.events.ModEvents;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockItem.class)
public abstract class OnBlockPlacedEventTrigger {
	@Inject(at = @At("HEAD"), method = "useOnBlock", cancellable = true)
	private void socwars_onBlockPlacedEvent(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
		if (context.getPlayer() instanceof ServerPlayerEntity serverPlayer) {
			final ActionResult result = ModEvents.ON_BLOCK_PLACED.invoker().onItemPickup(serverPlayer, context.getWorld().getBlockState(context.getBlockPos()).isReplaceable() ? context.getBlockPos() : context.getBlockPos().offset(context.getSide()), context);
			if (result != ActionResult.PASS) cir.setReturnValue(result);
		}
	}
}