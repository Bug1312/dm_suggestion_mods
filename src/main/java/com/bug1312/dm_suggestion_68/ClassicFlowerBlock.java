// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_68;

import net.minecraft.block.FlowerBlock;
import net.minecraft.potion.Effect;

public class ClassicFlowerBlock extends FlowerBlock {

	public ClassicFlowerBlock(Effect suspiciousStewEffect, int effectDuration, Properties properties) {
		super(suspiciousStewEffect, effectDuration, properties);
	}

	@Override
	public OffsetType getOffsetType() {
		return OffsetType.NONE;
	}

}
