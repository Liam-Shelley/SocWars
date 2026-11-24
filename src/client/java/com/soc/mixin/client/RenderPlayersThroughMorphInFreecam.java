package com.soc.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.soc.renderer.RenderUtil;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.player.PlayerEntity;
import net.xolt.freecam.Freecam;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = RenderUtil.class, remap = false)
abstract class RenderPlayersThroughMorphInFreecam {
	@WrapMethod(method = "shouldSeePlayerThroughMorph")
	private static boolean socwars$renderPlayersThroughMorphInFreecam(EntityRenderDispatcher dispatcher, PlayerEntity thisEntity, PlayerEntity player, Operation<Boolean> original) {
		return original.call(dispatcher, thisEntity, player) || Freecam.isEnabled();
	}
}
