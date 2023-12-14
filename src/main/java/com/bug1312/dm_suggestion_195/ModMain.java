// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_195;

import java.util.function.Function;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(ModMain.MOD_ID)
public class ModMain {
	public static final String MOD_ID = "dm_suggestion_195";

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ModMain.MOD_ID);
	public static final RegistryObject<Item> CONTROL_DISC = ITEMS.register("control_disc", () -> new ControlDiscItem(new Item.Properties().stacksTo(1).tab(ItemGroup.TAB_MISC)));

	public static final Function<Integer, TranslationTextComponent> ID_TOOLTIP = (id) -> new TranslationTextComponent(String.format("tooltip.%s.linked_id", ModMain.MOD_ID), id);
	public static final TranslationTextComponent TARDIS_LINKED = new TranslationTextComponent(String.format("notice.%s.control_disc.linked", ModMain.MOD_ID));
	
	public ModMain() {
		ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}
	
}