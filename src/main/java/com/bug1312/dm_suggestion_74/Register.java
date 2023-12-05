// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_74;

import java.util.function.Supplier;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.potion.Effects;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;


public class Register {
	
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ModMain.MOD_ID);
	public static final RegistryObject<Block> FLOWER_BLOCK = register(BLOCKS, "classic_blue_flower",  () -> new ClassicFlowerBlock(Effects.JUMP, 6, AbstractBlock.Properties.of(Material.GRASS).noCollission().strength(0.0F, 0.0F).sound(SoundType.GRASS)));
	public static final RegistryObject<Block> FLOWER_POT_BLOCK = register(BLOCKS, "potted_classic_blue_flower", () -> new FlowerPotBlock(() -> (FlowerPotBlock) Blocks.FLOWER_POT, Register.FLOWER_BLOCK, AbstractBlock.Properties.of(Material.DECORATION).instabreak().noOcclusion()));

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ModMain.MOD_ID);
	public static final RegistryObject<Item> FLOWER_ITEM = register(ITEMS, "classic_blue_flower", () -> new BlockItem(Register.FLOWER_BLOCK.get(), new Item.Properties().tab(ItemGroup.TAB_DECORATIONS)));

    // Register Method
	public static <T extends IForgeRegistryEntry<T>, U extends T> RegistryObject<U> register(final DeferredRegister<T> register, final String name, final Supplier<U> supplier) {
		return register.register(name, supplier);
	}
	
}