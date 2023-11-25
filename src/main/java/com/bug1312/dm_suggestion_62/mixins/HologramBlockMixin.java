// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_62.mixins;

import static net.minecraft.state.properties.BlockStateProperties.LIT;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.swdteam.common.block.HologramBlock;
import com.swdteam.common.block.TileEntityBaseBlock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

@Mixin(HologramBlock.class)
public abstract class HologramBlockMixin extends TileEntityBaseBlock.WaterLoggable {
	private HologramBlockMixin(Supplier<TileEntity> tileEntitySupplier, int light) { super(tileEntitySupplier, light); }

	private boolean isPowered = false;
	
	@Inject(at = @At("TAIL"), method = "createBlockStateDefinition")
	protected void createBlockStateDefinition(final StateContainer.Builder<Block, BlockState> builder, final CallbackInfo ci) {
		builder.add(LIT);
	}
	
	@Inject(at = @At("RETURN"), method = "<init>*")
	public void constructor(final CallbackInfo ci) {
		this.registerDefaultState(this.defaultBlockState().setValue(LIT, true));
	}
	
	@Override
	public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
		return true;
	}
	
	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean notify) {
		if (!world.isClientSide) {
			boolean hasSignal = world.hasNeighborSignal(pos);
			if (hasSignal != isPowered) {
				this.isPowered = hasSignal;
				if (hasSignal) world.setBlockAndUpdate(pos, state.cycle(LIT));
			}
		}
	}
	
	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}
	
	@Override
	public int getAnalogOutputSignal(BlockState state, World world, BlockPos pos) {
		return state.getValue(LIT) ? 15 : 0;
	}
	
}
