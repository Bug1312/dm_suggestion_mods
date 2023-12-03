// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_260.mixins;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.swdteam.common.init.DMDimensions;
import com.swdteam.common.init.DMTardis;
import com.swdteam.common.tardis.TardisData;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CompassItem;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

// This is wicked cool, I have to admit
@Mixin(targets = "net/minecraft/item/ItemModelsProperties$2")
public class CompassPointMixin {
	private final ItemModelsProperties.Angle wobble = new ItemModelsProperties.Angle();

	@Inject(at = @At("HEAD"), method = "Lnet/minecraft/item/ItemModelsProperties$2;call(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/entity/LivingEntity;)F", cancellable = true)
	public void call(ItemStack compass, @Nullable ClientWorld world, @Nullable LivingEntity livingEntity, CallbackInfoReturnable<Float> ci) {
        Entity entity = (Entity)(livingEntity != null ? livingEntity : compass.getEntityRepresentation());
        if (world == null && entity.level instanceof ClientWorld) world = (ClientWorld) entity.level;
       	
		if (!CompassItem.isLodestoneCompass(compass) && entity != null && world != null && world.dimension() == DMDimensions.TARDIS) {
			TardisData data = DMTardis.getTardisFromInteriorPos(entity.blockPosition());
			if (data != null) {
				BlockPos blockpos = data.getInteriorSpawnPosition().toBlockPos();
				long time = world.getGameTime();
				if (blockpos != null && !(entity.position().distanceToSqr(blockpos.getX() + 0.5D, entity.position().y(), blockpos.getZ() + 0.5D) < 1.0E-5F)) {
					boolean isLocalPlayer = livingEntity instanceof PlayerEntity && ((PlayerEntity) livingEntity).isLocalPlayer();
					double rotation = 0.0D;
					
					if (isLocalPlayer) rotation = (double) livingEntity.yRot;
					else if (entity instanceof ItemFrameEntity) rotation = this.getItemFrameRotation((ItemFrameEntity) entity);
					else if (entity instanceof ItemEntity) rotation = (180 - ((ItemEntity) entity).getSpin(0.5F) / (Math.PI * 2F) * 360F);
					else if (livingEntity != null) rotation = (double) livingEntity.yBodyRot;
					
					rotation = MathHelper.positiveModulo(rotation / 360D, 1D);
					Vector3d centerPos = Vector3d.atCenterOf(blockpos);
					double direction = Math.atan2(centerPos.z() - entity.getZ(), centerPos.x() - entity.getX()) / (Math.PI * 2F);
					double finalDir;
					if (isLocalPlayer) {
						if (this.wobble.shouldUpdate(time)) this.wobble.update(time, 0.5D - (rotation - 0.25D));
						finalDir = direction + this.wobble.rotation;
					} else {
						finalDir = 0.5D - (rotation - 0.25D - direction);
					}

					ci.setReturnValue(MathHelper.positiveModulo((float) finalDir, 1F));
				}
			}
		}
	}
	
	private double getItemFrameRotation(ItemFrameEntity entity) {
		Direction direction = entity.getDirection();
		int i = direction.getAxis().isVertical() ? 90 * direction.getAxisDirection().getStep() : 0;
		return MathHelper.wrapDegrees(180 + direction.get2DDataValue() * 90 + entity.getRotation() * 45 + i);
	}
}
