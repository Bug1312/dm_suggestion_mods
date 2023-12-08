// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_205.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import com.swdteam.network.packets.PacketEjectWaypointCartridge;

import net.minecraft.util.math.BlockPos;

@Mixin(PacketEjectWaypointCartridge.class)
public interface PacketEjectWaypointCartridgeAccessor {
	@Accessor("name") String getName();
	@Accessor("blockPos") BlockPos getBlockPos();	
}
