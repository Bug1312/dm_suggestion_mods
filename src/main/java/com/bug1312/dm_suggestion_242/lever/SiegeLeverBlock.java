// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_242.lever;

import static net.minecraft.state.properties.BlockStateProperties.HORIZONTAL_FACING;
import static net.minecraft.state.properties.BlockStateProperties.OPEN;
import static net.minecraft.state.properties.BlockStateProperties.POWERED;

import java.util.Optional;
import java.util.function.BiFunction;

import com.bug1312.dm_suggestion_242.ITardisDataDuck;
import com.bug1312.dm_suggestion_242.Register;
import com.bug1312.dm_suggestion_242.SiegeData;
import com.swdteam.common.block.AbstractRotateableWaterLoggableBlock;
import com.swdteam.common.init.DMBlocks;
import com.swdteam.common.init.DMDimensions;
import com.swdteam.common.init.DMNBTKeys;
import com.swdteam.common.init.DMTardis;
import com.swdteam.common.tardis.Location;
import com.swdteam.common.tardis.TardisData;
import com.swdteam.common.tardis.actions.TardisActionList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class SiegeLeverBlock extends AbstractRotateableWaterLoggableBlock {

	private static final VoxelShape SHAPE_NS = VoxelShapes.or(VoxelShapes.box(0, 0, 0, 1, 2/16D, 1), VoxelShapes.box(3/16D, 2/16D, 2/16D, 13/16D, 10/16D, 14/16D));
	private static final VoxelShape SHAPE_EW = VoxelShapes.or(VoxelShapes.box(0, 0, 0, 1, 2/16D, 1), VoxelShapes.box(2/16D, 2/16D, 3/16D, 14/16D, 10/16D, 13/16D));

	public SiegeLeverBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(super.defaultBlockState().setValue(POWERED, false).setValue(OPEN, false));
	}
	
	@Override
	protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(POWERED, OPEN);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		switch (state.getValue(HORIZONTAL_FACING)) {
			default: case NORTH: case SOUTH: return SHAPE_NS;
			case WEST: case EAST: return SHAPE_EW;
		}
	}
	
	@Override
	public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, Direction direction) {
		return true;
	}
	
	@Override
	public boolean isSignalSource(BlockState p_149744_1_) {
		return true;
	}
	
	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, IWorld world, BlockPos pos, BlockPos neighborPos) {
		return direction == Direction.DOWN && !this.canSurvive(neighborState, world, pos) ? state.getFluidState().createLegacyBlock() : super.updateShape(state, direction, neighborState, world, pos, neighborPos);
	}
	
	@Override
	public boolean canSurvive(BlockState state, IWorldReader reader, BlockPos pos) {
		return canSupportCenter(reader, pos.below(), Direction.UP);
	}
	
	public BlockState toggleSwitch(BlockState state, World world, BlockPos pos) {
		state = state.cycle(POWERED);
		world.setBlock(pos, state, 3);
		this.updateNeighbours(state, world, pos);
		return state;
	}
	
	@Override
	public int getSignal(BlockState state, IBlockReader world, BlockPos pos, Direction direction) {
		return state.getValue(POWERED) ? 15 : 0;
	}
	
	@Override @SuppressWarnings("deprecation")
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean notify) {
		if (!notify && !state.is(newState.getBlock())) {
			if (state.getValue(POWERED)) {
				this.updateNeighbours(state, world, pos);
			}

			super.onRemove(state, world, pos, newState, notify);
		}
	}
	
	private void updateNeighbours(BlockState state, World world, BlockPos pos) {
		world.updateNeighborsAt(pos, this);
		world.updateNeighborsAt(pos.below(), this);
	}
	
	@Override @SuppressWarnings("deprecation")
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
		if (hand == Hand.OFF_HAND) return ActionResultType.CONSUME;
		
		if (player.getItemInHand(hand).isEmpty() && player.isShiftKeyDown()) {
			world.setBlockAndUpdate(pos, state.cycle(OPEN));
			return ActionResultType.sidedSuccess(world.isClientSide());
		} 
		
		if (state.getValue(OPEN)) {
			if (!world.isClientSide()) {
				if (world.dimension() == DMDimensions.TARDIS) {
					TardisData data = DMTardis.getTardisFromInteriorPos(pos);
					if (data != null) {
						if (((ITardisDataDuck) data).get().inSiege() != state.getValue(POWERED)) {
							world.setBlock(pos, state.cycle(POWERED), 0);
							return ActionResultType.CONSUME;
						}
						
						boolean flag = this.attemptSiegeSwap((ServerWorld) world, data);

						if (flag) {
							toggleSwitch(state, world, pos);
							return ActionResultType.sidedSuccess(world.isClientSide());
						} else player.displayClientMessage(Register.OBSTRUCTION.withStyle(TextFormatting.RED), true);
					}
					return ActionResultType.CONSUME;
				} else {
					toggleSwitch(state, world, pos);
					return ActionResultType.sidedSuccess(world.isClientSide());
				}
			}
			
			return ActionResultType.CONSUME;
		}
		
		return super.use(state, world, pos, player, hand, result);
	}
	
	private boolean attemptSiegeSwap(ServerWorld world, TardisData data) {
		SiegeData siegeData = ((ITardisDataDuck) data).get();
		if (!siegeData.inSiege()) {
			siegeData.setSiege(true);
			return true;
		}
	
		BiFunction<BlockPos, World, Boolean> setTardis = (placePos, placeWorld) -> {
			siegeData.setSiege(false);
			
			placeWorld.setBlock(placePos, DMBlocks.TARDIS.get().defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, world.getFluidState(placePos).getType() == Fluids.WATER), 0);
			TileEntity tile = placeWorld.getBlockEntity(placePos);
			if (tile != null) {
				CompoundNBT tag = tile.serializeNBT();
				tag.putInt(DMNBTKeys.TARDIS_ID, data.getGlobalID());
				tag.putFloat(DMNBTKeys.ROTATION, data.getCurrentLocation().getFacing());
				tile.deserializeNBT(tag);
			}

			return true;
		};
		
		if (!siegeData.isPlaced && siegeData.entityUUID == null && siegeData.holderUUID == null) {
			siegeData.setSiege(false);
			return true;
		}
		
		if (siegeData.isPlaced) {
			Location loc = data.getCurrentLocation();
			return setTardis.apply(loc.getBlockPosition(), world.getServer().getLevel(loc.dimensionWorldKey()));
		}

		Entity entity = Optional.ofNullable((Entity) siegeData.getHolder(world)).orElse(siegeData.getEntity(world)); 

		if (entity != null) {
			World placeWorld = entity.level;
			BlockPos placePos = entity.blockPosition();
			
			if (TardisActionList.canLandOnBlock(placeWorld, placePos.below())) {
				data.setPreviousLocation(data.getCurrentLocation());
				data.setCurrentLocation(placePos, placeWorld.dimension());
				data.getCurrentLocation().setFacing(entity.getYHeadRot() + 180);
				data.save();
				
				return setTardis.apply(placePos, placeWorld);
			}
		}
		
		return false;
	}

}
