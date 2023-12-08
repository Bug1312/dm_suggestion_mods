// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_368.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.bug1312.dm_suggestion_368.ModMain;
import com.swdteam.client.init.ItemRenderingInit;
import com.swdteam.client.init.ItemRenderingRegistry;

import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
@Mixin(ItemRenderingInit.class)
public class MixedItemRendering {

	@Inject(at = @At("TAIL"), method = "addRegistries", remap = false)
	private static void add2D3DItems(CallbackInfo ci) {
		ItemRenderingRegistry.addItemRenderer(ModMain.RAINBOW_FEZ).addTransformType("worn", TransformType.HEAD);
	}

}
