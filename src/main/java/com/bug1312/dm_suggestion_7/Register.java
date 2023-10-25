// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_7;

import java.util.function.Supplier;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class Register {
	public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ModMain.MOD_ID);
	public static final RegistryObject<SoundEvent> RINGTONE = register(SOUNDS, "ringtone", () -> new SoundEvent(new ResourceLocation(ModMain.MOD_ID, "player.ringtone")));
	
	public static <T extends IForgeRegistryEntry<T>> RegistryObject<T> register(final DeferredRegister<T> register, final String name, final Supplier<? extends T> supplier) {
		return register.register(name, supplier);
	}
}
