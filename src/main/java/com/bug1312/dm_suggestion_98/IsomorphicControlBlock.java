// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_98;

import static net.minecraft.block.HorizontalBlock.FACING;
import static net.minecraft.state.properties.BlockStateProperties.POWERED;

import com.bug1312.dm_suggestion_98.data.ITardisDataDuck;
import com.bug1312.dm_suggestion_98.data.Permissions;
import com.bug1312.dm_suggestion_98.screen.PermissionScreen;
import com.swdteam.client.tardis.data.ClientTardisCache;
import com.swdteam.common.init.DMDimensions;
import com.swdteam.common.init.DMTardis;
import com.swdteam.common.tardis.TardisData;
import com.swdteam.common.tardis.TardisData.PermissionType;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

public class IsomorphicControlBlock extends Block {

	public IsomorphicControlBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.defaultBlockState().setValue(FACING, Direction.NORTH).setValue(POWERED, false));
	}
	
	@Override @SuppressWarnings("deprecation")
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
		if (world.dimension() == DMDimensions.TARDIS) {
			if (world.isClientSide()) {
				TardisData data = ClientTardisCache.getTardisData(pos);
				if (data != null && data.hasPermission(player, PermissionType.DEFAULT)) Minecraft.getInstance().setScreen(new PermissionScreen());
				else player.displayClientMessage(Register.NOT_ALLOWED.withStyle(TextFormatting.YELLOW), true);
			}
			return ActionResultType.CONSUME;
		}

		return super.use(state, world, pos, player, hand, result);
	}
	
	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(FACING, POWERED);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return super.getStateForPlacement(context).setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
	}
	
	public BlockState rotate(BlockState state, Rotation rotation) {
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@SuppressWarnings("deprecation")
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}
	
	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean notify) {
		boolean powered = world.hasNeighborSignal(pos);
		if (neighborBlock != this && powered != state.getValue(POWERED)) {
			world.setBlock(pos, state.setValue(POWERED, powered), 2);
		
			if (!world.isClientSide() && world.dimension() == DMDimensions.TARDIS) {
				TardisData data = DMTardis.getTardisFromInteriorPos(pos);
				if (data != null) {
					addActive(((ITardisDataDuck) data).get(), powered);
					data.save();
				}
			}
		}
	}
	
	@Override @SuppressWarnings("deprecation")
	public void onPlace(BlockState state, World world, BlockPos pos, BlockState newState, boolean notify) {
		if (!newState.is(state.getBlock()) && !world.isClientSide() && world.dimension() == DMDimensions.TARDIS) {
			TardisData data = DMTardis.getTardisFromInteriorPos(pos);
			if (data != null) {
				if (state.getValue(POWERED)) {
					addActive(((ITardisDataDuck) data).get(), true);
					data.save();
				}
			}
		}
		super.onPlace(state, world, pos, newState, notify);
	}
	
	@Override @SuppressWarnings("deprecation")
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean notify) {
		if (!newState.is(state.getBlock()) && !world.isClientSide() && world.dimension() == DMDimensions.TARDIS) {
			TardisData data = DMTardis.getTardisFromInteriorPos(pos);
			if (data != null) {
				if (state.getValue(POWERED)) {
					addActive(((ITardisDataDuck) data).get(), false);
					data.save();
				}			
			}
		}
		super.onRemove(state, world, pos, newState, notify);
	}
	
	public void addActive(Permissions perms, boolean add) {
		if (add) perms.activeBlocks++;
		else perms.activeBlocks--;
	}

}
