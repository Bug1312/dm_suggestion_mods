// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_206;

import java.util.function.Supplier;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;

@Mod(ModMain.MOD_ID)
public class ModMain {
	public static final String MOD_ID = "dm_suggestion_206";

	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ModMain.MOD_ID);
	public static final RegistryObject<Block> DALEK_CONTROL_BLOCK = register(BLOCKS, "dalek_button", () -> new PlungerButtonBlock(AbstractBlock.Properties.of(Material.DECORATION).noCollission().strength(0.5F).sound(SoundType.STONE)));

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ModMain.MOD_ID);
	public static final RegistryObject<Item> DALEK_CONTROL_ITEM = register(ITEMS, "dalek_button", () -> new BlockItem(DALEK_CONTROL_BLOCK.get(), new Item.Properties().tab(ItemGroup.TAB_REDSTONE)));

	// Correct subtitles & sound category, even with reused audio :P
	public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ModMain.MOD_ID);
	public static final RegistryObject<SoundEvent> BUTTON_NOISE = register(SOUNDS, "block.dalek_button", () -> new SoundEvent(new ResourceLocation(ModMain.MOD_ID, "block.dalek_button")));
	
	// Register Method
	public static <T extends IForgeRegistryEntry<T>, U extends T> RegistryObject<U> register(final DeferredRegister<T> register, final String name, final Supplier<U> supplier) {
		return register.register(name, supplier);
	}

	public ModMain() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

		BLOCKS.register(modBus);
		ITEMS.register(modBus);
		SOUNDS.register(modBus);
	}

}