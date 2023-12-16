// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_443;

import com.swdteam.common.item.KnockbackWeaponItem;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class UmbrellaItem extends KnockbackWeaponItem {

	public UmbrellaItem(IItemTier tier, int damage, float speed, Properties properties) {
		super(tier, damage, speed, properties);
	}
	
	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		if (player.isShiftKeyDown()) {
			ItemStack stack = player.getItemInHand(hand);
			
			if (!stack.getOrCreateTag().contains("Open")) stack.getTag().putBoolean("Open", true);
			else stack.getTag().putBoolean("Open", !stack.getTag().getBoolean("Open"));
		}
		return super.use(world, player, hand);
	}

}
