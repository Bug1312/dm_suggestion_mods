// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_98.network;

import java.util.UUID;
import java.util.function.Supplier;

import com.bug1312.dm_suggestion_98.ModMain;
import com.bug1312.dm_suggestion_98.data.ITardisDataDuck;
import com.bug1312.dm_suggestion_98.data.Permissions;
import com.swdteam.common.init.DMDimensions;
import com.swdteam.common.init.DMTardis;
import com.swdteam.common.tardis.TardisData;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

// Client -> Server
public class PacketRequestPermissions {
	
	public PacketRequestPermissions() {}
	public static void encode(PacketRequestPermissions msg, PacketBuffer buf) {}
	public static PacketRequestPermissions decode(PacketBuffer buf) { return new PacketRequestPermissions(); }

	public static void handle(PacketRequestPermissions msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayerEntity player = ctx.get().getSender();
			if (player.getLevel().dimension() != DMDimensions.TARDIS) return;
			TardisData data = DMTardis.getTardisFromInteriorPos(player.blockPosition());
			if (data == null) return;
			Permissions perms = ((ITardisDataDuck) data).get();
						
			ModMain.NETWORK.send(PacketDistributor.PLAYER.with(() -> ctx.get().getSender()), new PacketSendPermissions(perms.players.toArray(new UUID[perms.players.size()])));		
		});

		ctx.get().setPacketHandled(true);
	}
}
