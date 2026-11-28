package com.soc.mixin.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.soc.player.KeyBindings;
import net.minecraft.client.MinecraftClient;
import net.xolt.freecam.Freecam;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = KeyBindings.class, remap = false)
abstract class NoHoldSneakInFreecam {
	@WrapMethod(method = "lambda$static$4")
	private static void socwars$renderPlayersThroughMorphInFreecam(MinecraftClient client, Operation<Void> original) {
		if (!Freecam.isEnabled()) original.call(client);
	}
}
