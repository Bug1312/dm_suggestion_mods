// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_1.mixins;

import java.util.Collections;
import java.util.HashSet;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.bug1312.dm_suggestion_1.Register;

import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.MerchantOffer;

@Mixin(VillagerEntity.class)
public class VillagerEntityMixin {

	VillagerEntity _this = (VillagerEntity) ((Object) this);
	
	@Inject(at = @At("TAIL"), method = "updateSpecialPrices")
	public void isLookingAtMe(PlayerEntity player, CallbackInfo ci) {
		if (player.inventory.hasAnyOf(new HashSet<>(Collections.singleton(Register.PAPER.get()))))
			for (MerchantOffer merchantoffer : _this.getOffers()) {
	            int j = (int) Math.floor(0.1625D * (double) merchantoffer.getBaseCostA().getCount());
	            merchantoffer.addToSpecialPriceDiff(-Math.max(j, 1));
			}
				
	}
	
}
