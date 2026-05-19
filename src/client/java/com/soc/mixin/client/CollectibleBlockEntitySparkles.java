package com.soc.mixin.client;

import com.soc.blocks.blockentities.CollectibleBlockEntity;
import com.soc.player.ClientPlayerDataManager;
import net.minecraft.block.BlockState;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.soc.lib.SocWarsLib.randomCentredVec3d;

@Mixin(value = CollectibleBlockEntity.class, remap = false)
abstract class CollectibleBlockEntitySparkles {
	@Inject(method = "clientTick", at = @At("HEAD"))
	private static void socwars_collectibleBlockENtiySparkles(World world, BlockPos pos, BlockState blockState, CollectibleBlockEntity blockEntity, CallbackInfo ci) {
		if (world.random.nextFloat() < 0.25f && !ClientPlayerDataManager.hasCollectibleClient(blockEntity.getId())) {
			final Vec3d offset = randomCentredVec3d(world.random, 0.5d);
			final Vec3d centrePos = pos.toCenterPos().add(offset);
			world.addParticleClient(ParticleTypes.HAPPY_VILLAGER, centrePos.x, centrePos.y, centrePos.z, offset.z * 0.05d, offset.x * 0.05d, offset.y * 0.05d);
		}
	}
}
