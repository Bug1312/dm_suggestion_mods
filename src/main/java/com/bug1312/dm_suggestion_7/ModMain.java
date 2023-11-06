// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_7;

import com.bug1312.dm_suggestion_7.network.PacketRequestCall;
import com.bug1312.dm_suggestion_7.network.PacketRequestCallable;
import com.bug1312.dm_suggestion_7.network.PacketSendActiveCalls;
import com.bug1312.dm_suggestion_7.network.PacketSendAddCall;
import com.bug1312.dm_suggestion_7.network.PacketSendCallable;
import com.bug1312.dm_suggestion_7.network.PacketSendRemoveCall;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@Mod(ModMain.MOD_ID)
public class ModMain {
	public static final String MOD_ID = "dm_suggestion_7";
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel NETWORK = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(MOD_ID, "main"))
			.clientAcceptedVersions(PROTOCOL_VERSION::equals)
			.serverAcceptedVersions(PROTOCOL_VERSION::equals)
			.networkProtocolVersion(() -> PROTOCOL_VERSION)
			.simpleChannel();
	
	public ModMain() {
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
		MinecraftForge.EVENT_BUS.register(ServerEvents.class);
		
		Register.SOUNDS.register(FMLJavaModLoadingContext.get().getModEventBus());

	}
	
	private void commonSetup(final FMLCommonSetupEvent event) {		
		NETWORK.registerMessage(0, PacketRequestCallable.class, PacketRequestCallable::encode, PacketRequestCallable::decode, PacketRequestCallable::handle);
		NETWORK.registerMessage(1, PacketSendCallable.class, PacketSendCallable::encode, PacketSendCallable::decode, PacketSendCallable::handle);
		NETWORK.registerMessage(2, PacketRequestCall.class, PacketRequestCall::encode, PacketRequestCall::decode, PacketRequestCall::handle);
		NETWORK.registerMessage(3, PacketSendActiveCalls.class, PacketSendActiveCalls::encode, PacketSendActiveCalls::decode, PacketSendActiveCalls::handle);
		NETWORK.registerMessage(4, PacketSendAddCall.class, PacketSendAddCall::encode, PacketSendAddCall::decode, PacketSendAddCall::handle);
		NETWORK.registerMessage(5, PacketSendRemoveCall.class, PacketSendRemoveCall::encode, PacketSendRemoveCall::decode, PacketSendRemoveCall::handle);
	}

	@Mod.EventBusSubscriber(modid = MOD_ID, bus = Bus.FORGE, value = Dist.DEDICATED_SERVER)
	public static class ServerEvents {
		
		// There should be something better with payload events and 
		// FMLHandshakeHandler but old Discord msgs and docs aren't helpful
		@SubscribeEvent
		public static void playerJoined(EntityJoinWorldEvent event) {
			if (event.getEntity() instanceof ServerPlayerEntity) {
				ModMain.NETWORK.send(PacketDistributor.PLAYER.with(() -> (ServerPlayerEntity) event.getEntity()), new PacketSendActiveCalls(VoicePlugin.ACTIVE_CALLS));		
			}
		}
	}
}
