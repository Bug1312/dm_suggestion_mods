// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_525.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.swdteam.common.init.DMDimensions;
import com.swdteam.common.init.DMNBTKeys;
import com.swdteam.common.init.DMSoundEvents;
import com.swdteam.common.item.StattenheimRemoteItem;
import com.swdteam.common.tardis.TardisData;
import com.swdteam.common.tardis.TardisState;
import com.swdteam.common.tardis.actions.TardisActionList;
import com.swdteam.common.tardis.data.TardisFlightPool;
import com.swdteam.common.tileentity.TardisTileEntity;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

@Mixin(StattenheimRemoteItem.class)
public class StattenheimRemoteItemMixin extends Item {
	public StattenheimRemoteItemMixin(Properties _0) { super(_0); }

	@Inject(at = @At("HEAD"), method = "useOn", cancellable = true)
	public void useOn(ItemUseContext context, CallbackInfoReturnable<ActionResultType> ci) {
		ItemStack stack = context.getItemInHand();
		if (stack.getOrCreateTag().contains(DMNBTKeys.LINKED_ID)) {
			BlockPos pos = context.getClickedPos();
			World world = context.getLevel();
			
			TileEntity te = world.getBlockEntity(pos);
			if (!world.isClientSide() && te instanceof TardisTileEntity) {
				TardisTileEntity tile = (TardisTileEntity) te;
				
				if (tile.globalID == stack.getTag().getInt(DMNBTKeys.LINKED_ID)) {
					TardisData data = tile.tardisData;
					
					if (data != null) {
						MinecraftServer server = world.getServer();
						ServerWorld interiorDim = server.getLevel(DMDimensions.TARDIS);
	
						// If interior is loaded, play demat noise
						if (interiorDim.isLoaded(data.getInteriorSpawnPosition().toBlockPos())) {
							interiorDim.playSound(null, data.getInteriorSpawnPosition().toBlockPos(), DMSoundEvents.TARDIS_DEMAT.get(), SoundCategory.BLOCKS, 1, 1);
						}
	
						if (TardisActionList.doAnimation((ServerWorld) world, pos)) {
							world.playSound(null, pos, DMSoundEvents.TARDIS_DEMAT.get(), SoundCategory.BLOCKS, 1, 1);
							tile.setState(TardisState.DEMAT);
						} else world.setBlockAndUpdate(pos, world.getFluidState(pos).createLegacyBlock());
	
						data.setPreviousLocation(data.getCurrentLocation());
						data.save();
						TardisFlightPool.addFlight(data);
						
						context.getPlayer().getCooldowns().addCooldown(this, 400);
						stack.hurtAndBreak(1, context.getPlayer(), player -> player.broadcastBreakEvent(context.getHand()));

						ci.setReturnValue(ActionResultType.sidedSuccess(world.isClientSide()));
						ci.cancel();
					}
				}
			}
		}
	}

}
