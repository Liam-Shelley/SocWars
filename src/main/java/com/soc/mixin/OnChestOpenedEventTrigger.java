package com.soc.mixin;

import com.soc.events.ModEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChestBlock.class)
public abstract class OnChestOpenedEventTrigger {
    @Inject(at = @At("HEAD"), method = "onUse", cancellable = true)
    public void socwars_onChestOpened(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (world.isClient) return;
        if (!ModEvents.ON_CHEST_OPENED.invoker().onChestOpen((ServerPlayerEntity)player, pos)) cir.setReturnValue(ActionResult.FAIL);
    }
}