// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_198.mixins;

import static com.swdteam.common.block.AbstractLeverBlock.PULLED;

import org.spongepowered.asm.mixin.Mixin;

import com.swdteam.common.block.AbstractLeverBlock;
import com.swdteam.common.block.AbstractRotateableWaterLoggableBlock;

import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

@Mixin(AbstractLeverBlock.class)
public class AbstractLeverBlockMixin extends AbstractRotateableWaterLoggableBlock{
	public AbstractLeverBlockMixin(Properties properties) {
		super(properties);
	}

	@SuppressWarnings("deprecation")
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
		if (!moved && !state.is(newState.getBlock()) && state.getValue(PULLED)) this.updateNeighbours(state, world, pos);
		if (!moved && state.is(newState.getBlock())) this.updateNeighbours(state, world, pos);
		
		super.onRemove(state, world, pos, newState, moved);
	}

	public boolean isSignalSource(BlockState state) { return true; }

	public int getSignal(BlockState state, IBlockReader world, BlockPos pos, Direction direction) {
		return state.getValue(PULLED) ? 15 : 0;
	}

	public int getDirectSignal(BlockState state, IBlockReader world, BlockPos pos, Direction direction) {
		return state.getValue(PULLED) && direction == Direction.UP ? 15 : 0;
	}

	private void updateNeighbours(BlockState state, World world, BlockPos pos) {
		world.updateNeighborsAt(pos, this);
		world.updateNeighborsAt(pos.relative(Direction.DOWN), this);
	}
	
}
