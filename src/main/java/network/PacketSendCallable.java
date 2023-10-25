// Copyright 2023 Bug1312 (bug@bug1312.com)

package network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketSendCallable {

	private CallInfo[] data;

	public PacketSendCallable(CallInfo[] data) {
		this.data = data;
	}

	public static void encode(PacketSendCallable msg, PacketBuffer buf) {
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

	public static PacketSendCallable decode(PacketBuffer buf) {
		CallInfo[] data = {};

		if(buf.readableBytes() > 0) {
			try {
				ByteArrayInputStream byteStream = new ByteArrayInputStream(buf.readByteArray());

				ObjectInputStream objectStream = new ObjectInputStream(byteStream);
				Object object = objectStream.readObject();
				
				if (object != null && object instanceof CallInfo[]) data = (CallInfo[]) object;
			} catch (IOException | ClassNotFoundException err) {
				err.printStackTrace();
			}
		}
		
		return new PacketSendCallable(data);
	}

	public static void handle(PacketSendCallable msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			CallInfo.CALL_LIST.clear();
			CallInfo.CALL_LIST.addAll(Arrays.asList(msg.data));
		});

		ctx.get().setPacketHandled(true);
	}

}
