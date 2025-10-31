package com.soc.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.state.StateManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

import static com.soc.blocks.util.DepositableProperty.DEPOSITABLE;

@Mixin(ChestBlock.class)
public abstract class ChestDepositableProperty extends Block {
	public ChestDepositableProperty(Settings settings) {
		super(settings);
	}

	@Inject(at = @At("TAIL"), method = "<init>")
	private void socwars_setDepositableProperty(Supplier<BlockEntityType<? extends ChestBlockEntity>> blockEntityTypeSupplier, Settings settings, CallbackInfo ci) {
		super.setDefaultState(super.getDefaultState().with(DEPOSITABLE, false));
	}

	@Inject(at = @At("HEAD"), method = "appendProperties")
	protected void socwars_addDepositableProperty(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci) {
		builder.add(DEPOSITABLE);
	}
}