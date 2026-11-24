package com.soc.renderer;

import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.entity.player.PlayerEntity;

public class RenderUtil {
	public static boolean shouldSeePlayerThroughMorph(EntityRenderDispatcher dispatcher, PlayerEntity thisEntity, PlayerEntity player) {
		return (player.isCreative() && (dispatcher.camera.isThirdPerson() || thisEntity != player)) || player.isSpectator();
	}
}
