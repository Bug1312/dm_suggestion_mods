// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_237;

import java.util.List;

import com.swdteam.common.init.DMItems;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.entity.merchant.villager.VillagerTrades.ITrade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ModMain.MOD_ID)
public class ModMain {
	public static final String MOD_ID = "dm_suggestion_237";

	public ModMain() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

		Register.PROFESSIONS.register(modBus);
		Register.POI_TYPE.register(modBus);
		
		MinecraftForge.EVENT_BUS.addListener(this::addCustomTrades);
	}

	public void addCustomTrades(VillagerTradesEvent event) {
		Int2ObjectMap<List<ITrade>> trades = event.getTrades();
		if (event.getType() == Register.TARDIS_PROFESSION.get()) {
			List<ITrade> novice 	= trades.get(1);
			List<ITrade> apprentice	= trades.get(2);
			List<ITrade> journeyman	= trades.get(3);
			List<ITrade> expert 	= trades.get(4);
			List<ITrade> master		= trades.get(5);

			novice.add((trader, rand) -> new MerchantOffer(new ItemStack(DMItems.PLASTIC_CHUNK.get(), 15), new ItemStack(Items.EMERALD), 12, 1, 0.2F));
			novice.add((trader, rand) -> new MerchantOffer(new ItemStack(DMItems.SILICON.get(), 15), new ItemStack(Items.EMERALD), 12, 1, 0.2F));
			novice.add((trader, rand) -> new MerchantOffer(new ItemStack(DMItems.CINNABAR.get(), 20), new ItemStack(Items.EMERALD), 16, 1, 0.05F));
			novice.add((trader, rand) -> new MerchantOffer(new ItemStack(DMItems.ZEITON_CRYSTAL.get(), 5), new ItemStack(Items.EMERALD), 16, 1, 0.05F));

			apprentice.add((trader, rand) -> new MerchantOffer(new ItemStack(DMItems.CIRCUIT.get(), 5), new ItemStack(Items.EMERALD), 12, 1, 0.2F));
			apprentice.add((trader, rand) -> new MerchantOffer(new ItemStack(DMItems.STAINLESS_STEEL_INGOT.get(), rand.nextInt(3) + 4), new ItemStack(Items.EMERALD), 12, 3, 0.05F));
			apprentice.add(new ChameleonCircuitTrade());

			journeyman.add((trader, rand) -> new MerchantOffer(new ItemStack(DMItems.DALEKANIUM_INGOT.get(), rand.nextInt(2) + 4), new ItemStack(Items.EMERALD), 12, 4, 0.05F));
			journeyman.add((trader, rand) -> new MerchantOffer(new ItemStack(Items.EMERALD, 4), new ItemStack(DMItems.PLUNGER.get()), 12, 4, 0.2F));

			expert.add((trader, rand) -> new MerchantOffer(new ItemStack(DMItems.SONIC_EMITTER.get()), new ItemStack(Items.EMERALD), 12, 3, 0.2F));
			expert.add(new ChameleonCircuitTrade());
			
			master.add(new ChameleonCircuitTrade());
			master.add((trader, rand) -> new MerchantOffer(new ItemStack(Items.EMERALD), new ItemStack(Items.STICK), new ItemStack(DMItems.SONIC_SCREWDRIVER.get()), 1, 7, 0.05F));
		}
	}
	
}