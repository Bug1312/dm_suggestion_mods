// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_242.tardis;

import com.bug1312.dm_suggestion_242.ITardisDataDuck;
import com.bug1312.dm_suggestion_242.Register;
import com.bug1312.dm_suggestion_242.SiegeData;
import com.swdteam.common.block.TileEntityBaseBlock;
import com.swdteam.common.init.DMNBTKeys;
import com.swdteam.common.init.DMTardis;
import com.swdteam.common.tardis.TardisData;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
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

public class SiegeTardisBlock extends TileEntityBaseBlock.WaterLoggable {

	private static final VoxelShape SHAPE = VoxelShapes.box(4/16D, 0, 4/16D, 12/16D, 8/16D, 12/16D);
	
	public SiegeTardisBlock(Properties properties) {
		super(SiegeTardisTileEntity::new, properties);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}
	
	@Override
	public BlockRenderType getRenderShape(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
		if (hand == Hand.MAIN_HAND && !world.isClientSide() && player.getItemInHand(hand).isEmpty()) {
			TileEntity tile = world.getBlockEntity(pos);
			if (tile != null) {
				CompoundNBT tileTag = tile.serializeNBT();
				if (tileTag.contains(DMNBTKeys.TARDIS_ID)) {
					int id = tileTag.getInt(DMNBTKeys.TARDIS_ID);
					TardisData data = DMTardis.getTardis(id);
					SiegeData siegeData = ((ITardisDataDuck) data).get();
					if (siegeData != null) {						
						ItemStack siegeStack = new ItemStack(Register.SIEGE_ITEM.get());
						CompoundNBT stackTag = siegeStack.getOrCreateTag();
						stackTag.putInt(DMNBTKeys.TARDIS_ID, id);
						siegeStack.setTag(stackTag);
						
						player.setItemInHand(hand, siegeStack);
						siegeData.setPlacement(player);
						
						world.setBlockAndUpdate(pos, state.getFluidState().createLegacyBlock());
						return ActionResultType.sidedSuccess(world.isClientSide());
					}
				}
			}
		}
		return ActionResultType.FAIL;
	}
}
