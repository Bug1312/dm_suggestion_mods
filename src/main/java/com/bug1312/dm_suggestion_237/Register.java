// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_237;

import java.util.function.Supplier;

import com.google.common.collect.ImmutableSet;
import com.swdteam.common.init.DMBlocks;
import com.swdteam.common.init.DMSoundEvents;

import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.village.PointOfInterestType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class Register {
		
	public static final DeferredRegister<PointOfInterestType> POI_TYPE = DeferredRegister.create(ForgeRegistries.POI_TYPES, ModMain.MOD_ID);
	public static final RegistryObject<PointOfInterestType> ARS_POI = register(POI_TYPE, "ars_poi", () -> new PointOfInterestType("ars_poi", ImmutableSet.copyOf(DMBlocks.ARS.get().getStateDefinition().getPossibleStates()), 1, 1));
	
	public static final DeferredRegister<VillagerProfession> PROFESSIONS = DeferredRegister.create(ForgeRegistries.PROFESSIONS, ModMain.MOD_ID);
	public static final RegistryObject<VillagerProfession> TARDIS_PROFESSION = register(PROFESSIONS, "ars_villager", () -> new VillagerProfession("ars_villager", ARS_POI.get(), ImmutableSet.of(), ImmutableSet.of(DMBlocks.TARDIS.get(), DMBlocks.TARDIS_PLANT.get()), DMSoundEvents.ENTITY_STATTENHEIM_REMOTE_SYNC.get()));

	// Register Method
	public static <T extends IForgeRegistryEntry<T>, U extends T> RegistryObject<U> register(final DeferredRegister<T> register, final String name, final Supplier<U> supplier) {
		return register.register(name, supplier);
	}
	
}