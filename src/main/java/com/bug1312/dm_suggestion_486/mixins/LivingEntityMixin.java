// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_486.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.swdteam.common.item.TardisKeyItem;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
	protected LivingEntityMixin(EntityType<? extends LivingEntity> u_0, World u_1) { super(u_0, u_1); }
	public LivingEntity _this = (LivingEntity) ((Object) this);
	
	@Inject(at = @At("HEAD"), method = "tick")
	public void tick(CallbackInfo ci) {
		ItemStack itemstack = _this.getItemBySlot(EquipmentSlotType.CHEST);
		if (itemstack.getItem() instanceof TardisKeyItem) _this.addEffect(new EffectInstance(Effects.INVISIBILITY, 76, 0, false, true, true));
	}

}
