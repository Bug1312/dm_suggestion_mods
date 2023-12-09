// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_242.mixins.dalekmod;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.bug1312.dm_suggestion_242.ITardisDataDuck;
import com.bug1312.dm_suggestion_242.tardis.SiegeTardisTileEntity;
import com.swdteam.common.init.DMNBTKeys;
import com.swdteam.common.init.DMTardis;
import com.swdteam.common.item.StattenheimRemoteItem;
import com.swdteam.common.tardis.TardisData;

import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

@Mixin(StattenheimRemoteItem.class)
public class StattenheimRemoteItemMixin {

	@Inject(at = @At("HEAD"), method = "useOn")
	public void useOn(ItemUseContext context, CallbackInfoReturnable<ActionResultType> ci) {
		if (!context.getLevel().isClientSide()) {
			ItemStack stack = context.getItemInHand();
			if (stack.getOrCreateTag().contains(DMNBTKeys.LINKED_ID)) {
				int id = context.getItemInHand().getTag().getInt(DMNBTKeys.LINKED_ID);
				TardisData data = DMTardis.getTardis(id);
				
				if (data != null && !data.isInFlight() && ((ITardisDataDuck)data).get().isPlaced) {
					ServerWorld world = context.getLevel().getServer().getLevel(data.getCurrentLocation().dimensionWorldKey());
					BlockPos pos = data.getCurrentLocation().getBlockPosition();
					TileEntity te = world.getBlockEntity(pos);
					if (te instanceof SiegeTardisTileEntity) {
						((SiegeTardisTileEntity) te).sendUpdates();
						world.setBlockAndUpdate(pos, world.getBlockState(pos).getFluidState().createLegacyBlock());
					}
				}
			}
		}
	}
}
