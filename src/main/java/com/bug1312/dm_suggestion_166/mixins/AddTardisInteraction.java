// Copyright 2024 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_166.mixins;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.bug1312.dm_suggestion_166.SonicInteractionTardis;
import com.google.gson.JsonElement;
import com.swdteam.common.init.DMBlocks;
import com.swdteam.common.init.DMSonicRegistry;
import com.swdteam.common.sonic.datapack.SonicInteractionDataReloadListener;

import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

@Mixin(SonicInteractionDataReloadListener.class)
public class AddTardisInteraction {

	@Inject(at = @At("TAIL"), method = "apply", remap = false)
	protected void dm_suggestion_166$apply(Map<ResourceLocation, JsonElement> objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn, CallbackInfo ci) {
		DMSonicRegistry.SONIC_LOOKUP.put(DMBlocks.TARDIS.get(), new SonicInteractionTardis());
	}
	
}
