// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_371.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.swdteam.common.init.DMItems;
import com.swdteam.common.item.StattenheimRemoteItem;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@Mixin(Item.class)
public class ItemMixin {

	@Inject(at = @At("RETURN"), method = "isValidRepairItem", cancellable = true)
	public void isValidRepairItem(ItemStack stack, ItemStack repair, CallbackInfoReturnable<Boolean> ci) {
		if (stack.getItem() instanceof StattenheimRemoteItem && repair.getItem() == DMItems.CIRCUIT.get()) ci.setReturnValue(true);
	}
	
}
