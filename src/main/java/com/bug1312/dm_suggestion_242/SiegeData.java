// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_242;

import java.io.Serializable;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class SiegeData implements Serializable {
	private static final long serialVersionUID = 7767081169729404644L;

	@SerializedName("active")
	private boolean active = false;
	
	@SerializedName("is_placed")
	public boolean isPlaced = false;
	
	@Nullable
	@SerializedName("item_entity_uuid")
	public UUID entityUUID;
	
	@Nullable
	@SerializedName("item_entity_dim")
	private String entityDimension;
	
	@Nullable
	@SerializedName("holder_uuid")
	public UUID holderUUID;
	
	public boolean inSiege() { return active; }
	
	@Nullable
	public ServerPlayerEntity getHolder(World world) {
		if (world.isClientSide() || holderUUID == null) return null;
		return world.getServer().getPlayerList().getPlayer(holderUUID);
	}
	
	@Nullable
	public ItemEntity getEntity(World world) {
		if (world.isClientSide() || entityUUID == null || entityDimension == null) return null;
		Entity entity = world.getServer().getLevel(RegistryKey.create(Registry.DIMENSION_REGISTRY, new ResourceLocation(entityDimension))).getEntity(entityUUID);
		if (entity instanceof ItemEntity) return (ItemEntity) entity;
		return null;
	}
	
	public void setPlacement(ItemEntity entity) {
		this.resetPlacement();
		this.setEntity(entity);
	}
	
	public void setPlacement(PlayerEntity player) {
		this.resetPlacement();
		holderUUID = player.getUUID();
	}
	
	public void setPlacement() {
		this.resetPlacement();
		isPlaced = true;
	}
	
	public void setSiege(boolean flag) { 
		if (flag == false) this.resetPlacement();
		active = flag;
	}
	
	public void resetPlacement() {
		holderUUID = null;
		isPlaced = false;
		this.setEntity(null);
	}
	
	private void setEntity(ItemEntity entity) {
		if (entity == null) {
			entityUUID = null;
			entityDimension = null;
		} else {
			entityUUID = entity.getUUID();
			entityDimension = entity.level.dimension().location().toString();
		}
	}
	
}
