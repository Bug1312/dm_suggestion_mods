// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_174;

import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class Register {
	// Items
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ModMain.MOD_ID);
	public static final RegistryObject<Item> TIME_RING = register(ITEMS, "time_ring", () -> new InteriorTeleportItem(new Item.Properties().tab(ItemGroup.TAB_MISC).stacksTo(1)));

	// Translations
	public static final Function<Integer, TranslationTextComponent> ID_TOOLTIP = (id) -> new TranslationTextComponent(String.format("tooltip.%s.linked_id", ModMain.MOD_ID), id);
	public static final TranslationTextComponent TARDIS_LINKED = new TranslationTextComponent(String.format("notice.%s.linked", ModMain.MOD_ID));
	
	// Register Method
	public static <T extends IForgeRegistryEntry<T>, U extends T> RegistryObject<U> register(final DeferredRegister<T> register, final String name, final Supplier<U> supplier) {
		return register.register(name, supplier);
	}
}