/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2020-2021 TheRandomLabs
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

//Code taken and adapted from TooFast (https://modrinth.com/mod/too-fast) which itself was derived from RandomPatches (https://github.com/TheRandomLabs/RandomPatches/blob/1.16-forge/src/main/java/com/therandomlabs/randompatches/mixin/ServerPlayNetHandlerPlayerSpeedLimitsMixin.java)

package com.soc.mixin;

import net.minecraft.server.network.ServerPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class TooFastPort {
	@ModifyConstant(method = "onPlayerMove", constant = @Constant(floatValue = 100f))
	private float getDefaultMaxPlayerSpeed(float speed) {
		return 1000f;
	}

	@ModifyConstant(method = "onPlayerMove", constant = @Constant(floatValue = 300f))
	private float getMaxPlayerElytraSpeed(float speed) {
		return 3000f;
	}

	@ModifyConstant(method = "onVehicleMove", constant = @Constant(doubleValue = 100d))
	private double getMaxPlayerVehicleSpeed(double speed) {
		return 100f;
	}
}