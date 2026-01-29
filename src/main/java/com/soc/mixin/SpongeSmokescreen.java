package com.soc.mixin;

import com.soc.networking.s2c.SmokescreenPayload;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.BlockState;
import net.minecraft.block.SpongeBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SpongeBlock.class)
public abstract class SpongeSmokescreen {
	@Inject(method = "onBlockAdded", at = @At("HEAD"))
	private void socwars_spongeSmokescreen(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify, CallbackInfo ci) {
		PlayerLookup.tracking((ServerWorld) world, pos).forEach(player -> ServerPlayNetworking.send(player, new SmokescreenPayload(pos)));
	}
}