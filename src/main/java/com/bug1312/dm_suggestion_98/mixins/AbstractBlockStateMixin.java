// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_98.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.bug1312.dm_suggestion_98.Register;
import com.bug1312.dm_suggestion_98.data.Permissions;
import com.swdteam.client.tardis.data.ClientTardisCache;
import com.swdteam.common.init.DMDimensions;
import com.swdteam.common.init.DMTags;
import com.swdteam.common.init.DMTardis;
import com.swdteam.common.tardis.TardisData;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class AbstractBlockStateMixin {
	AbstractBlockState _this = (AbstractBlockState) ((Object) this);

	@Inject(at = @At("HEAD"), method = "use", cancellable = true)
	public void use(World world, PlayerEntity player, Hand hand, BlockRayTraceResult result, CallbackInfoReturnable<ActionResultType> ci) {
		if (world.dimension() == DMDimensions.TARDIS) {
			BlockPos pos = result.getBlockPos();
			TardisData data;
			
			if (world.isClientSide()) data = ClientTardisCache.getTardisData(pos);
			else data = DMTardis.getTardisFromInteriorPos(pos);

			if (data != null && !Permissions.isAllowed(data, player.getUUID(), _this)) {
				player.displayClientMessage(Register.NOT_ALLOWED.withStyle(TextFormatting.YELLOW), true);
				
				if (world.isClientSide() && _this.is(DMTags.Blocks.TARDIS_CONTROLS)) {
					world.addParticle(ParticleTypes.LAVA, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 0, 0, 0);
					player.animateHurt();
				}
				
				ci.setReturnValue(ActionResultType.CONSUME);
				ci.cancel();
			}
		}
	}	

}
