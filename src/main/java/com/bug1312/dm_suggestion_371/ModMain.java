// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_371;

import com.swdteam.common.init.DMItems;

import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ModMain.MOD_ID)
public class ModMain {
	public static final String MOD_ID = "dm_suggestion_371";

	public ModMain() {		
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::client);
	}

	private void client(final FMLClientSetupEvent event) {
		ItemModelsProperties.register(DMItems.STATTENHEIM_REMOTE.get(), new ResourceLocation(ModMain.MOD_ID, "broken"),  (stack, world, entity) -> {
			if (stack.getDamageValue() >= stack.getMaxDamage() - 1) return 1;
			return 0;
		});
	}
}