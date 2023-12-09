// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_242.mixins.dalekmod;

import org.spongepowered.asm.mixin.Mixin;

import com.bug1312.dm_suggestion_242.ITardisDataDuck;
import com.bug1312.dm_suggestion_242.SiegeData;
import com.google.gson.annotations.SerializedName;
import com.swdteam.common.tardis.TardisData;

@Mixin(TardisData.class)
public class TardisDataMixin implements ITardisDataDuck {
	@SerializedName("siege") private SiegeData siege;
	@Override public SiegeData get() { if (siege == null) siege = new SiegeData(); return siege; }
}