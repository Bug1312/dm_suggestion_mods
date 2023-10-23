// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_1;

import java.util.function.Supplier;

import net.minecraft.item.Item;
import net.minecraft.item.Item.Properties;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class Register {
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ModMain.MOD_ID);
	public static final RegistryObject<Item> PAPER = register(ITEMS, "psychic_paper", () -> new Item(new Properties().tab(ItemGroup.TAB_TOOLS)));
	
	public static <T extends IForgeRegistryEntry<T>> RegistryObject<T> register(final DeferredRegister<T> register, final String name, final Supplier<? extends T> supplier) {
		return register.register(name, supplier);
	}
}
