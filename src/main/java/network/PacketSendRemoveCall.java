// Copyright 2023 Bug1312 (bug@bug1312.com)

package network;

import java.util.UUID;
import java.util.function.Supplier;

import com.bug1312.dm_suggestion_7.VoicePlugin;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class PacketSendRemoveCall {

	private UUID uuid;
	
	public PacketSendRemoveCall(UUID uuid) {
		this.uuid = uuid;
	}

	public static void encode(PacketSendRemoveCall msg, PacketBuffer buf) {
		buf.writeUUID(msg.uuid);
	}	

	public static PacketSendRemoveCall decode(PacketBuffer buf) {				
		return new PacketSendRemoveCall(buf.readUUID());
	}

	public static void handle(PacketSendRemoveCall msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			VoicePlugin.ACTIVE_CALLS.remove(msg.uuid);
		});

		ctx.get().setPacketHandled(true);
	}

}
