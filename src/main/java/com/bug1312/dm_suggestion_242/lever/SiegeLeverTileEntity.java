package com.bug1312.dm_suggestion_242.lever;

import com.swdteam.common.init.DMBlockEntities;
import com.swdteam.common.tileentity.tardis.TardisPanelTileEntity;

import net.minecraft.util.ResourceLocation;

public class SiegeLeverTileEntity extends TardisPanelTileEntity {
	private static final long serialVersionUID = -117041474714095301L;

	public SiegeLeverTileEntity() {
		super(DMBlockEntities.TILE_FLIGHT_PANEL.get());
	}

	@Override
	public ResourceLocation getGUIIcon() {
		return new ResourceLocation("dm_suggestion_242:textures/item/siege_lever.png");
	}
}
