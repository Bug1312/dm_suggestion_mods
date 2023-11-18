// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_354;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ModMain.MOD_ID)
public class ModMain {
	public static final String MOD_ID = "dm_suggestion_354";

	public ModMain() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

		Register.ITEMS.register(modBus);
		Register.BLOCKS.register(modBus);
		Register.TILE_ENTITIES.register(modBus);

		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::client);
	}
	
	public void client(final FMLClientSetupEvent event) {
		RenderTypeLookup.setRenderLayer(Register.COPIER_BLOCK.get(), RenderType.cutout());
	}
	
}