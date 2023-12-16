// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_443;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemTier;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(ModMain.MOD_ID)
public class ModMain {
	public static final String MOD_ID = "dm_suggestion_443";

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ModMain.MOD_ID);
	public static final RegistryObject<Item> UMBRELLA = ITEMS.register("umbrella", () -> new UmbrellaItem(ItemTier.WOOD, 0, 0, new Item.Properties().tab(ItemGroup.TAB_TOOLS)));
	
	public ModMain() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		
		modBus.addListener(this::client);
		ITEMS.register(modBus);
	}

	private void client(final FMLClientSetupEvent event) {
		ItemModelsProperties.register(UMBRELLA.get(), new ResourceLocation(ModMain.MOD_ID, "open"), (stack, world, entity) -> {
			if (!stack.getOrCreateTag().contains("Open")) return 0;
			return stack.getTag().getBoolean("Open") ? 1 : 0;
		});
	}

}