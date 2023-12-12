// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_371.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.swdteam.common.item.StattenheimRemoteItem;

import net.minecraft.item.ItemStack;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
	private ItemStack _this = (ItemStack) ((Object) this);
	
	@Inject(at = @At("RETURN"), method = "getMaxDamage", cancellable = true)
	public void getMaxDamage(CallbackInfoReturnable<Integer> ci) {
		if (_this.getItem() instanceof StattenheimRemoteItem) ci.setReturnValue(ci.getReturnValue() + 1);
	}

}
