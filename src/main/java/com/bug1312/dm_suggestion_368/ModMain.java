// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_368;

import com.swdteam.common.item.ClothesItem;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(ModMain.MOD_ID)
public class ModMain {
	public static final String MOD_ID = "dm_suggestion_368";

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ModMain.MOD_ID);
	public static final RegistryObject<Item> RAINBOW_FEZ = ITEMS.register("rainbow_fez", () -> new ClothesItem(EquipmentSlotType.HEAD));

	public ModMain() {
		ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}	
}