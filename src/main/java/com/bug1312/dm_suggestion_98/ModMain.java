// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_98;

import com.bug1312.dm_suggestion_98.network.PacketRequestPermissions;
import com.bug1312.dm_suggestion_98.network.PacketSendPermission;
import com.bug1312.dm_suggestion_98.network.PacketSendPermissions;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

@Mod(ModMain.MOD_ID)
public class ModMain {
	public static final String MOD_ID = "dm_suggestion_98";
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel NETWORK = NetworkRegistry.ChannelBuilder
			.named(new ResourceLocation(MOD_ID, "main"))
			.clientAcceptedVersions(PROTOCOL_VERSION::equals)
			.serverAcceptedVersions(PROTOCOL_VERSION::equals)
			.networkProtocolVersion(() -> PROTOCOL_VERSION)
			.simpleChannel();

	public ModMain() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		
		modBus.addListener(this::commonSetup);

		Register.ITEMS.register(modBus);
		Register.BLOCKS.register(modBus);
	}
	
	private void commonSetup(final FMLCommonSetupEvent event) {		
		NETWORK.registerMessage(0, PacketRequestPermissions.class, PacketRequestPermissions::encode, PacketRequestPermissions::decode, PacketRequestPermissions::handle);
		NETWORK.registerMessage(1, PacketSendPermissions.class, PacketSendPermissions::encode, PacketSendPermissions::decode, PacketSendPermissions::handle);
		NETWORK.registerMessage(2, PacketSendPermission.class, PacketSendPermission::encode, PacketSendPermission::decode, PacketSendPermission::handle);
	}

}