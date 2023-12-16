// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_206;

import com.swdteam.common.init.DMItemTiers;

import net.minecraft.block.AbstractButtonBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.TieredItem;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class PlungerButtonBlock extends AbstractButtonBlock {
	private static final VoxelShape CEILING_SHAPE = VoxelShapes.or(VoxelShapes.box(2/16D, 15/16D, 2/16D, 14/16D, 1, 14/16D), VoxelShapes.box(4/16D, 11/16D, 4/16D, 12/16D, 15/16D, 12/16D));
	private static final VoxelShape FLOOR_SHAPE = VoxelShapes.or(VoxelShapes.box(2/16D, 0, 2/16D, 14/16D, 1/16D, 14/16D), VoxelShapes.box(4/16D, 1/16D, 4/16D, 12/16D, 5/16D, 12/16D));
	private static final VoxelShape NORTH_SHAPE = VoxelShapes.or(VoxelShapes.box(2/16D, 2/16D, 15/16D, 14/16D, 14/16D, 1), VoxelShapes.box(4/16D, 4/16D, 11/16D, 12/16D, 12/16D, 15/16D));
	private static final VoxelShape EAST_SHAPE = VoxelShapes.or(VoxelShapes.box(0, 2/16D, 2/16D, 1/16D, 14/16D, 14/16D), VoxelShapes.box(1/16D, 4/16D, 4/16D, 5/16D, 12/16D, 12/16D));
	private static final VoxelShape SOUTH_SHAPE = VoxelShapes.or(VoxelShapes.box(2/16D, 2/16D, 0, 14/16D, 14/16D, 1/16D), VoxelShapes.box(4/16D, 4/16D, 1/16D, 12/16D, 12/16D, 5/16D));
	private static final VoxelShape WEST_SHAPE = VoxelShapes.or(VoxelShapes.box(15/16D, 2/16D, 2/16D, 1, 14/16D, 14/16D), VoxelShapes.box(11/16D, 4/16D, 4/16D, 15/16D, 12/16D, 12/16D));
	
	public PlungerButtonBlock(Properties properties) {
		super(true, properties);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		Direction direction = state.getValue(FACING);
		
		switch (state.getValue(FACE)) {
			default: 
			case CEILING: 	return CEILING_SHAPE;
			case FLOOR: 	return FLOOR_SHAPE;
			case WALL:
				switch (direction) {
					default:
					case NORTH:	return NORTH_SHAPE;
					case EAST: 	return EAST_SHAPE;
					case SOUTH:	return SOUTH_SHAPE;
					case WEST: 	return WEST_SHAPE;
				}
			}
	}

	@Override
	protected SoundEvent getSound(boolean turningOn) {
		return turningOn ? ModMain.BUTTON_NOISE.get() : null;
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
		Item item = player.getItemInHand(hand).getItem();
		if (item instanceof TieredItem && ((TieredItem) item).getTier() == DMItemTiers.DALEK_PLUNGER) super.use(state, world, pos, player, hand, result);
		return ActionResultType.CONSUME;
	}

}
