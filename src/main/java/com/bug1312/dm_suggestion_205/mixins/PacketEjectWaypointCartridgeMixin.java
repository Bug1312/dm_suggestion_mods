// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_205.mixins;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.swdteam.common.init.DMDimensions;
import com.swdteam.common.init.DMItems;
import com.swdteam.common.init.DMTardis;
import com.swdteam.common.tardis.TardisData;
import com.swdteam.common.tileentity.tardis.DataWriterTileEntity;
import com.swdteam.network.NetworkHandler;
import com.swdteam.network.packets.PacketEjectWaypointCartridge;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketDirection;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent.Context;

@Mixin(PacketEjectWaypointCartridge.class)
public class PacketEjectWaypointCartridgeMixin {

	@Inject(at = @At("HEAD"), method = "handle", cancellable = true, remap = false)
	private static void handle(PacketEjectWaypointCartridge _msg, Supplier<Context> ctx, CallbackInfo ci) {
		PacketEjectWaypointCartridgeAccessor msg = (PacketEjectWaypointCartridgeAccessor) _msg;
		ctx.get().enqueueWork(() -> {
			if (ctx.get().getNetworkManager().getDirection() == PacketDirection.SERVERBOUND && msg.getName().startsWith("/")) {
				ServerPlayerEntity player = ctx.get().getSender();
				ServerWorld world = player.getLevel();
				if (world != null && world.dimension() == DMDimensions.TARDIS) {
					TileEntity te = world.getBlockEntity(msg.getBlockPos());
					TardisData data = DMTardis.getTardisFromInteriorPos(msg.getBlockPos());
					if (te != null && te instanceof DataWriterTileEntity && data != null) {
						DataWriterTileEntity writer = (DataWriterTileEntity) te;
						ItemStack stack = writer.cartridge;
						if (stack != null && stack.getItem() == DMItems.DATA_MODULE_GOLD.get()) {
							String[] args = msg.getName().split(" ");
							if (args[0].equalsIgnoreCase("/chameleon")) {
								CompoundNBT tag = stack.getOrCreateTag();
								tag.putString("Chameleon", data.getTardisExterior().getRegName());
								tag.putInt("ChameleonSkin", data.getSkinID());
								stack.setTag(tag);

								NetworkHandler.sendTo(player, new PacketEjectWaypointCartridge(new TranslationTextComponent("data_writer.dm_suggestion_205.chameleon.success").getString(), BlockPos.ZERO));
								ci.cancel();
							}
						}
					}
				}
			}
		});
	}

}
