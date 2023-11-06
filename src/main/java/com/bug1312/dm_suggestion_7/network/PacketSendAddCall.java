// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_7.network;

import java.util.UUID;
import java.util.function.Supplier;

import com.bug1312.dm_suggestion_7.VoicePlugin;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketSendAddCall {

	private UUID uuid;
	private int id;
	
	public PacketSendAddCall(UUID uuid, int id) {
		this.uuid = uuid;
		this.id = id;
	}

	public static void encode(PacketSendAddCall msg, PacketBuffer buf) {
		buf.writeUUID(msg.uuid);
		buf.writeInt(msg.id);
	}

	public static PacketSendAddCall decode(PacketBuffer buf) {				
		return new PacketSendAddCall(buf.readUUID(), buf.readInt());
	}

	public static void handle(PacketSendAddCall msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			VoicePlugin.ACTIVE_CALLS.put(msg.uuid, msg.id);
		});

		ctx.get().setPacketHandled(true);
	}

}
