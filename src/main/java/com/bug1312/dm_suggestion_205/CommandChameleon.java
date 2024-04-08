// Copyright 2024 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_205;

import java.util.function.Function;

import com.swdteam.common.command.tardis_console.ITardisConsoleCommand;
import com.swdteam.common.command.tardis_console.TardisConsoleCommandBase;
import com.swdteam.common.command.tardis_console.TriggerDefinedString;
import com.swdteam.common.command.tardis_console.environment.CommandEnvironmentSession;
import com.swdteam.common.init.DMItems;
import com.swdteam.common.init.DMTardisRegistry;
import com.swdteam.common.tardis.TardisData;
import com.swdteam.common.tileentity.tardis.DataWriterTileEntity;
import com.swdteam.main.DalekMod;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

public class CommandChameleon extends TardisConsoleCommandBase {
	@Override public String getUsage() { return "chameleon"; }
	@Override public String getCommand() { return "chameleon"; }

	private static Function<String, String> TRANSLATE = (String str) -> {
		return new TranslationTextComponent("data_writer.dm_suggestion_205.chameleon." + str).getString();
	};

	@Override
	public ITardisConsoleCommand.CommandResponse execute(String[] args, ServerPlayerEntity player, ItemStack stack, TardisData data, DataWriterTileEntity tile, CommandEnvironmentSession session) {

		if (!stack.isEmpty() && (stack.getItem() == DMItems.DATA_MODULE.get() || stack.getItem() == DMItems.DATA_MODULE_GOLD.get())) {
			if (stack.getItem() == DMItems.DATA_MODULE_GOLD.get()) {
				session
					.prompt(player, TRANSLATE.apply("options"))
					.addOption(TriggerDefinedString.arg("set", "set <Exterior Name> [Skin Number]"))
					.process((response, args2, player2, stack2, data2, tile2, session2) -> {
						if (args2.length < 1) return this.getResponse(player, TRANSLATE.apply("missing_args"), ResponseType.FAIL);
						
						if (!ResourceLocation.isValidResourceLocation(args2[0])) return this.getResponse(player, TRANSLATE.apply("invalid_exterior"), ResponseType.FAIL);
						ResourceLocation skin = args2[0].contains(":") ? new ResourceLocation(response) : new ResourceLocation(DalekMod.MODID, args2[0]);
						if (!DMTardisRegistry.getRegistry().containsKey(skin)) return this.getResponse(player, TRANSLATE.apply("invalid_exterior"), ResponseType.FAIL);
						
						int skinId = 0;
						try { skinId = Integer.parseInt(args2[1]); } 
						catch (NumberFormatException e) { return this.getResponse(player, TRANSLATE.apply("invalid_skin"), ResponseType.FAIL); }
						skinId = skinId % DMTardisRegistry.getRegistry().get(skin).getData().getSkinCount();

						CompoundNBT tag = stack.getOrCreateTag();
						tag.putString("Chameleon", skin.toString());
						tag.putInt("ChameleonSkin", skinId);
						stack.setTag(tag);

						return this.getResponse(player, TRANSLATE.apply("success_add"), ResponseType.SUCCESS);
					})
					.addOption(TriggerDefinedString.arg("current"))
					.process((response, args2, player2, stack2, data2, tile2, session2) -> {
						CompoundNBT tag = stack.getOrCreateTag();
						tag.putString("Chameleon", data.getTardisExterior().getRegName());
						tag.putInt("ChameleonSkin", data.getSkinID());
						stack.setTag(tag);
						
						return this.getResponse(player, TRANSLATE.apply("success_add"), ResponseType.SUCCESS);
					})
					.addOption(TriggerDefinedString.arg("remove"))
					.process((response, args2, player2, stack2, data2, tile2, session2) -> {
						CompoundNBT tag = stack.getOrCreateTag();
						
						if (tag.contains("Chameleon") || tag.contains("ChameleonSkin")) {
							tag.remove("Chameleon");
							tag.remove("ChameleonSkin");
							stack.setTag(tag);
						
							return this.getResponse(player, TRANSLATE.apply("success_remove"), ResponseType.SUCCESS);
						}

						return this.getResponse(player, TRANSLATE.apply("no_exterior"), ResponseType.FAIL);
					})
					.build();

				return null;
			}

			return this.getResponse(player, TRANSLATE.apply("invalid_cartridge"), ResponseType.FAIL);
		}

		return this.getResponse(player, TRANSLATE.apply("no_cartridge"), ResponseType.FAIL);
	}
}