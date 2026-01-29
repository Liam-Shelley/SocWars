package com.soc.mixin;

import net.minecraft.block.*;
import net.minecraft.state.StateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.soc.blocks.util.DepositableProperty.DEPOSITABLE;

@Mixin(EnderChestBlock.class)
public abstract class EnderChestDepositableProperty extends Block {
	public EnderChestDepositableProperty(Settings settings) {
		super(settings);
	}

	@Inject(at = @At("TAIL"), method = "<init>")
	private void socwars_setDepositableProperty(Settings settings, CallbackInfo ci) {
		super.setDefaultState(super.getDefaultState().with(DEPOSITABLE, false));
	}

	@Inject(at = @At("HEAD"), method = "appendProperties")
	protected void socwars_addDepositableProperty(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
		builder.add(DEPOSITABLE);
	}
}