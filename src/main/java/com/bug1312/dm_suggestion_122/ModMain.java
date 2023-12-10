// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_122;

import java.util.Arrays;
import java.util.List;

import com.swdteam.common.init.DMItems;
import com.swdteam.common.init.DMTabs;

import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
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
	public static final String MOD_ID = "dm_suggestion_122";

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ModMain.MOD_ID);
	public static final RegistryObject<Item> KEYCHAIN = ITEMS.register("keychain", () -> new KeyChainItem(new Item.Properties().tab(DMTabs.DM_TARDIS).stacksTo(1)));
	
	public ModMain() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		
		modBus.addListener(this::client);
		
		ITEMS.register(modBus);
	}
	
	public static final List<ResourceLocation> KEYCHAIN_ITEMS = Arrays.asList(
		DMItems.TARDIS_KEY.getId(),
		DMItems.TARDIS_FAN_KEY.getId(),
		DMItems.TARDIS_SPADE_KEY.getId(),
		DMItems.TARDIS_LOCK_PICK_KEY.getId(),
		DMItems.STATTENHEIM_REMOTE.getId()
	);
	
	public static float itemToPredicate(ItemStack stack) {
		Item item = stack.getItem();
		return KEYCHAIN_ITEMS.indexOf(item.getRegistryName());
	}
	
	private void client(final FMLClientSetupEvent event) {
		ItemModelsProperties.register(KEYCHAIN.get(), new ResourceLocation(ModMain.MOD_ID, "current_item"),  (stack, world, entity) -> {
			if (!stack.getOrCreateTag().contains("Selected") || !stack.getOrCreateTag().contains("Items")) return -1;
			
			CompoundNBT tag = stack.getOrCreateTag();
			NonNullList<ItemStack> items = NonNullList.withSize(KeyChainItem.KEY_MAX, ItemStack.EMPTY);
			ItemStackHelper.loadAllItems(tag, items);

			int selected = tag.getInt("Selected");
						
			if (!items.isEmpty()) return itemToPredicate(items.get(selected));
			return -1;
		});
		
		ItemModelsProperties.register(KEYCHAIN.get(), new ResourceLocation(ModMain.MOD_ID, "next_item"),  (stack, world, entity) -> {
			if (!stack.getOrCreateTag().contains("Selected") || !stack.getOrCreateTag().contains("Items")) return -1;
			
			CompoundNBT tag = stack.getOrCreateTag();
			NonNullList<ItemStack> items = NonNullList.withSize(KeyChainItem.KEY_MAX, ItemStack.EMPTY);
			ItemStackHelper.loadAllItems(tag, items);
			
			int selected = tag.getInt("Selected");
			int max = (int) items.stream().filter(item -> !item.isEmpty()).count();
			
			if (max > 1) return itemToPredicate(items.get((selected + 1) % max));
			else return -1;
		});
	}
	
}