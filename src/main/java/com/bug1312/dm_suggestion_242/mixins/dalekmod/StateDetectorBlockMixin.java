package com.bug1312.dm_suggestion_242.mixins.dalekmod;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.swdteam.common.block.AbstractRotateableWaterLoggableBlock;
import com.swdteam.common.block.tardis.StateDetectorBlock;
import com.swdteam.common.init.DMDimensions;
import com.swdteam.common.init.DMTardis;
import com.swdteam.common.tardis.TardisData;
import com.swdteam.common.tileentity.TardisTileEntity;
import com.swdteam.common.tileentity.tardis.StateDetectorTileEntity;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

@Mixin(StateDetectorBlock.class)
public abstract class StateDetectorBlockMixin extends AbstractRotateableWaterLoggableBlock {
	public StateDetectorBlockMixin(Properties properties) { super(properties); }

	@Inject(at = @At("HEAD"), method = "tick", cancellable = true)
	public void tick(BlockState blockState, ServerWorld world, BlockPos blockPos, Random random, CallbackInfo ci) {
		if (world.getBlockEntity(blockPos) instanceof StateDetectorTileEntity && world.dimension().equals(DMDimensions.TARDIS)) {
			TardisData data = DMTardis.getTardisFromInteriorPos(blockPos);
			BlockPos exteriorPos = data.getCurrentLocation().getBlockPosition();
			ServerWorld exteriorWorld = world.getServer().getLevel(data.getCurrentLocation().dimensionWorldKey());
			TileEntity tile = exteriorWorld.getBlockEntity(exteriorPos);
			// Remember kids, never forget to check your data in every way because Java sucks lol
			if (tile != null && !(tile instanceof TardisTileEntity)) {
				world.getBlockTicks().scheduleTick(blockPos, this, 10);
				ci.cancel();
			}
		}
	}
}
