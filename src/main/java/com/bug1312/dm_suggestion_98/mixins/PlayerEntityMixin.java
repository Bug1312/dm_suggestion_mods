// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_98.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.bug1312.dm_suggestion_98.Register;
import com.bug1312.dm_suggestion_98.data.ITardisDataDuck;
import com.bug1312.dm_suggestion_98.data.Permissions;
import com.swdteam.client.tardis.data.ClientTardisCache;
import com.swdteam.common.init.DMDimensions;
import com.swdteam.common.init.DMTags;
import com.swdteam.common.init.DMTardis;
import com.swdteam.common.tardis.TardisData;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;
import net.minecraft.world.World;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends Entity {
	public PlayerEntityMixin(EntityType<?> _0, World _1) { super(_0, _1); }
	PlayerEntity _this = (PlayerEntity) ((Object) this);

	@Inject(at = @At("HEAD"), method = "blockActionRestricted", cancellable = true)
	public void blockActionRestricted(World world, BlockPos pos, GameType type, CallbackInfoReturnable<Boolean> ci) {
		if (world.dimension() == DMDimensions.TARDIS) {
			BlockState state = world.getBlockState(pos);
			TardisData data;
						
			if (world.isClientSide()) data = ClientTardisCache.getTardisData(pos);
			else data = DMTardis.getTardisFromInteriorPos(pos);
						
			if (data != null && ((ITardisDataDuck) data).get().activeCreativeBlocks > 0 && !Permissions.isAllowed(data, _this.getUUID(), state)) {
				_this.displayClientMessage(Register.NOT_ALLOWED.withStyle(TextFormatting.YELLOW), true);
				
				if (world.isClientSide() && state.is(DMTags.Blocks.TARDIS_CONTROLS)) {
					world.addParticle(ParticleTypes.LAVA, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 0, 0, 0);
					_this.animateHurt();
				}
				
				ci.setReturnValue(true);
				ci.cancel();
			}
		}

	}

}
