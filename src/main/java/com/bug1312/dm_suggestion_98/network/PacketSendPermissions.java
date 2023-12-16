// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_98.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.UUID;
import java.util.function.Supplier;

import com.bug1312.dm_suggestion_98.screen.PermissionScreen;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

// Server -> Client
public class PacketSendPermissions {

	private UUID[] data;

	public PacketSendPermissions(UUID[] data) {
		this.data = data;
	}

	public static void encode(PacketSendPermissions msg, PacketBuffer buf) {
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

	public static PacketSendPermissions decode(PacketBuffer buf) {
		UUID[] data = {};

		if(buf.readableBytes() > 0) {
			try {
				ByteArrayInputStream byteStream = new ByteArrayInputStream(buf.readByteArray());

				ObjectInputStream objectStream = new ObjectInputStream(byteStream);
				Object object = objectStream.readObject();
				
				if (object != null && object instanceof UUID[]) data = (UUID[]) object;
			} catch (IOException | ClassNotFoundException err) {
				err.printStackTrace();
			}
		}
		
		return new PacketSendPermissions(data);
	}

	public static void handle(PacketSendPermissions msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			PermissionScreen.ALLOWED.clear();
			PermissionScreen.ALLOWED.addAll(Arrays.asList(msg.data));
			PermissionScreen.mustUpdate = true;
		});

		ctx.get().setPacketHandled(true);
	}

}
