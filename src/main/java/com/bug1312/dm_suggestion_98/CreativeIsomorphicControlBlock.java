// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_98;

import com.bug1312.dm_suggestion_98.data.Permissions;

public class CreativeIsomorphicControlBlock extends IsomorphicControlBlock {

	public CreativeIsomorphicControlBlock(Properties properties) {
		super(properties);
	}

	@Override
	public void addActive(Permissions perms, boolean add) {
		if (add) perms.activeCreativeBlocks++;
		else perms.activeCreativeBlocks--;
	}

}
