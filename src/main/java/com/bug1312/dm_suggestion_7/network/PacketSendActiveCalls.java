// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_7.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import com.bug1312.dm_suggestion_7.VoicePlugin;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketSendActiveCalls {

	private Map<UUID, Integer> data;

	public PacketSendActiveCalls(Map<UUID, Integer> data) {
		this.data = data;
	}

	public static void encode(PacketSendActiveCalls msg, PacketBuffer buf) {
		try {
			ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

			ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
			objectStream.writeObject(msg.data);
			objectStream.close();

			buf.writeByteArray(byteStream.toByteArray());
		} catch (IOException err) {
			err.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static PacketSendActiveCalls decode(PacketBuffer buf) {
		Map<UUID, Integer> data = new HashMap<>();

		if(buf.readableBytes() > 0) {
			try {
				ByteArrayInputStream byteStream = new ByteArrayInputStream(buf.readByteArray());

				ObjectInputStream objectStream = new ObjectInputStream(byteStream);
				Object object = objectStream.readObject();
				
				if (object != null && object instanceof Map<?,?>) data = (Map<UUID, Integer>) object;
			} catch (IOException | ClassNotFoundException err) {
				err.printStackTrace();
			}
		}
				
		return new PacketSendActiveCalls(data);
	}

	public static void handle(PacketSendActiveCalls msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			VoicePlugin.ACTIVE_CALLS.clear();
			VoicePlugin.ACTIVE_CALLS.putAll(msg.data);
		});

		ctx.get().setPacketHandled(true);
	}

}
