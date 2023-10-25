// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_7.call;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import com.bug1312.dm_suggestion_7.ModMain;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.swdteam.common.init.DMTardis;
import com.swdteam.main.DalekMod;

import de.maxhenkel.voicechat.Voicechat;
import de.maxhenkel.voicechat.gui.widgets.ToggleImageButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.widget.list.AbstractOptionList;
import net.minecraft.util.ColorHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import network.PacketRequestCall;

@OnlyIn(Dist.CLIENT)
public class FilterCallListEntry extends AbstractOptionList.Entry<FilterCallListEntry> {
	
	private static ResourceLocation[] MISSING_SKINS = new ResourceLocation[] {
		new ResourceLocation(DalekMod.MODID, "textures/gui/dmu/def_head_0.png"),	
		new ResourceLocation(DalekMod.MODID, "textures/gui/dmu/def_head_1.png"),	
		new ResourceLocation(DalekMod.MODID, "textures/gui/dmu/def_head_2.png"),	
		new ResourceLocation(DalekMod.MODID, "textures/gui/dmu/def_head_3.png")	
	};
	@Nullable private Integer randomMissing;

	private final int tardisID;
	private final String ownerName;
	private final ITextComponent ownerNameText;
	@Nullable private final Supplier<ResourceLocation> skinGetter;
	@Nullable private final ToggleImageButton callButton;
	
	private final Minecraft minecraft;
	private final List<IGuiEventListener> children;
	
	private static final ITextComponent CALL_TEXT = (new TranslationTextComponent("gui.dm_suggestion_7.call.call_button"));
	private static final int BG_FILL = ColorHelper.PackedColor.color(255, 74, 74, 74);
	private static final int PLAYERNAME_COLOR = ColorHelper.PackedColor.color(255, 255, 255, 255);
    private static final ResourceLocation SPEAKER = new ResourceLocation(Voicechat.MODID, "textures/icons/speaker_button.png");

	public FilterCallListEntry(Minecraft minecraft, CallScreen screen, int tardisID, String ownerName, Supplier<ResourceLocation> skinSupplier) {
		this.minecraft = minecraft;
		this.tardisID = tardisID;
		this.ownerName = ownerName;
		this.ownerNameText = new TranslationTextComponent("gui.dm_suggestion_7.call.call_tardis", this.ownerName);
		this.skinGetter = skinSupplier;
		
		BlockPos currentTardis = minecraft.player.blockPosition();
		if (DMTardis.getIDForXZ(currentTardis.getX(), currentTardis.getZ()) != this.tardisID) {
			this.callButton = new ToggleImageButton(0, 0, SPEAKER, () -> false, button -> {
				ModMain.NETWORK.sendToServer(new PacketRequestCall(this.tardisID));
				minecraft.setScreen(null);				
			}, (button, stack, mouseX, mouseY) -> {
				screen.renderTooltip(stack, CALL_TEXT, mouseX, mouseY);
			});
			this.children = ImmutableList.of(this.callButton);
		} else {
			this.callButton = null;
			this.children = ImmutableList.of();
		}
	}

	@Override
	public void render(MatrixStack stack, int p_230432_2_, int p_230432_3_, int p_230432_4_, int p_230432_5_, int p_230432_6_, int p_230432_7_, int p_230432_8_, boolean p_230432_9_, float p_230432_10_) {
		int i = p_230432_4_ + 4;
		int j = p_230432_3_ + (p_230432_6_ - 24) / 2;
		int k = i + 24 + 4;
		AbstractGui.fill(stack, p_230432_4_, p_230432_3_, p_230432_4_ + p_230432_5_, p_230432_3_ + p_230432_6_, BG_FILL);
		int l = p_230432_3_ + (p_230432_6_ - 9) / 2;

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
		
		this.minecraft.font.draw(stack, this.ownerNameText, (float)k, (float)l, PLAYERNAME_COLOR);

		if (this.callButton != null) {
			this.callButton.x = p_230432_4_ + (p_230432_5_ - this.callButton.getWidth() - 4);
			this.callButton.y = p_230432_3_ + (p_230432_6_ - this.callButton.getHeight()) / 2;
			this.callButton.render(stack, p_230432_7_, p_230432_8_, p_230432_10_);
		}
	}

	public List<? extends IGuiEventListener> children() {
		return this.children;
	}
	
	public String getOwnerName() {
		return this.ownerName;
	}
	
	public int getID() {
		return this.tardisID;
	}

}