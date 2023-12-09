// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_242.mixins.dalekmod;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.bug1312.dm_suggestion_242.ITardisDataDuck;
import com.bug1312.dm_suggestion_242.ModMain;
import com.swdteam.common.tardis.TardisState;
import com.swdteam.common.tileentity.ExtraRotationTileEntityBase;
import com.swdteam.common.tileentity.TardisTileEntity;

import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;

@Mixin(TardisTileEntity.class)
public abstract class TardisTileEntityMixin extends ExtraRotationTileEntityBase implements ITickableTileEntity {
	public TardisTileEntityMixin(TileEntityType<?> tileEntityTypeIn) { super(tileEntityTypeIn); }
	TardisTileEntity _this = (TardisTileEntity) ((Object) this);
	
	@Inject(at = @At("HEAD"), method = "tick", cancellable = true)
	public void replaceBlock(CallbackInfo ci) {
		if (_this.tardisData != null && ((ITardisDataDuck) _this.tardisData).get().inSiege()) {
			if (_this.state == TardisState.DEMAT) level.setBlockAndUpdate(worldPosition, getBlockState().getFluidState().createLegacyBlock());
			else ModMain.setSiegeBlock(level, worldPosition, _this.tardisData, _this.tardisData.getCurrentLocation().getFacing());
			ci.cancel();
		}	
	}
}
