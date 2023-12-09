// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_242.mixins.dalekmod.disable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.bug1312.dm_suggestion_242.ITardisDataDuck;
import com.bug1312.dm_suggestion_242.Register;
import com.swdteam.common.block.tardis.ArsBlock;
import com.swdteam.common.block.tardis.FlightPanelBlock;
import com.swdteam.common.block.tardis.TardisDoorBlock;
import com.swdteam.common.init.DMDimensions;
import com.swdteam.common.init.DMTardis;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

@Mixin({FlightPanelBlock.class, ArsBlock.class, TardisDoorBlock.class})
public class DisableBlocks {

	@Inject(at = @At("HEAD"), method = "use", cancellable = true)
	public void disableWithSiege(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result, CallbackInfoReturnable<ActionResultType> ci) {
		if (!world.isClientSide() && world.dimension() == DMDimensions.TARDIS) {
			ITardisDataDuck data = (ITardisDataDuck) DMTardis.getTardisFromInteriorPos(pos);
			if (data != null && data.get().inSiege()) {
				player.displayClientMessage(Register.SIEGE_ERROR.withStyle(TextFormatting.YELLOW), true);
				ci.setReturnValue(ActionResultType.FAIL);
				ci.cancel();
			}
		}
	}
}
