// Copyright 2023 Bug1312 (bug@bug1312.com)

package network;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import com.bug1312.dm_suggestion_7.ModMain;
import com.bug1312.dm_suggestion_7.Register;
import com.bug1312.dm_suggestion_7.TardisHelper;
import com.bug1312.dm_suggestion_7.VoicePlugin;
import com.swdteam.common.init.DMDimensions;
import com.swdteam.common.tileentity.tardis.PoliceBoxDoorsTileEntity;

import de.maxhenkel.voicechat.api.Group;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class PacketRequestCall {

	int callee;
	
	public PacketRequestCall(int callee) {
		this.callee = callee;
	}
	
	public static void encode(PacketRequestCall msg, PacketBuffer buf) {
		buf.writeInt(msg.callee);
	}
	
	public static PacketRequestCall decode(PacketBuffer buf) { 
		return new PacketRequestCall(buf.readInt());
	}

	public static void handle(PacketRequestCall msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayerEntity callerPlayer = ctx.get().getSender();
			MinecraftServer server = callerPlayer.getServer();
			
			List<PoliceBoxDoorsTileEntity> calleeDoors = TardisHelper.getTilesInTardis(msg.callee, server.getLevel(DMDimensions.TARDIS), PoliceBoxDoorsTileEntity.class);

			for (PoliceBoxDoorsTileEntity door: calleeDoors) {
				door.getLevel().playSound(null, door.getBlockPos(), Register.RINGTONE.get(), SoundCategory.PLAYERS, 1.0F, 1.0F);
			}
			
			Group group = VoicePlugin.voicechatServerApi.groupBuilder()
			        .setPersistent(false)
			        .setName("TARDIS Call")
			        .setPassword(""+new Random().nextLong())
			        .setType(Group.Type.NORMAL)
			        .build();
			
			VoicechatConnection connection = VoicePlugin.voicechatServerApi.getConnectionOf(callerPlayer.getUUID());
			if (connection != null) connection.setGroup(group);
			
			VoicePlugin.ACTIVE_CALLS.put(group.getId(), msg.callee);
			ModMain.NETWORK.send(PacketDistributor.ALL.noArg(), new PacketSendAddCall(group.getId(), msg.callee));
		});

		ctx.get().setPacketHandled(true);
	}
}

