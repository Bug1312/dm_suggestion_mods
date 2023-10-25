// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_7.call;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.swdteam.common.init.DMTardis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.list.AbstractOptionList;
import net.minecraft.client.network.play.NetworkPlayerInfo;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import network.CallInfo;

@OnlyIn(Dist.CLIENT)
public class FilterCallList extends AbstractOptionList<FilterCallListEntry> {
	private final CallScreen callScreen;
	private final Minecraft minecraft;
	private final List<FilterCallListEntry> tardises = Lists.newArrayList();
	@Nullable private String filter;
	
	public FilterCallList(CallScreen screen, Minecraft minecraft, int p_i244516_3_, int p_i244516_4_, int p_i244516_5_, int p_i244516_6_, int p_i244516_7_) {
		super(minecraft, p_i244516_3_, p_i244516_4_, p_i244516_5_, p_i244516_6_, p_i244516_7_);
		this.callScreen = screen;
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

	public void updateTardisList(Collection<CallInfo> tardises, double scrollAmount) {
		this.tardises.clear();
		
		for(CallInfo info : tardises) {			
			NetworkPlayerInfo networkplayerinfo = this.minecraft.player.connection.getPlayerInfo(info.owner);
			if (networkplayerinfo != null) {
				this.tardises.add(new FilterCallListEntry(this.minecraft, this.callScreen, info.id, networkplayerinfo.getProfile().getName(), networkplayerinfo::getSkinLocation));
			} else {
				GameProfile profile = new GameProfile(info.owner, null);
				profile = minecraft.getMinecraftSessionService().fillProfileProperties(profile, false);
				profile = SkullTileEntity.updateGameprofile(profile);
				
		        String name = profile.getName();
				Map<Type, MinecraftProfileTexture> skinMap = minecraft.getSkinManager().getInsecureSkinInformation(profile);
				Supplier<ResourceLocation> skin = skinMap.containsKey(Type.SKIN) ? () -> minecraft.getSkinManager().registerTexture(skinMap.get(Type.SKIN), Type.SKIN) : null;

				this.tardises.add(new FilterCallListEntry(this.minecraft, this.callScreen, info.id, name, skin));
			}
		}

		BlockPos playerPos = minecraft.player.blockPosition();
		int currentTardis = DMTardis.getIDForXZ(playerPos.getX(), playerPos.getZ());
		
		this.updateFilteredTardises();
		this.tardises.sort((a, b) -> {
		    if (a.getID() == currentTardis && b.getID() != currentTardis) return -1;
		    else if (b.getID() == currentTardis && a.getID() != currentTardis) return 1;
		    else return a.getOwnerName().compareToIgnoreCase(b.getOwnerName());
		});
		this.replaceEntries(this.tardises);
		this.setScrollAmount(scrollAmount);
	}

	private void updateFilteredTardises() {
		if (this.filter != null) {
			this.tardises.removeIf((entry) -> {
				return !entry.getOwnerName().toLowerCase(Locale.ROOT).contains(this.filter);
			});
			this.replaceEntries(this.tardises);
		}

	}

	public void setFilter(String search) {
		this.filter = search;
	}

	public boolean isEmpty() {
		return this.tardises.isEmpty();
	}
}