// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_205.mixins;

import static com.swdteam.common.block.tardis.WaypointPanelBlock.CARTRIDGE_TYPE;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.swdteam.common.block.tardis.WaypointPanelBlock;
import com.swdteam.common.init.DMDimensions;
import com.swdteam.common.init.DMTardis;
import com.swdteam.common.item.DataModuleItem;
import com.swdteam.common.tardis.TardisData;
import com.swdteam.common.tileentity.tardis.WaypointPanelTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

@Mixin(WaypointPanelBlock.class)
public class WaypointPanelBlockMixin extends Block {
	public WaypointPanelBlockMixin(Properties _0) {
		super(_0);
	}

	@Inject(at = @At("HEAD"), method = "use")
	public void use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit, CallbackInfoReturnable<ActionResultType> ci) {
		if (hand == Hand.MAIN_HAND && !world.isClientSide() && !player.isShiftKeyDown() && state.getValue(CARTRIDGE_TYPE) != 0 && world.dimension().equals(DMDimensions.TARDIS)) {
			TileEntity te = world.getBlockEntity(pos);
			if (te != null && te instanceof WaypointPanelTileEntity) {
				WaypointPanelTileEntity tile = (WaypointPanelTileEntity) te;
				if (tile.cartridge != null && tile.cartridge.getItem() instanceof DataModuleItem) {
					ItemStack stack = tile.cartridge;
					if (stack.hasTag() && stack.getOrCreateTag().contains("location") && stack.getTag().contains("Chameleon")) {
						TardisData data = DMTardis.getTardisFromInteriorPos(pos);
						String chameleon = stack.getTag().getString("Chameleon");
						if (data != null && data.getUnlockedExteriors().contains(chameleon)) {
							data.setExterior(chameleon);
							
							if (stack.getTag().contains("ChameleonSkin")) data.setSkinID(stack.getTag().getInt("ChameleonSkin"));	
							else data.setSkinID(0);
						}
					}
				}
			}
		}
	}
}
