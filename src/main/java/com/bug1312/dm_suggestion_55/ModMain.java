// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_55;

import com.swdteam.common.item.GunItem;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;

@Mod(ModMain.MOD_ID)
public class ModMain {
	public static final String MOD_ID = "dm_suggestion_55";

	public ModMain() {}
	
	public static float quickChargeMath(GunItem item, ItemStack stack) {
		int i = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.QUICK_CHARGE, stack);
		float def = item.requiredChargeTime;
		return def - i;
	}
}