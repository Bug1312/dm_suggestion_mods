// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_174;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ModMain.MOD_ID)
public class ModMain {
	public static final String MOD_ID = "dm_suggestion_174";

	public ModMain() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		
		Register.ITEMS.register(modBus);
	}
	
}