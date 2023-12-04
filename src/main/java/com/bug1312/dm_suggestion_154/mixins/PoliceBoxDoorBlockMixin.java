// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_154.mixins;

import static net.minecraft.state.properties.BlockStateProperties.DOUBLE_BLOCK_HALF;
import static net.minecraft.state.properties.BlockStateProperties.POWERED;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.swdteam.common.block.RotatableTileEntityBase;
import com.swdteam.common.block.tardis.PoliceBoxDoorBlock;
import com.swdteam.common.init.DMDimensions;
import com.swdteam.common.init.DMFlightMode;
import com.swdteam.common.init.DMTardis;
import com.swdteam.common.tardis.TardisData;
import com.swdteam.common.tardis.TardisDoor;
import com.swdteam.common.tileentity.tardis.PoliceBoxDoorsTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(PoliceBoxDoorBlock.class)
public class PoliceBoxDoorBlockMixin extends RotatableTileEntityBase.WaterLoggable {
	public PoliceBoxDoorBlockMixin(Supplier<TileEntity> tileEntitySupplier, Properties properties) { super(tileEntitySupplier, properties); }

	public PoliceBoxDoorBlock _this = (PoliceBoxDoorBlock) ((Object) this);
		
	@Inject(at = @At("RETURN"), method = "<init>*")
	public void constructor(final CallbackInfo ci) {
		this.registerDefaultState(_this.defaultBlockState().setValue(POWERED, Boolean.valueOf(false)));
	}
	
	@Inject(at = @At("TAIL"), method = "createBlockStateDefinition")
	public void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder, CallbackInfo ci) {
		builder.add(POWERED);
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean notify) {
		boolean powered = world.hasNeighborSignal(pos) || world.hasNeighborSignal(pos.relative(state.getValue(DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN));
		if (neighborBlock != this && powered != state.getValue(POWERED)) {			
			if (!world.isClientSide()) {
				TileEntity te = world.getBlockEntity(pos);
				if (state.getValue(DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER) te = world.getBlockEntity(pos.below());
				
				if (te != null && te instanceof PoliceBoxDoorsTileEntity) {
					PoliceBoxDoorsTileEntity tile = (PoliceBoxDoorsTileEntity) te;
					Runnable toggleDoor = () -> { tile.setOpen(TardisDoor.BOTH, powered, !tile.isMainDoor()); };
					
					if (world.dimension() == DMDimensions.TARDIS) {
						TardisData data = DMTardis.getTardisFromInteriorPos(pos);
						if (data != null && !data.isLocked() && !DMFlightMode.isInFlight(data.getGlobalID())) toggleDoor.run();
					} else toggleDoor.run();
				}
			}
		
			world.setBlock(pos, state.setValue(POWERED, powered), 2);
		}
	}

}
