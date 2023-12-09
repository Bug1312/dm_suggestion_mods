// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_242.tardis;

import javax.annotation.Nullable;

import com.bug1312.dm_suggestion_242.Register;
import com.swdteam.common.init.DMNBTKeys;
import com.swdteam.common.tileentity.DMTileEntityBase;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;

public class SiegeTardisTileEntity extends DMTileEntityBase {
	@Nullable Integer id;
	
	public SiegeTardisTileEntity() {
		super(Register.SIEGE_TILE.get());
	}

	@Override
	public CompoundNBT save(CompoundNBT compound) {
		if (this.id != null) compound.putInt(DMNBTKeys.TARDIS_ID, this.id);
		return super.save(compound);
	}

	@Override
	public void load(BlockState blockstate, CompoundNBT compound) {
		if (compound.contains(DMNBTKeys.TARDIS_ID)) this.id = compound.getInt(DMNBTKeys.TARDIS_ID);
		super.load(blockstate, compound);
	}

}