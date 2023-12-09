// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_242.mixins.dalekmod;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.bug1312.dm_suggestion_242.ITardisDataDuck;
import com.bug1312.dm_suggestion_242.Register;
import com.bug1312.dm_suggestion_242.SiegeData;
import com.swdteam.common.tardis.TardisData;
import com.swdteam.common.tardis.actions.TardisActionList;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

@Mixin(TardisActionList.class)
public class TardisActionListMixin {
	
	@Inject(at = @At("RETURN"), method = "Lcom/swdteam/common/tardis/actions/TardisActionList;demat(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/world/World;Lcom/swdteam/common/tardis/TardisData;Z)Z", remap = false)
	private static void demat(PlayerEntity player, World world, TardisData data, boolean showMessage, CallbackInfoReturnable<Boolean> ci) {
		if (ci.getReturnValueZ()) {
			BlockPos pos = data.getCurrentLocation().getBlockPosition();
			ServerWorld serverWorld = world.getServer().getLevel(data.getCurrentLocation().dimensionWorldKey());

			SiegeData siegeData = ((ITardisDataDuck) data).get();
			siegeData.resetPlacement();

			if (serverWorld != null) {
				BlockState state = serverWorld.getBlockState(pos);
				if (state.getBlock() == Register.SIEGE_BLOCK.get()) serverWorld.setBlockAndUpdate(pos, state.getFluidState().createLegacyBlock());
			}
		}
	}

}
