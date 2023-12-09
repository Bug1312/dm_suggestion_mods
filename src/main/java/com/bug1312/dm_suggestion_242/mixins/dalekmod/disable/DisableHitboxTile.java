// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_242.mixins.dalekmod.disable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.bug1312.dm_suggestion_242.ITardisDataDuck;
import com.swdteam.common.init.DMDimensions;
import com.swdteam.common.init.DMTardis;
import com.swdteam.common.tileentity.DMTileEntityBase;
import com.swdteam.common.tileentity.tardis.TardisDoorHitboxTileEntity;

import net.minecraft.tileentity.TileEntityType;

@Mixin(TardisDoorHitboxTileEntity.class)
public class DisableHitboxTile extends DMTileEntityBase {
	public DisableHitboxTile(TileEntityType<?> _0) { super(_0); }
	
	@Inject(at = @At("HEAD"), method = "tick", cancellable = true)
	public void disableWithSiege(CallbackInfo ci) {
		if (!level.isClientSide() && level.dimension() == DMDimensions.TARDIS) {
			ITardisDataDuck data = (ITardisDataDuck) DMTardis.getTardisFromInteriorPos(getBlockPos());
			if (data != null && data.get().inSiege()) ci.cancel();
		}
	}
}
