package com.bug1312.dm_suggestion_92.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.bug1312.dm_suggestion_92.Register;
import com.swdteam.common.init.DMDimensions;
import com.swdteam.common.init.DMNBTKeys;
import com.swdteam.common.init.DMTardis;
import com.swdteam.common.item.TardisKeyItem;
import com.swdteam.common.tardis.Location;
import com.swdteam.common.tardis.TardisData;
import com.swdteam.common.tardis.TardisDoor;
import com.swdteam.common.tileentity.TardisTileEntity;
import com.swdteam.common.tileentity.TardisTileEntity.DoorSource;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

@Mixin(TardisKeyItem.class)
public class TardisKeyItemMixin extends Item {
	public TardisKeyItemMixin(Properties _0) { super(_0); }
	
	public long lastUsedOnBlock = -1;
		
	@Inject(at = @At("HEAD"), method = "useOn")
	public void useOn(ItemUseContext context, CallbackInfoReturnable<ActionResultType> ci) {
		this.lastUsedOnBlock = context.getLevel().getGameTime();
	}
	
	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		if (!world.isClientSide() && world.getGameTime() != this.lastUsedOnBlock) {
			ItemStack stack = player.getItemInHand(hand);
						
			if (stack.getOrCreateTag().contains(DMNBTKeys.LINKED_ID)) {
				int id = stack.getTag().getInt(DMNBTKeys.LINKED_ID);
				TardisData data = DMTardis.getTardis(id);
				if (data != null) {
					if (world.dimension() == DMDimensions.TARDIS) {
						if (DMTardis.getIDForXZ(player.blockPosition().getX(),player.blockPosition().getZ()) == id) {
							data.setLocked(!data.isLocked());
							if (data.isLocked()) data.setDoorOpen(false);
							data.save();
							
							world.playSound(null, player.blockPosition(), Register.BEEP_BEEP.get(), SoundCategory.PLAYERS, 1, 1);
							player.displayClientMessage((data.isLocked() ? Register.LOCKED : Register.UNLOCKED).withStyle(TextFormatting.GREEN), true);

							return ActionResult.sidedSuccess(stack, world.isClientSide()); 
						}
					}
					
					if (!data.isInFlight()) {
						Location location = data.getCurrentLocation();
						if (location.dimensionWorldKey() == world.dimension()) {
							BlockPos pos = location.getBlockPosition();
							double distance = player.position().distanceTo(new Vector3d(pos.getX(), pos.getY(), pos.getZ()));
							if (distance <= 15) {
								data.setLocked(!data.isLocked());
								if (data.isLocked()) {
									TileEntity te = world.getBlockEntity(pos);
									if (te instanceof TardisTileEntity) ((TardisTileEntity) te).closeDoor(TardisDoor.BOTH, DoorSource.TARDIS);
									
									data.setDoorOpen(false);
								}
								data.save();
								
								world.playSound(null, player.blockPosition(), Register.BEEP_BEEP.get(), SoundCategory.PLAYERS, 1, 1);
								player.displayClientMessage((data.isLocked() ? Register.LOCKED : Register.UNLOCKED).withStyle(TextFormatting.GREEN), true);

								return ActionResult.sidedSuccess(stack, world.isClientSide()); 
							}
						}
					}
				}
			}
		}
		return super.use(world, player, hand);
	}
	
}
