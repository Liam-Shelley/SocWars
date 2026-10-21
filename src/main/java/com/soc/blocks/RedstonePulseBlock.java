package com.soc.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;

public class RedstonePulseBlock extends Block {
	public static final BooleanProperty POWERED = Properties.POWERED;

	public RedstonePulseBlock(Settings settings) {
		super(settings);
		this.setDefaultState(this.getDefaultState().with(POWERED, false));
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(POWERED);
	}

	@Override
	protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		world.setBlockState(pos, state.with(POWERED, false), 2);

		world.updateNeighbors(pos, this);
	}

	private void scheduleTick(World world, ScheduledTickView tickView, BlockPos pos) {
		world.setBlockState(pos, world.getBlockState(pos).with(POWERED, false), 2);

		if (!world.isClient) {
			tickView.scheduleBlockTick(pos, this, 2);
		}
	}

	@Override
	protected boolean emitsRedstonePower(BlockState state) {
		return true;
	}

	@Override
	protected int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
		return state.get(POWERED) ? 15 : 0;
	}
}
