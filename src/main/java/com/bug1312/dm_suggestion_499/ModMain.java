// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_499;

import com.swdteam.common.init.DMBlocks;
import com.swdteam.common.init.DMDimensions;
import com.swdteam.common.init.DMItems;
import com.swdteam.common.init.DMNBTKeys;
import com.swdteam.common.init.DMSoundEvents;
import com.swdteam.common.init.DMTardis;
import com.swdteam.common.tardis.TardisData;
import com.swdteam.common.tardis.TardisDoor;
import com.swdteam.common.tardis.TardisState;
import com.swdteam.common.tardis.actions.TardisActionList;
import com.swdteam.common.tardis.data.TardisFlightPool;
import com.swdteam.common.tileentity.TardisTileEntity;

import net.minecraft.block.Blocks;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.DirectionalPlaceContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ModMain.MOD_ID)
public class ModMain {
	public static final String MOD_ID = "dm_suggestion_499";

	public ModMain() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::common);
	}
	
	public void common(final FMLCommonSetupEvent event) {
		DispenserBlock.registerBehavior(DMItems.STATTENHEIM_REMOTE.get(), new DefaultDispenseItemBehavior() {
			public ItemStack execute(IBlockSource dispenser, ItemStack stack) {
				Direction direction = dispenser.getBlockState().getValue(DispenserBlock.FACING);
				BlockPos pos = dispenser.getPos().relative(direction);
				ServerWorld newDim = dispenser.getLevel();

				if (stack.getOrCreateTag().contains(DMNBTKeys.LINKED_ID)) {
					if (newDim.getBlockState(pos).canBeReplaced(new DirectionalPlaceContext(newDim, pos, Direction.NORTH, stack, Direction.NORTH))) {
						int id = stack.getOrCreateTag().getInt(DMNBTKeys.LINKED_ID);
						TardisData data = DMTardis.getTardis(id);
						
						if (data != null) {
							MinecraftServer server = newDim.getServer();
							ServerWorld interiorDim = server.getLevel(DMDimensions.TARDIS);

							// If interior is loaded, play demat noise
							if (interiorDim.isLoaded(data.getInteriorSpawnPosition().toBlockPos())) {
								interiorDim.playSound(null, data.getInteriorSpawnPosition().toBlockPos(), DMSoundEvents.TARDIS_DEMAT.get(), SoundCategory.BLOCKS, 1, 1);
							}

							// If TARDIS is landed, remove it
							if (!data.isInFlight()) {
								BlockPos currentPos = data.getCurrentLocation().getBlockPosition();
								ServerWorld currentDim = server.getLevel(data.getCurrentLocation().dimensionWorldKey());
								TileEntity currentTile = currentDim.getBlockEntity(currentPos);
								if (currentTile instanceof TardisTileEntity) {
									if (TardisActionList.doAnimation(currentDim, currentPos)) {
										currentDim.playSound(null, currentPos, DMSoundEvents.TARDIS_DEMAT.get(), SoundCategory.BLOCKS, 1, 1);
										((TardisTileEntity) currentTile).setState(TardisState.DEMAT);
									}
									else currentDim.setBlockAndUpdate(currentPos, Blocks.AIR.defaultBlockState());
								}
							}

							// Set new block
							TardisFlightPool.completeFlight(server, data);
							newDim.setBlockAndUpdate(pos, DMBlocks.TARDIS.get().defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, newDim.getBlockState(pos).getBlock() instanceof FlowingFluidBlock));

							// Set new data
							data.setPreviousLocation(data.getCurrentLocation());
							data.setCurrentLocation(pos, newDim.dimension());

							// Set new tile
							TileEntity newTile = newDim.getBlockEntity(pos);
							if (newTile instanceof TardisTileEntity) {
								TardisTileEntity tardis = (TardisTileEntity) newTile;
								tardis.globalID = id;
								tardis.rotation = dispenser.getBlockState().getValue(DispenserBlock.FACING).getOpposite().toYRot();
								data.getCurrentLocation().setFacing(tardis.rotation);
								tardis.closeDoor(TardisDoor.BOTH, TardisTileEntity.DoorSource.TARDIS);
								tardis.setState(TardisState.REMAT);
							}

							data.save();

							if (stack.hurt(1, newDim.random, null)) stack.shrink(1);
							newDim.playSound(null, pos, DMSoundEvents.ENTITY_STATTENHEIM_REMOTE_SUMMON.get(), SoundCategory.BLOCKS, 1, 1);
						}
					}
				} else return super.execute(dispenser, stack);
				
				return stack;
			}
		});
	}

}