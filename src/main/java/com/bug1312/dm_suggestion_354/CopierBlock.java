// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_354;

import static com.swdteam.common.block.tardis.DataWriterBlock.CARTRIDGE_TYPE;

import com.swdteam.common.block.RotatableTileEntityBase;
import com.swdteam.common.init.DMItems;
import com.swdteam.common.item.DataModuleItem;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class CopierBlock extends RotatableTileEntityBase.WaterLoggable  {

	public static final VoxelShape SHAPE_N = VoxelShapes.or(VoxelShapes.box(0, 0, 5/16D, 1, 1, 1), VoxelShapes.box(0, 0, 0, 1, 11/16D, 5/16D), VoxelShapes.box(0, 11/16D, 2/16D, 1, 14/16D, 5/16D));
	public static final VoxelShape SHAPE_E = VoxelShapes.or(VoxelShapes.box(0, 0, 0, 11/16D, 1, 1), VoxelShapes.box(11/16D, 0, 0, 1, 11/16D, 1), VoxelShapes.box(11/16D, 11/16D, 0, 14/16D, 14/16D, 1));
	public static final VoxelShape SHAPE_S = VoxelShapes.or(VoxelShapes.box(0, 0, 0, 1, 1, 11/16D), VoxelShapes.box(0, 0, 11/16D, 1, 11/16D, 1), VoxelShapes.box(0, 11/16D, 11/16D, 1, 14/16D, 14/16D));
	public static final VoxelShape SHAPE_W = VoxelShapes.or(VoxelShapes.box(5/16D, 0, 0, 1, 1, 1), VoxelShapes.box(0, 0, 0, 5/16D, 11/16D, 1), VoxelShapes.box(2/16D, 11/16D, 0, 5/16D, 14/16D, 1));
	
	public CopierBlock(Properties properties) {
		super(CopierTile::new, properties);
		this.registerDefaultState(this.defaultBlockState().setValue(CARTRIDGE_TYPE, 0));
	}
	
	@Override
	public void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(CARTRIDGE_TYPE);
	}

	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.MODEL;
	}
	
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		switch (state.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
			default:
			case NORTH:	return SHAPE_N;
			case EAST: 	return SHAPE_E;
			case SOUTH:	return SHAPE_S;
			case WEST: 	return SHAPE_W;
		}
	}

		
	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
		ItemStack stack = player.getItemInHand(hand);
		if (stack.getItem() instanceof DataModuleItem) {
			if (state.getValue(CARTRIDGE_TYPE) != 0 && stack.getItem() == DMItems.DATA_MODULE.get() && stack.getOrCreateTag().getBoolean("written")) return ActionResultType.CONSUME;
			
			if (!world.isClientSide()) {
				TileEntity te = world.getBlockEntity(pos);
				if (te instanceof CopierTile) {
					CopierTile tile = (CopierTile) te;
					if (tile.getItem(0).isEmpty()) {
						ItemStack inputModule = stack.split(1);
						if (player.abilities.instabuild) stack.grow(1);
						
						tile.setItem(0, inputModule);
						world.setBlockAndUpdate(pos, state.setValue(CARTRIDGE_TYPE, inputModule.getItem() == DMItems.DATA_MODULE.get() ? 1 : 2));
					} else {
						ItemStack newModule = stack.split(1);
						if (player.abilities.instabuild) stack.grow(1);
						
						newModule.setTag(tile.getItem(0).getOrCreateTag());
						
						if (stack.isEmpty()) player.setItemInHand(hand, newModule);
						else player.addItem(newModule);
					}
				}
			}
			return ActionResultType.sidedSuccess(true);
		} else if (stack.isEmpty()) {
			if (!world.isClientSide()) {
				TileEntity te = world.getBlockEntity(pos);
				if (te instanceof CopierTile) {
					CopierTile tile = (CopierTile) te;
					if (!tile.getItem(0).isEmpty()) {
						player.setItemInHand(hand, tile.getItem(0).split(1));
						world.setBlockAndUpdate(pos, state.setValue(CARTRIDGE_TYPE, 0));
					}
				}
			}
			return ActionResultType.sidedSuccess(true);
		}
		return ActionResultType.PASS;
	}
	
	@Override @SuppressWarnings("deprecation")
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState state2, boolean bool) {
		if (!state.is(state2.getBlock())) {
			TileEntity te = world.getBlockEntity(pos);
			if (te instanceof CopierTile) InventoryHelper.dropContents(world, pos, (CopierTile) te);			
		}
		
		super.onRemove(state, world, pos, state2, bool);
	}
	
}
