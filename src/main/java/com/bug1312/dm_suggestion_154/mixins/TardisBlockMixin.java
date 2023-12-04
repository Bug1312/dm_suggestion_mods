// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_154.mixins;

import static net.minecraft.state.properties.BlockStateProperties.POWERED;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.swdteam.common.block.TileEntityBaseBlock;
import com.swdteam.common.block.tardis.TardisBlock;
import com.swdteam.common.tardis.TardisDoor;
import com.swdteam.common.tileentity.TardisTileEntity;
import com.swdteam.common.tileentity.TardisTileEntity.DoorSource;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(TardisBlock.class)
public class TardisBlockMixin extends TileEntityBaseBlock.WaterLoggable {
	public TardisBlockMixin(Supplier<TileEntity> tileEntitySupplier, Properties properties) { super(tileEntitySupplier, properties); }

	public TardisBlock _this = (TardisBlock) ((Object) this);
		
	@Inject(at = @At("RETURN"), method = "<init>*")
	public void constructor(final CallbackInfo ci) {
		this.registerDefaultState(_this.defaultBlockState().setValue(POWERED, Boolean.valueOf(false)));
	}
	
	@Override
	public void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(POWERED);
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean notify) {
		boolean powered = world.hasNeighborSignal(pos);
		if (neighborBlock != this && powered != state.getValue(POWERED)) {			
			if (!world.isClientSide()) {
				TileEntity te = world.getBlockEntity(pos);
				if (te != null && te instanceof TardisTileEntity) {
					TardisTileEntity tile = (TardisTileEntity) te;	
					tile.setDoor(TardisDoor.BOTH, powered, DoorSource.TARDIS);
				}
			}
		
			world.setBlock(pos, state.setValue(POWERED, powered), 2);
		}
	}
}
