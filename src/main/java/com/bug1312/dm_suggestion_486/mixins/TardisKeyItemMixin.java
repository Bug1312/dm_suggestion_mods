// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_486.mixins;

import org.spongepowered.asm.mixin.Mixin;

import com.swdteam.common.item.TardisKeyItem;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@Mixin(TardisKeyItem.class)
public class TardisKeyItemMixin extends Item {
	public TardisKeyItemMixin(Properties u_0) { super(u_0); }
	
	@Override
	public EquipmentSlotType getEquipmentSlot(ItemStack stack) {
		return EquipmentSlotType.CHEST;
	}

}
