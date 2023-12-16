// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_379;

import com.swdteam.common.block.RoundelBlock;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class RedstoneRoundelBlock extends RoundelBlock {
	public RedstoneRoundelBlock(AbstractBlock.Properties properties) { super(properties); }
	public boolean isSignalSource(BlockState state) { return true; }
	public int getSignal(BlockState state, IBlockReader world, BlockPos pos, Direction dir) { return 15; }
}
