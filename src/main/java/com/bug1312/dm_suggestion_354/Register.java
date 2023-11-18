// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_354;

import java.util.function.Supplier;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class Register {
	
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ModMain.MOD_ID);
	public static final RegistryObject<Block> COPIER_BLOCK = register(BLOCKS, "copier", () -> new CopierBlock(AbstractBlock.Properties.of(Material.METAL).strength(2.0F, 20.0F).requiresCorrectToolForDrops().sound(SoundType.METAL)));

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ModMain.MOD_ID);
	public static final RegistryObject<Item> COPIER_ITEM = register(ITEMS, "copier", () -> new BlockItem(Register.COPIER_BLOCK.get(), new Item.Properties().tab(ItemGroup.TAB_DECORATIONS)));
	
	public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, ModMain.MOD_ID);
	public static final RegistryObject<TileEntityType<CopierTile>> COPIER_TILE = register(TILE_ENTITIES, "copier", () -> TileEntityType.Builder.of(CopierTile::new, Register.COPIER_BLOCK.get()).build(null));

    // Register Method
	public static <T extends IForgeRegistryEntry<T>, U extends T> RegistryObject<U> register(final DeferredRegister<T> register, final String name, final Supplier<U> supplier) {
		return register.register(name, supplier);
	}
	
}
