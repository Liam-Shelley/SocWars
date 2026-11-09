package com.soc.mixin;

import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HopperBlockEntity.class)
abstract class HopperInventoryCrashBandaid {
	@Inject(method = "getAvailableSlots", at = @At("HEAD"), cancellable = true)
	private static void socwars_hopperInventoryCrashBandaid(Inventory inventory, Direction side, CallbackInfoReturnable<int[]> cir) {
		if (!(inventory instanceof Inventory)) cir.setReturnValue(new int[] {});
	}
}
