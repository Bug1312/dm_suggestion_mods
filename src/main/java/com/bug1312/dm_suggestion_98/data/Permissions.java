// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_98.data;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import com.swdteam.common.init.DMTags;
import com.swdteam.common.tardis.TardisData;

import net.minecraft.block.AbstractBlock.AbstractBlockState;

public class Permissions implements Serializable {
	private static final long serialVersionUID = -2431805049593880630L;
	
	public Set<UUID> players = new HashSet<>();
	
	public int activeBlocks;
	public int activeCreativeBlocks;
		
	public static boolean isAllowed(TardisData data, UUID player, AbstractBlockState state) {
		if (data.getOwner_uuid().equals(player)) return true;
		Permissions perms = ((ITardisDataDuck) data).get();
		boolean blockAllowed = (!state.is(DMTags.Blocks.TARDIS_CONTROLS) && perms.activeCreativeBlocks <= 0); 

		return (perms.activeBlocks + perms.activeCreativeBlocks) > 0 ? blockAllowed || perms.players.contains(player) : true;
	}
	
}
