// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_1.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.bug1312.dm_suggestion_1.Register;

import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(EndermanEntity.class)
public class EndermanEntityMixin {

	@Inject(at = @At("HEAD"), method = "isLookingAtMe", cancellable = true)
	public void isLookingAtMe(PlayerEntity player, CallbackInfoReturnable<Boolean> ci) {
		if (player.isHolding(Register.PAPER.get())) ci.setReturnValue(false);
	}
	
}
