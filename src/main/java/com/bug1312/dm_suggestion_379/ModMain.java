// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_379;

import com.swdteam.common.block.RoundelBlock;
import com.swdteam.common.init.DMTabs;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(ModMain.MOD_ID)
public class ModMain {
	public static final String MOD_ID = "dm_suggestion_379";

	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ModMain.MOD_ID);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ModMain.MOD_ID);

	static {
		registerRoundel(Blocks.COAL_BLOCK);
		registerRoundel(Blocks.IRON_BLOCK);
		registerRoundel(Blocks.GOLD_BLOCK);
		registerRoundel(Blocks.REDSTONE_BLOCK, true, false);
		registerRoundel(Blocks.EMERALD_BLOCK);
		registerRoundel(Blocks.LAPIS_BLOCK);
		registerRoundel(Blocks.DIAMOND_BLOCK);
		registerRoundel(Blocks.NETHERITE_BLOCK, false, true);
	}

	public ModMain() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		ITEMS.register(modBus);
		BLOCKS.register(modBus);
	}

	// Register Methods
	public static void registerRoundel(final Block baseBlock, boolean isRedstone, boolean isNetherite) {
		String name = ForgeRegistries.BLOCKS.getKey(baseBlock).getPath();
		RegistryObject<Block> newBlock = BLOCKS.register(name + "_roundel", () -> isRedstone ? new RedstoneRoundelBlock(AbstractBlock.Properties.copy(baseBlock)) : new RoundelBlock(AbstractBlock.Properties.copy(baseBlock)));
		ITEMS.register(name + "_roundel", () -> new BlockItem(newBlock.get(), (isNetherite ? new Item.Properties().fireResistant() : new Item.Properties()).tab(DMTabs.DM_TARDIS)));
	}

	public static void registerRoundel(final Block block) { registerRoundel(block, false, false); }

}