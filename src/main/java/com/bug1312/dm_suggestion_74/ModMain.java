// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_74;

import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ModMain.MOD_ID)
public class ModMain {
	public static final String MOD_ID = "dm_suggestion_74";

	public ModMain() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

		Register.ITEMS.register(modBus);
		Register.BLOCKS.register(modBus);
		
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::client);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::common);
	}
	
	public void client(final FMLClientSetupEvent event) {
		RenderTypeLookup.setRenderLayer(Register.FLOWER_BLOCK.get(), RenderType.cutout());
		RenderTypeLookup.setRenderLayer(Register.FLOWER_POT_BLOCK.get(), RenderType.cutout());
	}
	
	public void common(final FMLCommonSetupEvent event) {
        ((FlowerPotBlock) Blocks.FLOWER_POT).addPlant(Register.FLOWER_BLOCK.getId(), Register.FLOWER_POT_BLOCK);
	}
	
}