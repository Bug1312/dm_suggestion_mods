// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_354;

import javax.annotation.Nullable;

import com.swdteam.common.init.DMItems;
import com.swdteam.common.item.DataModuleItem;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.LockableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

public class CopierTile extends LockableTileEntity implements ISidedInventory {

	// 0:input 1:inside
	private NonNullList<ItemStack> items = NonNullList.withSize(2, ItemStack.EMPTY);

	public CopierTile() { super(Register.COPIER_TILE.get()); }

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public int getContainerSize() {
		return items.size();
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack itemstack : items) if (!itemstack.isEmpty()) return false;
		return true;
	}

	@Override
	public ItemStack getItem(int index) {
		return index >= 0 && index < items.size() ? items.get(index) : ItemStack.EMPTY;
	}

	@Override
	public ItemStack removeItem(int index, int amount) {
		return ItemStackHelper.removeItem(items, index, amount);
	}

	@Override
	public ItemStack removeItemNoUpdate(int index) {
		return ItemStackHelper.takeItem(items, index);
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		if (index < 0 || index >= items.size()) return;

		if (!getItem(0).isEmpty() && index == 1 && !(stack.getItem() == DMItems.DATA_MODULE.get() && stack.getOrCreateTag().getBoolean("written")))
			stack.setTag(getItem(0).getOrCreateTag());
		
		items.set(index, stack);			
	}

	@Override
	public boolean stillValid(PlayerEntity player) {
		if (this.level.getBlockEntity(this.worldPosition) != this) return false;
		
		return !(player.distanceToSqr(this.worldPosition.getX() + 0.5D, this.worldPosition.getY() + 0.5D, this.worldPosition.getZ() + 0.5D) > 64.0D);
	}

	@Override
	public void clearContent() 	{
		items.clear();
	}

	@Override
	public void load(BlockState state, CompoundNBT tag) {
		super.load(state, tag);
		this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ItemStackHelper.loadAllItems(tag, this.items);
	}

	@Override
	public CompoundNBT save(CompoundNBT tag) {
		super.save(tag);
		ItemStackHelper.saveAllItems(tag, this.items);
		return tag;
	}

	@Override
	public boolean canOpen(PlayerEntity player) {
		return false;
	}

	@Override
	protected ITextComponent getDefaultName() {
		return StringTextComponent.EMPTY;
	}
	
	@Override
	protected Container createMenu(int p_213906_1_, PlayerInventory p_213906_2_) {
		return null;
	}
	
	@Override
	public int[] getSlotsForFace(Direction direction) {
		if (direction == Direction.UP) return new int[] {0};
		return new int[]{1};
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		return super.canPlaceItem(index, stack) && items.get(index).isEmpty() && stack.getItem() instanceof DataModuleItem;
	}
	
	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack stack, Direction direction) {
		return this.canPlaceItem(index, stack);
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
		return (index != 0);
	}
	
	LazyOptional<? extends IItemHandler>[] handlers = SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);

	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing) {
		if (!this.remove && facing != null && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (facing == Direction.UP) return handlers[0].cast();
			else if (facing == Direction.DOWN) return handlers[1].cast();
			return handlers[2].cast();
		}
		return super.getCapability(capability, facing);
	}

	@Override
	protected void invalidateCaps() {
		super.invalidateCaps();
		for (int x = 0; x < handlers.length; x++) handlers[x].invalidate();
	}
	
}
