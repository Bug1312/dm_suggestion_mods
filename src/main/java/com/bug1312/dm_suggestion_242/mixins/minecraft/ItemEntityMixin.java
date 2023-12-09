// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_242.mixins.minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.bug1312.dm_suggestion_242.Register;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity {
	public ItemEntityMixin(EntityType<?> _0, World _1) { super(_0, _1); }
	public ItemEntity _this = (ItemEntity) ((Object) this);
		
	@Inject(at = @At("HEAD"), method = "hurt", cancellable = true)
	public void hurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> ci) {
		ItemStack stack = _this.getItem();
		if (stack.getItem() == Register.SIEGE_ITEM.get()) {
			// Item Entities will never get OUT_OF_WORLD, but hey always good to stay safe
			if (source != DamageSource.OUT_OF_WORLD) ci.setReturnValue(false);
		}
	}
	
}
