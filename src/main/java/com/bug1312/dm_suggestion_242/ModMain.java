// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_242;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.bug1312.dm_suggestion_242.tardis.SiegeTardisItem;
import com.swdteam.common.init.DMNBTKeys;
import com.swdteam.common.tardis.TardisData;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// No clue how to check modded containers, especially 
// since they don't need to be Container-s or IInventory-s
@Mod(ModMain.MOD_ID)
public class ModMain {
	public static final String MOD_ID = "dm_suggestion_242";
	public static final Map<UUID, Entity> ENTITY_MAP = new HashMap<>();

	public ModMain() {
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		IEventBus eventBus = MinecraftForge.EVENT_BUS;
		
		modBus.addListener(this::client);
		eventBus.addListener(SiegeTardisItem::onPickup);

		Register.ITEMS.register(modBus);
		Register.BLOCKS.register(modBus);
		Register.TILE_ENTITIES.register(modBus);
	}
	
	private void client(final FMLClientSetupEvent event) {
		RenderTypeLookup.setRenderLayer(Register.SIEGE_LEVER_BLOCK.get(), RenderType.cutout());
	}
	
	public static void setSiegeBlock(World world, BlockPos pos, TardisData data, float rotation) {
		world.setBlock(pos, Register.SIEGE_BLOCK.get().defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, world.getFluidState(pos).getType() == Fluids.WATER), 0);
		TileEntity tile = world.getBlockEntity(pos);
		if (tile != null) {
			CompoundNBT tag = tile.serializeNBT();
			tag.putInt(DMNBTKeys.TARDIS_ID, data.getGlobalID());
			tile.deserializeNBT(tag);
			
			((ITardisDataDuck) data).get().setPlacement();
			data.setPreviousLocation(data.getCurrentLocation());
			data.setCurrentLocation(pos, world.dimension());
			data.getCurrentLocation().setFacing(rotation);
			data.save();
		}
	}
	
}