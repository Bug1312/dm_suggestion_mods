// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_98;

import java.util.function.Supplier;

import com.swdteam.common.init.DMTabs;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class Register {

	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ModMain.MOD_ID);
	public static final RegistryObject<Block> ISOMORPHIC_CONTROL_BLOCK = register(BLOCKS, "isomorphic_control", () -> new IsomorphicControlBlock(AbstractBlock.Properties.of(Material.METAL).strength(2.0F, 20.0F).requiresCorrectToolForDrops().sound(SoundType.METAL)));
	public static final RegistryObject<Block> CREATIVE_ISOMORPHIC_CONTROL_BLOCK = register(BLOCKS, "super_isomorphic_control", () -> new CreativeIsomorphicControlBlock(AbstractBlock.Properties.of(Material.METAL).strength(2.0F, 20.0F).requiresCorrectToolForDrops().sound(SoundType.METAL)));

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ModMain.MOD_ID);
	public static final RegistryObject<Item> ISOMORPHIC_CONTROL_ITEM = register(ITEMS, "isomorphic_control", () -> new BlockItem(Register.ISOMORPHIC_CONTROL_BLOCK.get(), new Item.Properties().tab(DMTabs.DM_TARDIS)));
	public static final RegistryObject<Item> CREATIVE_ISOMORPHIC_CONTROL_ITEM = register(ITEMS, "super_isomorphic_control", () -> new BlockItem(Register.CREATIVE_ISOMORPHIC_CONTROL_BLOCK.get(), new Item.Properties().tab(DMTabs.DM_TARDIS)));
	
	public static final TranslationTextComponent NOT_ALLOWED = new TranslationTextComponent(String.format("notice.%s.no_perms", ModMain.MOD_ID));
	public static final TranslationTextComponent CHECKBOX = new TranslationTextComponent(String.format("gui.%s.checkbox", ModMain.MOD_ID));

	// Register Method
	public static <T extends IForgeRegistryEntry<T>, U extends T> RegistryObject<U> register(final DeferredRegister<T> register, final String name, final Supplier<U> supplier) {
		return register.register(name, supplier);
	}

}
