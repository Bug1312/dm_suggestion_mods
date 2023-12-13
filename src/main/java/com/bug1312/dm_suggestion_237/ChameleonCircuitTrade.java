// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_237;

import java.util.List;
import java.util.Random;

import com.swdteam.common.init.DMItems;
import com.swdteam.common.init.DMTardisRegistry;
import com.swdteam.common.tardis.Tardis;

import net.minecraft.entity.Entity;
import net.minecraft.entity.merchant.villager.VillagerTrades;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

public class ChameleonCircuitTrade implements VillagerTrades.ITrade {

	public MerchantOffer getOffer(Entity entity, Random rand) {
		ItemStack stack = new ItemStack(DMItems.CHAMELEON_CATRTIDGE.get());
		List<Tardis> tardises = DMTardisRegistry.getRegistryAsList();
		Tardis tardis = tardises.get(rand.nextInt(tardises.size()));
		ResourceLocation skin = tardis.getRegistryName();
		stack.getOrCreateTag().putString("skin_id", skin.toString());
		
		stack.setHoverName(new StringTextComponent(tardis.getData().getExteriorName().getString()).setStyle(Style.EMPTY.withColor(TextFormatting.GREEN).withItalic(false)));

		return new MerchantOffer(new ItemStack(Items.EMERALD), new ItemStack(DMItems.CIRCUIT.get()), stack, 3, 3, 0.2F);
	}
}
