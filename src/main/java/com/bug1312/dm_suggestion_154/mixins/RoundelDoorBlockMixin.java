// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_154.mixins;

import static com.swdteam.common.block.tardis.RoundelDoorBlock.DOOR_PART;
import static net.minecraft.state.properties.BlockStateProperties.POWERED;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.swdteam.common.block.RotatableTileEntityBase;
import com.swdteam.common.block.tardis.RoundelDoorBlock;
import com.swdteam.common.init.DMBlocks;
import com.swdteam.common.init.DMDimensions;
import com.swdteam.common.init.DMFlightMode;
import com.swdteam.common.init.DMTardis;
import com.swdteam.common.tardis.TardisData;
import com.swdteam.common.tileentity.tardis.RoundelDoorTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(RoundelDoorBlock.class)
public class RoundelDoorBlockMixin extends RotatableTileEntityBase.WaterLoggable {
	public RoundelDoorBlockMixin(Supplier<TileEntity> tileEntitySupplier, Properties properties) { super(tileEntitySupplier, properties); }

	public RoundelDoorBlock _this = (RoundelDoorBlock) ((Object) this);
	
	@Inject(at = @At("RETURN"), method = "<init>*")
	public void constructor(final CallbackInfo ci) {
		this.registerDefaultState(_this.defaultBlockState().setValue(POWERED, Boolean.valueOf(false)));
	}
	
	@Inject(at = @At("TAIL"), method = "createBlockStateDefinition")
	public void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder, CallbackInfo ci) {
		builder.add(POWERED);
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean isMoving) {
		List<BlockPos> otherPos = this.getOtherPos(state, world, pos);
		boolean powered = otherPos.stream().anyMatch(bpos -> world.hasNeighborSignal(bpos));
	
		if (neighborBlock != this && powered != state.getValue(POWERED)) {		
			for (BlockPos bpos : otherPos) {
				BlockState oldState = world.getBlockState(bpos);
				if (oldState.getBlock() == DMBlocks.ROUNDEL_DOOR.get()) {
					world.setBlock(bpos, oldState.setValue(POWERED, powered), 2);
				}
			}
			
			if (!world.isClientSide()) {
				Runnable toggleDoor = () -> {
					for (BlockPos bpos : otherPos) {
						TileEntity te = world.getBlockEntity(bpos);
						if (te != null && te instanceof RoundelDoorTileEntity) {
							RoundelDoorTileEntity tile = (RoundelDoorTileEntity) te;
							tile.setOpen(powered, !tile.isMainDoor());
						}
					}
				};

				if (world.dimension() == DMDimensions.TARDIS) {
					TardisData data = DMTardis.getTardisFromInteriorPos(pos);
					if (data != null && !data.isLocked() && !DMFlightMode.isInFlight(data.getGlobalID())) toggleDoor.run();
				} else toggleDoor.run();
			}

		}
	}

	public List<BlockPos> getOtherPos(BlockState state, World world, BlockPos pos) {
		switch (state.getValue(DOOR_PART)) {
			default:
			case 0: return Arrays.asList(pos, pos.above(), pos.above(2)); 		
			case 1: return Arrays.asList(pos, pos.above(), pos.below()); 
			case 2: return Arrays.asList(pos, pos.below(), pos.below(2)); 
		}
	}
	
}
