// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_242;

import java.util.function.Supplier;

import com.bug1312.dm_suggestion_242.lever.SiegeLeverBlock;
import com.bug1312.dm_suggestion_242.tardis.SiegeTardisBlock;
import com.bug1312.dm_suggestion_242.tardis.SiegeTardisItem;
import com.bug1312.dm_suggestion_242.tardis.SiegeTardisTileEntity;
import com.swdteam.common.init.DMTabs;
import com.swdteam.common.init.DMTranslationKeys;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class Register {
	
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ModMain.MOD_ID);
	public static final RegistryObject<Block> SIEGE_BLOCK = register(BLOCKS, "siege_mode_cube", () -> new SiegeTardisBlock(AbstractBlock.Properties.of(Material.METAL).strength(-1.0F, 3600000.0F).sound(SoundType.METAL)));
	public static final RegistryObject<Block> SIEGE_LEVER_BLOCK = register(BLOCKS, "siege_lever", () -> new SiegeLeverBlock(AbstractBlock.Properties.of(Material.STONE).instabreak().noOcclusion().sound(SoundType.WOOD)));

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ModMain.MOD_ID);
	public static final RegistryObject<Item> SIEGE_ITEM = register(ITEMS, "siege_mode_cube", () -> new SiegeTardisItem(Register.SIEGE_BLOCK.get(), new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));
	public static final RegistryObject<Item> SIEGE_LEVER_ITEM = register(ITEMS, "siege_lever", () -> new BlockItem(Register.SIEGE_LEVER_BLOCK.get(), new Item.Properties().tab(DMTabs.DM_TARDIS)));
	
	public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, ModMain.MOD_ID);
	public static final RegistryObject<TileEntityType<SiegeTardisTileEntity>> SIEGE_TILE = register(TILE_ENTITIES, "siege_mode_cube", () -> TileEntityType.Builder.of(SiegeTardisTileEntity::new, Register.SIEGE_BLOCK.get()).build(null));

	public static final TranslationTextComponent SIEGE_ERROR = new TranslationTextComponent(String.format("notice.%s.siege_error", ModMain.MOD_ID));
	public static final TranslationTextComponent OBSTRUCTION = DMTranslationKeys.TARDIS_UNABLE_TO_LAND;
	
    // Register Method
	public static <T extends IForgeRegistryEntry<T>, U extends T> RegistryObject<U> register(final DeferredRegister<T> register, final String name, final Supplier<U> supplier) {
		return register.register(name, supplier);
	}
	
}