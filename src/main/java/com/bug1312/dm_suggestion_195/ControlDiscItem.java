// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_195;

import java.util.List;

import com.swdteam.common.init.DMBlocks;
import com.swdteam.common.init.DMDimensions;
import com.swdteam.common.init.DMNBTKeys;
import com.swdteam.common.init.DMSoundEvents;
import com.swdteam.common.init.DMTardis;
import com.swdteam.common.tardis.TardisData;
import com.swdteam.common.tardis.TardisDoor;
import com.swdteam.common.tardis.TardisState;
import com.swdteam.common.tardis.actions.TardisActionList;
import com.swdteam.common.tardis.data.TardisFlightPool;
import com.swdteam.common.tileentity.TardisTileEntity;
import com.swdteam.util.WorldUtils;

import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DirectionalPlaceContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.server.MinecraftServer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ControlDiscItem extends Item {

	public ControlDiscItem(Properties properties) {
		super(properties);
	}

	@Override
	public ActionResultType useOn(ItemUseContext context) {
		PlayerEntity player = context.getPlayer();
		ItemStack stack = context.getItemInHand();
		if (!stack.getOrCreateTag().contains(DMNBTKeys.LINKED_ID)) {
			if (player.isShiftKeyDown()) {
				World world = context.getLevel();
				BlockPos pos = context.getClickedPos();
				TileEntity te = world.getBlockEntity(pos);
				if (te != null && te instanceof TardisTileEntity) {
					TardisTileEntity tardis = (TardisTileEntity) te;
					TardisData data = DMTardis.getTardis(tardis.globalID);
					if (data.hasPermission(player, TardisData.PermissionType.DEFAULT)) {
						world.playSound(null, pos, DMSoundEvents.ENTITY_STATTENHEIM_REMOTE_SYNC.get(), SoundCategory.BLOCKS, 1, 1);
						player.displayClientMessage(ModMain.TARDIS_LINKED.withStyle(TextFormatting.GREEN), true);

						stack.getOrCreateTag().putInt(DMNBTKeys.LINKED_ID, tardis.globalID);
					} else data.noPermission(player);
				}
			}
		} else {
			World world = context.getLevel();

			if (!world.isClientSide()) {
				BlockPos pos = context.getClickedPos();

				if (!WorldUtils.canPlace(world, pos, false)) pos = pos.above();

				if (world.getBlockState(pos).canBeReplaced(new DirectionalPlaceContext(world, pos, Direction.NORTH, stack, Direction.NORTH))) {
					int id = stack.getOrCreateTag().getInt(DMNBTKeys.LINKED_ID);
					TardisData data = DMTardis.getTardis(id);

					if (data != null) {
						MinecraftServer server = world.getServer();
						ServerWorld interiorDim = server.getLevel(DMDimensions.TARDIS);

						// If interior is loaded, play demat noise
						if (interiorDim.isLoaded(data.getInteriorSpawnPosition().toBlockPos())) {
							if (data.isLocked()) interiorDim.playSound(null, data.getInteriorSpawnPosition().toBlockPos(), DMSoundEvents.TARDIS_LOCK.get(), SoundCategory.BLOCKS, 1, 1);
							interiorDim.playSound(null, data.getInteriorSpawnPosition().toBlockPos(), DMSoundEvents.TARDIS_DEMAT.get(), SoundCategory.BLOCKS, 1, 1);
						}

						// If TARDIS is landed, remove it
						if (!data.isInFlight()) {
							BlockPos currentPos = data.getCurrentLocation().getBlockPosition();
							ServerWorld currentDim = server.getLevel(data.getCurrentLocation().dimensionWorldKey());
							TileEntity currentTile = currentDim.getBlockEntity(currentPos);
							if (currentTile instanceof TardisTileEntity) {
								if (TardisActionList.doAnimation(currentDim, currentPos)) {
									if (data.isLocked()) interiorDim.playSound(null, data.getInteriorSpawnPosition().toBlockPos(), DMSoundEvents.TARDIS_LOCK.get(), SoundCategory.BLOCKS, 1, 1);
									currentDim.playSound(null, currentPos, DMSoundEvents.TARDIS_DEMAT.get(), SoundCategory.BLOCKS, 1, 1);
									((TardisTileEntity) currentTile).setState(TardisState.DEMAT);
								} else currentDim.setBlockAndUpdate(currentPos, currentDim.getBlockState(currentPos).getFluidState().createLegacyBlock());
							}
						}

						// Set new block
						TardisFlightPool.completeFlight(server, data);
						world.setBlockAndUpdate(pos, DMBlocks.TARDIS.get().defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, world.getBlockState(pos).getBlock() instanceof FlowingFluidBlock));

						// Set new data
						data.setPreviousLocation(data.getCurrentLocation());
						data.setCurrentLocation(pos, world.dimension());
						data.setLocked(false);

						// Set new tile
						TileEntity newTile = world.getBlockEntity(pos);
						if (newTile instanceof TardisTileEntity) {
							TardisTileEntity tardis = (TardisTileEntity) newTile;
							tardis.globalID = id;
							tardis.rotation = context.getPlayer().getYHeadRot();
							data.getCurrentLocation().setFacing(tardis.rotation);
							tardis.closeDoor(TardisDoor.BOTH, TardisTileEntity.DoorSource.TARDIS);
							tardis.setState(TardisState.REMAT);
						}

						data.save();

						stack.shrink(1);
						if (!player.abilities.instabuild) player.broadcastBreakEvent(context.getHand());

						world.playSound(null, pos, DMSoundEvents.ENTITY_STATTENHEIM_REMOTE_SUMMON.get(), SoundCategory.BLOCKS, 1, 1);
					}
				}
			}

			return ActionResultType.sidedSuccess(world.isClientSide());
		}
		return super.useOn(context);
	}
	
	@Override
	public boolean isFoil(ItemStack stack) {
		return stack.getOrCreateTag().contains(DMNBTKeys.LINKED_ID) || super.isFoil(stack);
	}

	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);

		Minecraft mc = Minecraft.getInstance();
		if (mc.options.advancedItemTooltips && stack.getOrCreateTag().contains(DMNBTKeys.LINKED_ID)) {
			tooltip.add(ModMain.ID_TOOLTIP.apply(stack.getTag().getInt(DMNBTKeys.LINKED_ID)).withStyle(TextFormatting.DARK_GRAY));
		}
	}

}
