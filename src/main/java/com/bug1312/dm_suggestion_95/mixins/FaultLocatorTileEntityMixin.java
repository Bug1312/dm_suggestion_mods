// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_95.mixins;

import org.spongepowered.asm.mixin.Mixin;

import com.swdteam.common.init.DMItems;
import com.swdteam.common.init.DMNBTKeys;
import com.swdteam.common.init.DMTardis;
import com.swdteam.common.item.ArtronItem;
import com.swdteam.common.item.FluidLinkItem;
import com.swdteam.common.tardis.TardisData;
import com.swdteam.common.tileentity.DMTileEntityBase;
import com.swdteam.common.tileentity.tardis.FaultLocatorTileEntity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;

@Mixin(FaultLocatorTileEntity.class)
public class FaultLocatorTileEntityMixin extends DMTileEntityBase implements IInventory, ITickableTileEntity {
	private FaultLocatorTileEntityMixin(TileEntityType<?> tileEntityTypeIn) { super(tileEntityTypeIn); }	
	FaultLocatorTileEntity _this = (FaultLocatorTileEntity) ((Object) this);

	@Override
	public void clearContent() {
		_this.setFuelSlot(ItemStack.EMPTY);
	}

	@Override
	public int getContainerSize() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return _this.getFuelSlot().isEmpty();
	}
	
	@Override
	public boolean canPlaceItem(int slot, ItemStack stack) {
		Item item = stack.getItem();
		
		if (item instanceof ArtronItem) return this.isEmpty();

		if (item instanceof FluidLinkItem) {
            TardisData data = DMTardis.getTardisFromInteriorPos(this.getBlockPos());
            if (data == null) return false;
            
			if (item == DMItems.ACCURACY_FLUID_LINK.get()) return data.getFluidLinkAccuracy() < 100;
			if (item == DMItems.FLIGHT_FLUID_LINK.get()) return data.getFluidLinkFlightTime() < 100;
			if (item == DMItems.FUEL_FLUID_LINK.get()) return data.getFluidLinkFuelConsumption() < 100;
		}
		
		return false;
	}

	@Override
	public ItemStack getItem(int slot) {
		return (slot == 0) ? _this.getFuelSlot() : ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItem(int slot, int count) {
		ItemStack stack = _this.getFuelSlot();
		if (stack.getItem() == Items.GLASS_BOTTLE) {
			_this.setFuelSlot(ItemStack.EMPTY);
			this.setChanged();
			return stack;
		} 
		
		return ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItemNoUpdate(int slot) {
		_this.setFuelSlot(ItemStack.EMPTY);
		return ItemStack.EMPTY;
	}

	// An issue due to the fault locator being rushed is if someone 
	// has the GUI open, it won't update. Same as if two players were
	// using the GUI at once it won't sync
	@Override
	public void setItem(int slot, ItemStack stack) {
		Item item = stack.getItem();
		
		if (item instanceof ArtronItem) {
			_this.setFuelSlot(stack);
			return;
		}

		if (item instanceof FluidLinkItem) {
            TardisData data = DMTardis.getTardisFromInteriorPos(this.getBlockPos());
            if (data == null) return;
            
			if (item == DMItems.ACCURACY_FLUID_LINK.get()) data.setFluidLinkAccuracy(data.getFluidLinkAccuracy() + 1);
			else if (item == DMItems.FLIGHT_FLUID_LINK.get()) data.setFluidLinkFlightTime(data.getFluidLinkFlightTime() + 1);
			else if (item == DMItems.FUEL_FLUID_LINK.get()) data.setFluidLinkFuelConsumption(data.getFluidLinkFuelConsumption() + 1);
			
			data.save();
		}
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		if (level.getBlockEntity(worldPosition) != this) return false;
		return !(player.distanceToSqr(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D) > 64.0D);
	}

	// We were really rushed on the fault locator so all the interactions 
	// are in the GUI and it's not a real container. Have to add this.
	@Override
	public void tick() {
		if (this.isEmpty()) return;
		if (!(_this.getFuelSlot().getItem() instanceof ArtronItem)) return;
        TardisData data = DMTardis.getTardisFromInteriorPos(this.getBlockPos());
        if (data == null) return;
        if (data.getFuel() >= 100) return;
        
        ItemStack stack = _this.getFuelSlot();
        
        double fuel = stack.getOrCreateTag().contains(DMNBTKeys.ATRON_CHARGE) ? 
        		stack.getOrCreateTag().getDouble(DMNBTKeys.ATRON_CHARGE) : 100D;
		
        if (data.getFuel() + fuel > 100) {
        	// It's not empty
        	double leftOver = (data.getFuel() + fuel) - 100;
        	
        	CompoundNBT tag = stack.getOrCreateTag();
        	tag.putDouble(DMNBTKeys.ATRON_CHARGE, leftOver);
        	stack.setTag(tag);
        } else {
        	// It's empty
        	stack = new ItemStack(Items.GLASS_BOTTLE);
        }
        
        data.addFuel(fuel);
        data.save();
        _this.setFuelSlot(stack);
	}
}
