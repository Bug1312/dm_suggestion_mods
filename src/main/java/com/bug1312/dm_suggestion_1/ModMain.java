// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_1;

import com.swdteam.client.init.ItemRenderingRegistry;
import com.swdteam.client.init.ItemRenderingRegistry.ItemRenderInfo;

import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ModMain.MOD_ID)
public class ModMain {
	public static final String MOD_ID = "dm_suggestion_1";

	public ModMain() {
		Register.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}

	public void clientSetup(FMLClientSetupEvent event) {
		ItemRenderInfo paper = ItemRenderingRegistry.addItemRenderer(Register.PAPER);
		paper.addTransformType("gui", TransformType.GROUND);
		paper.addTransformType("gui", TransformType.FIXED);
		paper.addTransformType("gui", TransformType.GUI);
	}
	
}
