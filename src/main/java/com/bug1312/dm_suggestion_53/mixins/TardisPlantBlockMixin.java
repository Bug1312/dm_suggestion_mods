// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_53.mixins;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;

import com.swdteam.common.block.tardis.TardisPlantBlock;
import com.swdteam.common.tileentity.tardis.TardisPlantTileEntity;

import net.minecraft.block.BlockState;
import net.minecraft.block.IGrowable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

@Mixin(TardisPlantBlock.class)
public class TardisPlantBlockMixin implements IGrowable {

	@Override
	public boolean isValidBonemealTarget(IBlockReader world, BlockPos pos, BlockState state, boolean bool) {
		return true;
	}

	@Override
	public boolean isBonemealSuccess(World world, Random rand, BlockPos pos, BlockState state) {
		return rand.nextInt(5) == 0;
	}

	@Override
	public void performBonemeal(ServerWorld world, Random rand, BlockPos pos, BlockState state) {
		TileEntity tile = world.getBlockEntity(pos);
		if (tile != null || tile instanceof TardisPlantTileEntity) ((TardisPlantTileEntity) tile).addAge();
	}

}
