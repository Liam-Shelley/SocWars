package com.soc.mixin;

import com.soc.events.ModEvents;
import net.minecraft.block.BedBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BedBlock.class)
public abstract class OnBedBrokenEventTrigger extends AbstractBlockBreak {
    @Inject(at = @At("HEAD"), method = "onBreak", cancellable = true)
	protected void socwars_onBedBreak(World world, BlockPos pos, BlockState state, PlayerEntity player, CallbackInfoReturnable<BlockState> cir) {
		if (player instanceof ServerPlayerEntity serverPlayer) {
			if (!ModEvents.ON_BED_BROKEN.invoker().onBedBreak(serverPlayer, pos)) {
				cir.setReturnValue(state);
			}
		} else {
			cir.setReturnValue(state);
		}
	}
}