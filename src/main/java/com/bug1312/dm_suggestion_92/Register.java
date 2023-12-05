package com.bug1312.dm_suggestion_92;

import java.util.function.Supplier;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class Register {

	// Sounds
	public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, ModMain.MOD_ID);
	public static final RegistryObject<SoundEvent> BEEP_BEEP = register(SOUND_EVENTS, "player.beep", () -> new SoundEvent(new ResourceLocation(ModMain.MOD_ID, "player.beep")));

    // Translations
	public static final TranslationTextComponent LOCKED = new TranslationTextComponent(String.format("tooltip.%s.locked", ModMain.MOD_ID));
	public static final TranslationTextComponent UNLOCKED = new TranslationTextComponent(String.format("tooltip.%s.unlocked", ModMain.MOD_ID));
	
    // Register Method
	public static <T extends IForgeRegistryEntry<T>, U extends T> RegistryObject<U> register(final DeferredRegister<T> register, final String name, final Supplier<U> supplier) {
		return register.register(name, supplier);
	}
}
