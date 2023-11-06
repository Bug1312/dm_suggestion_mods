// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_7.call;

import java.util.Locale;

import com.bug1312.dm_suggestion_7.ModMain;
import com.bug1312.dm_suggestion_7.network.CallInfo;
import com.bug1312.dm_suggestion_7.network.PacketRequestCallable;
import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CallScreen extends Screen {

	protected static final ResourceLocation SOCIAL_INTERACTIONS_LOCATION = new ResourceLocation("textures/gui/social_interactions.png");
	private static final ITextComponent SEARCH_PLACEHOLDER = (new TranslationTextComponent("gui.socialInteractions.search_hint")).withStyle(TextFormatting.ITALIC).withStyle(TextFormatting.GRAY);
	private static final ITextComponent NONE_FOUND = (new TranslationTextComponent("gui.dm_suggestion_7.call.none_found")).withStyle(TextFormatting.GRAY);

	private TextFieldWidget searchBox;
	private String lastSearch = "";

	private FilterCallList tardisCallList;
	private boolean initialized;

	public CallScreen() {
		super(new TranslationTextComponent("gui.dm_suggestion_7.call.title"));
	}

	private int windowHeight() {
		return Math.max(52, this.height - 128 - 16);
	}

	private int backgroundUnits() {
		return this.windowHeight() / 16;
	}

	private int listEnd() {
		return 80 + this.backgroundUnits() * 16 - 8;
	}

	private int marginX() {
		return (this.width - 238) / 2;
	}

	public void tick() {
		super.tick();
		this.searchBox.tick();
		
		if (CallInfo.CALL_LIST.size() != tardisCallList.children().size()) tardisCallList.updateTardisList(CallInfo.CALL_LIST, height);
	}

	@Override
	public void renderBackground(MatrixStack stack) {
		int i = this.marginX() + 3;
		super.renderBackground(stack);
		this.minecraft.getTextureManager().bind(SOCIAL_INTERACTIONS_LOCATION);
		this.blit(stack, i, 64, 1, 1, 236, 8);
		int j = this.backgroundUnits();

		for (int k = 0; k < j; ++k) {
			this.blit(stack, i, 72 + 16 * k, 1, 10, 236, 16);
		}

		this.blit(stack, i, 72 + 16 * j, 1, 27, 236, 8);
		this.blit(stack, i + 10, 76, 243, 1, 12, 12);
	}

	@Override
	protected void init() {
		ModMain.NETWORK.sendToServer(new PacketRequestCallable());

		this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
		if (this.initialized) {
			this.tardisCallList.updateSize(this.width, this.height, 88, this.listEnd());
		} else {
			this.tardisCallList = new FilterCallList(this, Minecraft.getInstance(), this.width, this.height, 88, this.listEnd(), 36);
		}

		String s = this.searchBox != null ? this.searchBox.getValue() : "";
		this.searchBox = new TextFieldWidget(this.font, this.marginX() + 28, 78, 196, 16, SEARCH_PLACEHOLDER) {
			protected IFormattableTextComponent createNarrationMessage() {
				return !CallScreen.this.searchBox.getValue().isEmpty() && CallScreen.this.tardisCallList.isEmpty() ? super.createNarrationMessage().append(", ").append(CallScreen.NONE_FOUND) : super.createNarrationMessage();
			}
		};
		this.searchBox.setMaxLength(16);
		this.searchBox.setBordered(false);
		this.searchBox.setVisible(true);
		this.searchBox.setTextColor(16777215);
		this.searchBox.setValue(s);
		this.searchBox.setResponder(this::checkSearchStringUpdate);
		this.children.add(this.searchBox);
		this.children.add(this.tardisCallList);
		this.initialized = true;
	}

	@Override
	public void render(MatrixStack stack, int mouseX, int mouseY, float frameTick) {
		this.renderBackground(stack);
		
		if (!this.tardisCallList.isEmpty()) {
			this.tardisCallList.render(stack, mouseX, mouseY, frameTick);
		} else if (!this.searchBox.getValue().isEmpty()) {
			drawCenteredString(stack, this.minecraft.font, NONE_FOUND, this.width / 2, (78 + this.listEnd()) / 2, -1);
		}

		if (!this.searchBox.isFocused() && this.searchBox.getValue().isEmpty()) {
			drawString(stack, this.minecraft.font, SEARCH_PLACEHOLDER, this.searchBox.x, this.searchBox.y, -1);
		} else {
			this.searchBox.render(stack, mouseX, mouseY, frameTick);
		}

		super.render(stack, mouseX, mouseY, frameTick);
	}

	private void checkSearchStringUpdate(String search) {
		search = search.toLowerCase(Locale.ROOT);
		if (!search.equals(this.lastSearch)) {
			this.tardisCallList.setFilter(search);
			this.lastSearch = search;
			tardisCallList.updateTardisList(CallInfo.CALL_LIST, tardisCallList.getScrollAmount());

		}
	}
	
	@Override
	public void onClose() {
		CallInfo.CALL_LIST.clear();
		super.onClose();
	}

}
