// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_55.mixins;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.bug1312.dm_suggestion_55.ModMain;
import com.swdteam.common.init.DMItems;
import com.swdteam.common.item.GunItem;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

@Mixin(GunItem.class)
public abstract class GunItemMixin extends Item {

	public GunItemMixin(Properties u_0) { super(u_0); }

	@Redirect(method = "releaseUsing", at = @At(value = "FIELD", target = "Lcom/swdteam/common/item/GunItem;requiredChargeTime:F", ordinal = 0, opcode = Opcodes.GETFIELD), remap = false)
	private float injected(GunItem item, ItemStack stack, World worldIn, LivingEntity entityLiving, int timeLeft) {
		return ModMain.quickChargeMath(item, stack);
	}
	
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
		boolean def = super.canApplyAtEnchantingTable(stack, enchantment);
		boolean quickCharge = stack.getItem() != DMItems.CANNON.get() && enchantment == Enchantments.QUICK_CHARGE;
		
		return def || (quickCharge);
	}
	
}
