// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_68.mixins;

import java.util.function.Supplier;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.bug1312.dm_suggestion_68.ClassicFlowerBlock;
import com.swdteam.common.init.DMBlocks;

import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.potion.Effects;
import net.minecraftforge.fml.RegistryObject;

@Mixin(DMBlocks.class) 
public class DMBlocksMixin {

	@Inject(at = @At("HEAD"), method = "Lcom/swdteam/common/init/DMBlocks;registerBlock(Ljava/util/function/Supplier;Ljava/lang/String;Lnet/minecraft/item/ItemGroup;)Lnet/minecraftforge/fml/RegistryObject;", cancellable = true, remap = false)
	private static <B extends Block> void registerFlowers(Supplier<B> block, String name, ItemGroup itemgroup, CallbackInfoReturnable<RegistryObject<Block>> ci) {
		switch (name) {
			case "classic_red_flower":
				ci.setReturnValue(DMBlocks.registerBlock(() -> new ClassicFlowerBlock(Effects.NIGHT_VISION, 5, Properties.of(Material.GRASS).noCollission().strength(0.0F, 0.0F).sound(SoundType.GRASS)), name, new Item.Properties().tab(itemgroup), true));
				break;
			case "classic_yellow_flower":
				ci.setReturnValue(DMBlocks.registerBlock(() -> new ClassicFlowerBlock(Effects.SATURATION, 7, Properties.of(Material.GRASS).noCollission().strength(0.0F, 0.0F).sound(SoundType.GRASS)), name, new Item.Properties().tab(itemgroup), true));
				break;
		}
	}
}
