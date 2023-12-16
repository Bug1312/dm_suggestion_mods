// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_98.screen;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.bug1312.dm_suggestion_98.ModMain;
import com.bug1312.dm_suggestion_98.Register;
import com.bug1312.dm_suggestion_98.network.PacketSendPermission;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.swdteam.main.DalekMod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.button.CheckboxButton;
import net.minecraft.client.gui.widget.list.AbstractOptionList;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FilterPermissionListEntry extends AbstractOptionList.Entry<FilterPermissionListEntry> {
	
	private static ResourceLocation[] MISSING_SKINS = new ResourceLocation[] {
		new ResourceLocation(DalekMod.MODID, "textures/gui/dmu/def_head_0.png"),	
		new ResourceLocation(DalekMod.MODID, "textures/gui/dmu/def_head_1.png"),	
		new ResourceLocation(DalekMod.MODID, "textures/gui/dmu/def_head_2.png"),	
		new ResourceLocation(DalekMod.MODID, "textures/gui/dmu/def_head_3.png")	
	};
	@Nullable private Integer randomMissing;

	private final UUID uuid;
	private final String name;
	@Nullable private final Supplier<ResourceLocation> skinGetter;
	@Nullable private final CheckboxButton toggleButton;
	
	private final Minecraft minecraft;
	private final List<IGuiEventListener> children;
	
	private static final int BG_FILL = ColorHelper.PackedColor.color(255, 74, 74, 74);
	private static final int PLAYERNAME_COLOR = ColorHelper.PackedColor.color(255, 255, 255, 255);

	public FilterPermissionListEntry(Minecraft minecraft, PermissionScreen screen, UUID uuid, String name, Supplier<ResourceLocation> skinSupplier) {
		this.minecraft = minecraft;
		this.uuid = uuid;
		this.name = name;
		this.skinGetter = skinSupplier;
		
		if (!minecraft.player.getUUID().equals(uuid)) {
			this.toggleButton = new CheckboxButton(0, 0, 24, 20, Register.CHECKBOX, PermissionScreen.ALLOWED.contains(uuid), false) {
				@Override
				public void onPress() {
					super.onPress();
					ModMain.NETWORK.sendToServer(new PacketSendPermission(uuid, this.selected()));
				}
			};

			this.children = ImmutableList.of(this.toggleButton);
		} else {
			this.toggleButton = null;
			this.children = ImmutableList.of();
		}
	}

	@Override
	public void render(MatrixStack stack, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
		int i = x + 4;
		int j = y + (entryHeight - 24) / 2;
		int k = i + 24 + 4;
		AbstractGui.fill(stack, x, y, x + entryWidth, y + entryHeight, BG_FILL);
		int l = y + (entryHeight - 9) / 2;

		if (this.skinGetter != null) {
			this.minecraft.getTextureManager().bind(this.skinGetter.get());
			AbstractGui.blit(stack, i, j, 24, 24, 8, 8, 8, 8, 64, 64);
			RenderSystem.enableBlend();
			AbstractGui.blit(stack, i, j, 24, 24, 40, 8, 8, 8, 64, 64);
			RenderSystem.disableBlend();
		} else {
			if (this.randomMissing == null) this.randomMissing = new Random().nextInt(MISSING_SKINS.length);
			this.minecraft.getTextureManager().bind(MISSING_SKINS[randomMissing]);
			AbstractGui.blit(stack, i, j, 24, 24, 0F, 0F, 8, 8, 8, 8);
		}
		
		this.minecraft.font.draw(stack, this.name, (float)k, (float)l, PLAYERNAME_COLOR);

		if (this.toggleButton != null) {
			this.toggleButton.x = x + (entryWidth - this.toggleButton.getWidth() - 4);
			this.toggleButton.y = y + (entryHeight - this.toggleButton.getHeight()) / 2;
			this.toggleButton.render(stack, mouseX, mouseY, tickDelta);
		}
	}

	public List<? extends IGuiEventListener> children() {
		return this.children;
	}
	
	public String getOwnerName() {
		return this.name;
	}
	
	public UUID getID() {
		return this.uuid;
	}

}