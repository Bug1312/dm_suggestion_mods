// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_371.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.swdteam.common.item.StattenheimRemoteItem;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;

@Mixin(StattenheimRemoteItem.class)
public class StattenheimRemoteItemMixin {
	
	@Inject(at = @At("HEAD"), method = "useOn", cancellable = true)
	public void useOn(ItemUseContext context, CallbackInfoReturnable<ActionResultType> ci) {
		ItemStack stack = context.getItemInHand();
		if (stack.getDamageValue() >= stack.getMaxDamage() - 1) {
			ci.setReturnValue(ActionResultType.PASS);
			ci.cancel();
		}
	}
	
}
