// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_98.screen;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.list.AbstractOptionList;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FilterPermissionList extends AbstractOptionList<FilterPermissionListEntry> {
	private final PermissionScreen permissionScreen;
	private final Minecraft minecraft;
	private final List<FilterPermissionListEntry> listEntries = Lists.newArrayList();
	@Nullable private String filter;
	
	public FilterPermissionList(PermissionScreen screen, Minecraft minecraft, int p_i244516_3_, int p_i244516_4_, int p_i244516_5_, int p_i244516_6_, int p_i244516_7_) {
		super(minecraft, p_i244516_3_, p_i244516_4_, p_i244516_5_, p_i244516_6_, p_i244516_7_);
		this.permissionScreen = screen;
		this.minecraft = minecraft;
		this.setRenderBackground(false);
		this.setRenderTopAndBottom(false);
	}

	public void render(MatrixStack stack, int mouseX, int mouseY, float frameTick) {
		double d0 = this.minecraft.getWindow().getGuiScale();
		RenderSystem.enableScissor((int)((double)this.getRowLeft() * d0), (int)((double)(this.height - this.y1) * d0), (int)((double)(this.getScrollbarPosition() + 6) * d0), (int)((double)(this.height - (this.height - this.y1) - this.y0 - 4) * d0));
		super.render(stack, mouseX, mouseY, frameTick);
		RenderSystem.disableScissor();	
	}

	public void updateTardisList(double scrollAmount) {
		Collection<UUID> tardises = PermissionScreen.ALLOWED;
		this.listEntries.clear();
		
		Set<UUID> uuids = Stream.concat(minecraft.player.connection.getOnlinePlayers().stream().map((p) -> p.getProfile().getId()), tardises.stream()).collect(Collectors.toSet());
		
		for(UUID info : uuids) {	
			NetworkPlayerInfo networkplayerinfo = this.minecraft.player.connection.getPlayerInfo(info);
			if (networkplayerinfo != null) {
				this.listEntries.add(new FilterPermissionListEntry(this.minecraft, this.permissionScreen, info, networkplayerinfo.getProfile().getName(), networkplayerinfo::getSkinLocation));
			} else {
				GameProfile profile = new GameProfile(info, null);
				profile = minecraft.getMinecraftSessionService().fillProfileProperties(profile, false);
				profile = SkullTileEntity.updateGameprofile(profile);
				
				String name = profile.getName();
				Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> skinMap = minecraft.getSkinManager().getInsecureSkinInformation(profile);
				Supplier<ResourceLocation> skin = skinMap.containsKey(MinecraftProfileTexture.Type.SKIN) ? () -> minecraft.getSkinManager().registerTexture(skinMap.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN) : null;

				this.listEntries.add(new FilterPermissionListEntry(this.minecraft, this.permissionScreen, info, name, skin));
			}
		}

		UUID currentTardis = minecraft.player.getUUID();
		
		this.updateFilteredTardises();
		this.listEntries.sort((a, b) -> {
			if (a.getID() == currentTardis && b.getID() != currentTardis) return -1;
			else if (b.getID() == currentTardis && a.getID() != currentTardis) return 1;
			else return a.getOwnerName().compareToIgnoreCase(b.getOwnerName());
		});
		this.replaceEntries(this.listEntries);
		this.setScrollAmount(scrollAmount);
	}

	private void updateFilteredTardises() {
		if (this.filter != null) {
			this.listEntries.removeIf((entry) -> !entry.getOwnerName().toLowerCase(Locale.ROOT).contains(this.filter));
			this.replaceEntries(this.listEntries);
		}

	}

	public void setFilter(String search) {
		this.filter = search;
	}

	public boolean isEmpty() {
		return this.listEntries.isEmpty();
	}
}