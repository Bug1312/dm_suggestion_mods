// Copyright 2024 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_205;

import com.swdteam.common.command.tardis_console.environment.CommandEnvironment;
import com.swdteam.common.init.DMTardis;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

@Mod(ModMain.MOD_ID)
public class ModMain {
	public static final String MOD_ID = "dm_suggestion_205";

	public ModMain() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onServerStarting(FMLServerStartingEvent event) {
		DMTardis.CommandRegistry.register(new CommandChameleon(), CommandEnvironment.TARDOS);
	}
}