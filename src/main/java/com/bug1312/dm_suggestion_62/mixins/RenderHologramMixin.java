// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_62.mixins;

import static net.minecraft.state.properties.BlockStateProperties.LIT;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.swdteam.client.render.tileentity.RenderHologram;
import com.swdteam.common.tileentity.HologramTileEntity;

import net.minecraft.client.renderer.IRenderTypeBuffer;

@Mixin(RenderHologram.class)
public class RenderHologramMixin {

	@Inject(at = @At("HEAD"), method = "render", cancellable = true, remap = false)
	public void render(final HologramTileEntity tile, final float partialTicks, final MatrixStack matrixStack, final IRenderTypeBuffer iRenderTypeBuffer, final int combinedLightIn, final int combinedOverlayIn, final CallbackInfo ci) {
		if (!tile.getBlockState().getValue(LIT)) ci.cancel();
	}
}
