// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_242.tardis;

import com.bug1312.dm_suggestion_242.ITardisDataDuck;
import com.bug1312.dm_suggestion_242.Register;
import com.bug1312.dm_suggestion_242.SiegeData;
import com.swdteam.common.init.DMNBTKeys;
import com.swdteam.common.init.DMTardis;
import com.swdteam.common.tardis.TardisData;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class SiegeTardisItem extends BlockItem {

	public SiegeTardisItem(Block block, Properties properties) {
		super(block, properties);
	}
	
	@Override
	public ActionResultType place(BlockItemUseContext context) {
		ItemStack item = context.getItemInHand();
		if (!item.getOrCreateTag().contains(DMNBTKeys.TARDIS_ID)) return ActionResultType.FAIL;
		ActionResultType result = super.place(context);
		
		World world = context.getLevel();
		if (result == ActionResultType.sidedSuccess(world.isClientSide()) && !world.isClientSide() && item.getOrCreateTag().contains(DMNBTKeys.TARDIS_ID)) {
			TardisData data = DMTardis.getTardis(item.getTag().getInt(DMNBTKeys.TARDIS_ID));
			SiegeData siegeData = ((ITardisDataDuck) data).get();
			if (data != null) {
				siegeData.setPlacement();
				data.setPreviousLocation(data.getCurrentLocation());
				data.setCurrentLocation(context.getClickedPos(), context.getLevel().dimension());
				data.save();
			}
			
			TileEntity tile = world.getBlockEntity(context.getClickedPos());
			if (tile != null) {
				CompoundNBT tag = tile.serializeNBT();
				tag.putInt(DMNBTKeys.TARDIS_ID, item.getTag().getInt(DMNBTKeys.TARDIS_ID));
				tile.deserializeNBT(tag);
				context.getPlayer().setItemInHand(context.getHand(), ItemStack.EMPTY);
			}
		}
		return result;
	}
	
	@Override
	public boolean onDroppedByPlayer(ItemStack item, PlayerEntity player) {
		World world = player.level;
		if (!world.isClientSide() && item.getOrCreateTag().contains(DMNBTKeys.TARDIS_ID)) {
			TardisData data = DMTardis.getTardis(item.getTag().getInt(DMNBTKeys.TARDIS_ID));
			SiegeData siegeData = ((ITardisDataDuck) data).get();
			if (data != null) siegeData.resetPlacement();
		}
		
		return super.onDroppedByPlayer(item, player);
	}
	
	// Forge Event
	public static void onPickup(PlayerEvent.ItemPickupEvent event) {
		ItemStack stack = event.getStack();
		if (!event.getEntity().level.isClientSide() && stack.getItem() == Register.SIEGE_ITEM.get() && stack.getOrCreateTag().contains(DMNBTKeys.TARDIS_ID)) {
			int id = stack.getTag().getInt(DMNBTKeys.TARDIS_ID);
			TardisData data = DMTardis.getTardis(id);
			SiegeData siegeData = ((ITardisDataDuck) data).get();
			if (data != null) siegeData.setPlacement(event.getPlayer());
		}
	}

	
	@Override
	public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
		if (!entity.level.isClientSide() && stack.getOrCreateTag().contains(DMNBTKeys.TARDIS_ID)) {
			int id = stack.getTag().getInt(DMNBTKeys.TARDIS_ID);
			TardisData data = DMTardis.getTardis(id);
			SiegeData siegeData = ((ITardisDataDuck) data).get();
			if (data != null) {
				if (data.isInFlight() || !siegeData.inSiege() || siegeData.getHolder(entity.level) != null ||
					(siegeData.getEntity(entity.level) != null && siegeData.getEntity(entity.level) != entity)) 
				{
					stack.setCount(0);
					return true;
				}
				
				if (siegeData.getEntity(entity.level) == null) {
					siegeData.setPlacement(entity);
					return true;
				}
			}
		}
		
		return super.onEntityItemUpdate(stack, entity);
	}
	
	@Override
	public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
		if (!world.isClientSide() && stack.getOrCreateTag().contains(DMNBTKeys.TARDIS_ID)) {
			int id = stack.getTag().getInt(DMNBTKeys.TARDIS_ID);
			TardisData data = DMTardis.getTardis(id);
			SiegeData siegeData = ((ITardisDataDuck) data).get();
			if (data != null) {
				if (data.isInFlight() || !siegeData.inSiege() || siegeData.getEntity(entity.level) != null || siegeData.getHolder(entity.level) == null ||
					(siegeData.getHolder(entity.level) != null && siegeData.getHolder(entity.level) != entity)) 
				{
					stack.setCount(0);
				}
			}
		}
		super.inventoryTick(stack, world, entity, slot, selected);		
	}

}