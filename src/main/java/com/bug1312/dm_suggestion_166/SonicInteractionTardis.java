// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_166;

import com.swdteam.common.init.DMDimensions;
import com.swdteam.common.init.DMSonicRegistry;
import com.swdteam.common.init.DMSoundEvents;
import com.swdteam.common.sonic.SonicCategory;
import com.swdteam.common.tardis.TardisData;
import com.swdteam.common.tardis.TardisState;
import com.swdteam.common.tardis.actions.TardisActionList;
import com.swdteam.common.tardis.data.TardisFlightPool;
import com.swdteam.common.tileentity.TardisTileEntity;

import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class SonicInteractionTardis implements DMSonicRegistry.ISonicInteraction {

	@Override
	public void interact(World world, PlayerEntity player, ItemStack stack, Object entityOrPos) {
		if (entityOrPos instanceof BlockPos && !world.isClientSide()) {
			BlockPos pos = (BlockPos) entityOrPos;
	
			TileEntity te = world.getBlockEntity(pos);
			if (te instanceof TardisTileEntity) {
				TardisData data = ((TardisTileEntity) te).tardisData;
				
				if (data != null) {
					MinecraftServer server = world.getServer();
					ServerWorld interiorDim = server.getLevel(DMDimensions.TARDIS);
	
					// If interior is loaded, play demat noise
					if (interiorDim.isLoaded(data.getInteriorSpawnPosition().toBlockPos())) {
						interiorDim.playSound(null, data.getInteriorSpawnPosition().toBlockPos(), DMSoundEvents.TARDIS_DEMAT.get(), SoundCategory.BLOCKS, 1, 1);
					}
	
					BlockPos currentPos = data.getCurrentLocation().getBlockPosition();
					ServerWorld currentDim = server.getLevel(data.getCurrentLocation().dimensionWorldKey());
					TileEntity currentTile = currentDim.getBlockEntity(currentPos);
					if (currentTile instanceof TardisTileEntity) {
						if (TardisActionList.doAnimation(currentDim, currentPos)) {
							currentDim.playSound(null, currentPos, DMSoundEvents.TARDIS_DEMAT.get(), SoundCategory.BLOCKS, 1, 1);
							((TardisTileEntity) currentTile).setState(TardisState.DEMAT);
						} else currentDim.setBlockAndUpdate(currentPos, Blocks.AIR.defaultBlockState());
					}
	
					data.setPreviousLocation(data.getCurrentLocation());
					TardisFlightPool.addFlight(data);
				}
			}
		}
	}
	
	@Override
	public int scanTime() { return 10; }

	@Override
	public boolean disableDefaultInteraction(World var1, PlayerEntity var2, ItemStack var3, Object var4) { return true; }

	@Override
	public SonicCategory getCategory() { return SonicCategory.REDSTONE; }

}
