// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_55.mixins;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.bug1312.dm_suggestion_55.ModMain;
import com.swdteam.client.overlay.OverlayGunChargeup;
import com.swdteam.common.item.GunItem;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
@Mixin(OverlayGunChargeup.class)
public class OverlayGunChargeupMixin {

	@Redirect(method = "render", at = @At(value = "FIELD", target = "Lcom/swdteam/common/item/GunItem;requiredChargeTime:F", ordinal = 0, opcode = Opcodes.GETFIELD), remap = false)
	private float injected(GunItem item) {
		Minecraft mc = Minecraft.getInstance();
		ItemStack stack = mc.player.getUseItem();
		
		return ModMain.quickChargeMath(item, stack);
	}
}
