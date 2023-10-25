// Copyright 2023 Bug1312 (bug@bug1312.com)

package network;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import com.bug1312.dm_suggestion_7.ModMain;
import com.bug1312.dm_suggestion_7.TardisHelper;
import com.swdteam.common.init.DMDimensions;
import com.swdteam.common.init.DMTardis;
import com.swdteam.common.tileentity.tardis.PoliceBoxDoorsTileEntity;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

public class PacketRequestCallable {

	public PacketRequestCallable() {}
	public static void encode(PacketRequestCallable msg, PacketBuffer buf) {}
	public static PacketRequestCallable decode(PacketBuffer buf) { return new PacketRequestCallable(); }

	public static void handle(PacketRequestCallable msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayerEntity player = ctx.get().getSender();
			MinecraftServer server = player.getServer();
			List<ServerPlayerEntity> players = server.getLevel(DMDimensions.TARDIS).getPlayers(p -> p.isAlive() && p.getLevel().dimension().equals(DMDimensions.TARDIS));
			Set<Integer> ids = new HashSet<>();

			players.forEach(p -> ids.add(DMTardis.getIDForXZ(p.blockPosition().getX(), p.blockPosition().getZ())));
			
			Set<CallInfo> callInfos = new HashSet<>();
			
			for (Integer id : ids) {
				List<PoliceBoxDoorsTileEntity> tiles = TardisHelper.getTilesInTardis(id, server.getLevel(DMDimensions.TARDIS), PoliceBoxDoorsTileEntity.class);
				if (tiles.size() > 0) callInfos.add(new CallInfo(id, DMTardis.getTardis(id).getOwner_uuid()));
			}

			ModMain.NETWORK.send(PacketDistributor.PLAYER.with(() -> player), new PacketSendCallable(callInfos.toArray(new CallInfo[callInfos.size()])));		
		});

		ctx.get().setPacketHandled(true);
	}
}
