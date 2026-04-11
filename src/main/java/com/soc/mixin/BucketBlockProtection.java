package com.soc.mixin;

import com.soc.game.manager.GamesManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BucketItem.class)
abstract class BucketBlockProtection {
    @Inject(method = "placeFluid", at = @At("HEAD"), cancellable = true)
    private void socwars_bucketBlockProtection(LivingEntity user, World world, BlockPos pos, BlockHitResult hitResult, CallbackInfoReturnable<Boolean> cir) {
        if (GamesManager.getInstance().getGame(user).map(manager -> manager.isBlockProtected(pos, world.getBlockState(pos))).orElse(false)) cir.setReturnValue(false);
    }
}
