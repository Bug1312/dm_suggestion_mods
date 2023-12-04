// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_166;

import com.swdteam.common.init.DMBlocks;
import com.swdteam.common.init.DMSonicRegistry;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ModMain.MOD_ID)
public class ModMain {
	public static final String MOD_ID = "dm_suggestion_166";

	public ModMain() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::register);
	}
	
	private void register(ParallelDispatchEvent event) {
		event.enqueueWork(() -> {
			DMSonicRegistry.SONIC_LOOKUP.put(DMBlocks.TARDIS.get(), new SonicInteractionTardis());
		});
	}
	
}