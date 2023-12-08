// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_77.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.swdteam.common.init.DMNBTKeys;
import com.swdteam.common.init.DMTardis;
import com.swdteam.common.item.TardisKeyItem;
import com.swdteam.common.tardis.Location;
import com.swdteam.common.tardis.TardisData;

import net.minecraft.command.arguments.EntityAnchorArgument.Type;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

@Mixin(Item.class)
public class ItemMixin {
	Item _this = (Item) ((Object) this);
	
	public long lastUsedOnBlock = -1;
		
	@Inject(at = @At("HEAD"), method = "useOn")
	public void useOn(ItemUseContext context, CallbackInfoReturnable<ActionResultType> ci) {
		this.lastUsedOnBlock = context.getLevel().getGameTime();
	}
	
	@Inject(at = @At("HEAD"), method = "use", cancellable = true)
	public void use(World world, PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult<ItemStack>> ci) {
		if (_this instanceof TardisKeyItem && !world.isClientSide() && world.getGameTime() != this.lastUsedOnBlock) {
			ItemStack stack = player.getItemInHand(hand);
						
			if (stack.getOrCreateTag().contains(DMNBTKeys.LINKED_ID)) {
				int id = stack.getTag().getInt(DMNBTKeys.LINKED_ID);
				TardisData data = DMTardis.getTardis(id);					
				if (!data.isInFlight()) {
					Location location = data.getCurrentLocation();
					if (location.dimensionWorldKey() == world.dimension()) {
						BlockPos pos = location.getBlockPosition();
						player.lookAt(Type.FEET, new Vector3d(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D));
						ci.setReturnValue(ActionResult.sidedSuccess(stack, world.isClientSide()));
						ci.cancel();
					}
				}
			}
		}
	}
	
}