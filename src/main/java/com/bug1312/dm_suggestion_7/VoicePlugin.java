// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_7;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import com.bug1312.dm_suggestion_7.network.PacketSendRemoveCall;

import de.maxhenkel.voicechat.api.ForgeVoicechatPlugin;
import de.maxhenkel.voicechat.api.Group;
import de.maxhenkel.voicechat.api.VoicechatApi;
import de.maxhenkel.voicechat.api.VoicechatClientApi;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.events.ClientVoicechatInitializationEvent;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.RemoveGroupEvent;
import de.maxhenkel.voicechat.api.events.VoicechatServerStartedEvent;
import net.minecraftforge.fml.network.PacketDistributor;

@ForgeVoicechatPlugin
public class VoicePlugin implements VoicechatPlugin {

	public static final Map<UUID, Integer> ACTIVE_CALLS = new HashMap<>();
    public static VoicechatApi voicechatApi;
	@Nullable public static VoicechatServerApi voicechatServerApi;
	@Nullable public static VoicechatClientApi voicechatClientApi;
	
	@Override
	public String getPluginId() {
		return ModMain.MOD_ID;
	}

	@Override
	public void initialize(VoicechatApi api) {
		voicechatApi = api;
	}
	
	@Override
	public void registerEvents(EventRegistration registration) {
		registration.registerEvent(RemoveGroupEvent.class, this::closeCall);
		registration.registerEvent(VoicechatServerStartedEvent.class, this::serverStart);
		registration.registerEvent(ClientVoicechatInitializationEvent.class, this::clientStart);
	}
	
    private void serverStart(VoicechatServerStartedEvent event) {
    	voicechatServerApi = event.getVoicechat();
    }
    
    private void clientStart(ClientVoicechatInitializationEvent event) {
    	voicechatClientApi = event.getVoicechat();
    }
	
	public void closeCall(RemoveGroupEvent event) {
		Group group = event.getGroup();
		if (ACTIVE_CALLS.containsKey(group.getId())) {
			ACTIVE_CALLS.remove(group.getId());			
			ModMain.NETWORK.send(PacketDistributor.ALL.noArg(), new PacketSendRemoveCall(group.getId()));
		}
		
	}
	
	public static boolean callExists(int id) {
		return ACTIVE_CALLS.containsValue(id);
	}
	
	public static Optional<Group> getCallGroup(int id) {
		Optional<Entry<UUID, Integer>> result = ACTIVE_CALLS.entrySet().stream().filter(entry -> entry.getValue() == id).findAny();
		if (!result.isPresent()) return Optional.empty();
		return Optional.of(voicechatServerApi.getGroup(result.get().getKey()));
	}
	
}
