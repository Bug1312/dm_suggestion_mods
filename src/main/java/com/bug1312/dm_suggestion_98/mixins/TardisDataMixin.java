// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_98.mixins;

import org.spongepowered.asm.mixin.Mixin;

import com.bug1312.dm_suggestion_98.data.ITardisDataDuck;
import com.bug1312.dm_suggestion_98.data.Permissions;
import com.google.gson.annotations.SerializedName;
import com.swdteam.common.tardis.TardisData;

@Mixin(TardisData.class)
public class TardisDataMixin implements ITardisDataDuck {
	@SerializedName("suggestion_98_permissions") private Permissions perms;
	@Override public Permissions get() { if (perms == null) perms = new Permissions(); return perms; }
}
