// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_242.mixins.minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.bug1312.dm_suggestion_242.tardis.SiegeTardisItem;

import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.HopperTileEntity;

@Mixin(HopperTileEntity.class)
public class HopperTileEntityMixin {

	@Inject(at = @At("HEAD"), method = "Lnet/minecraft/tileentity/HopperTileEntity;addItem(Lnet/minecraft/inventory/IInventory;Lnet/minecraft/entity/item/ItemEntity;)Z", cancellable = true)
	private static void excludeSiegeTardis(IInventory inventory, ItemEntity entity, CallbackInfoReturnable<Boolean> ci) {
		if (entity.getItem().getItem() instanceof SiegeTardisItem) {
			ci.setReturnValue(false);
			ci.cancel();
		}
	}
	
}
