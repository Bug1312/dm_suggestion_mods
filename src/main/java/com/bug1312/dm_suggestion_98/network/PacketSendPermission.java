// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_98.network;

import java.util.UUID;
import java.util.function.Supplier;

import com.bug1312.dm_suggestion_98.data.ITardisDataDuck;
import com.bug1312.dm_suggestion_98.data.Permissions;
import com.swdteam.common.init.DMDimensions;
import com.swdteam.common.init.DMTardis;
import com.swdteam.common.tardis.TardisData;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

// Client -> Server
public class PacketSendPermission {

	private UUID uuid;
	private boolean add;

	public PacketSendPermission(UUID uuid, boolean add) {
		this.uuid = uuid;
		this.add = add;
	}

	public static void encode(PacketSendPermission msg, PacketBuffer buf) {
		buf.writeUUID(msg.uuid);
		buf.writeBoolean(msg.add);
	}

	public static PacketSendPermission decode(PacketBuffer buf) {
		return new PacketSendPermission(buf.readUUID(), buf.readBoolean());
	}

	public static void handle(PacketSendPermission msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayerEntity player = ctx.get().getSender();
			if (player.getLevel().dimension() != DMDimensions.TARDIS) return;
			TardisData data = DMTardis.getTardisFromInteriorPos(player.blockPosition());
			if (data == null) return;
			if (!data.getOwner_uuid().equals(player.getUUID())) return;
			Permissions perms = ((ITardisDataDuck) data).get();
			
			if (msg.add) perms.players.add(msg.uuid);
			else perms.players.remove(msg.uuid);
			
			data.save();
		});

		ctx.get().setPacketHandled(true);
	}

}
