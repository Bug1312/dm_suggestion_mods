// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_174;

import java.util.List;

import com.swdteam.common.init.DMDimensions;
import com.swdteam.common.init.DMNBTKeys;
import com.swdteam.common.init.DMSoundEvents;
import com.swdteam.common.init.DMTardis;
import com.swdteam.common.tardis.Location;
import com.swdteam.common.tardis.TardisData;
import com.swdteam.common.teleport.TeleportRequest;
import com.swdteam.common.tileentity.TardisTileEntity;
import com.swdteam.util.TeleportUtil;
import com.swdteam.util.math.Position;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class InteriorTeleportItem extends Item {

	public long registeredTick = -1;
	
	public InteriorTeleportItem(Properties properties) {
		super(properties);
	}
	
	@Override
	public ActionResultType useOn(ItemUseContext context) {
		ItemStack stack = context.getItemInHand();
		PlayerEntity player = context.getPlayer();
		World world = context.getLevel();
		this.registeredTick = world.getGameTime();
		if (!stack.getOrCreateTag().contains(DMNBTKeys.LINKED_ID) && player.isShiftKeyDown()) {
			BlockPos pos = context.getClickedPos();
			TileEntity te = world.getBlockEntity(pos);
			if (te != null && te instanceof TardisTileEntity) {
				TardisTileEntity tardis = (TardisTileEntity) te;
				TardisData data = DMTardis.getTardis(tardis.globalID);
				if (data.hasPermission(player, TardisData.PermissionType.DEFAULT)) {
					world.playSound(null, pos, DMSoundEvents.ENTITY_STATTENHEIM_REMOTE_SYNC.get(), SoundCategory.BLOCKS, 1, 1);
					player.displayClientMessage(Register.TARDIS_LINKED.withStyle(TextFormatting.GREEN), true);

					stack.getOrCreateTag().putInt(DMNBTKeys.LINKED_ID, tardis.globalID);
				} else data.noPermission(player);
			}
		} else this.registeredTick = -1;
		return super.useOn(context);
	}
	
	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		ItemStack stack = player.getItemInHand(hand);
		if (!world.isClientSide() && world.getGameTime() != registeredTick && stack.getOrCreateTag().contains(DMNBTKeys.LINKED_ID)) {
			TardisData data = DMTardis.getTardis(stack.getTag().getInt(DMNBTKeys.LINKED_ID));
			Position intPosition = data.getInteriorSpawnPosition();
			World intWorld = world.getServer().getLevel(DMDimensions.TARDIS);
			Location intLocation = new Location(new Vector3d(intPosition.x(), intPosition.y(), intPosition.z()), intWorld.dimension());
			intLocation.setFacing(data.getInteriorSpawnRotation());
			
			TeleportUtil.TELEPORT_REQUESTS.put(player, new TeleportRequest(intLocation, (entity, location) -> {
				world.playSound(null, location.getBlockPosition(), DMSoundEvents.BLOCK_TELEPORT_PAD.get(), SoundCategory.PLAYERS, 0.5F, 1);
				intWorld.playSound(null, location.getBlockPosition(), DMSoundEvents.BLOCK_TELEPORT_PAD.get(), SoundCategory.PLAYERS, 0.5F, 1);
			}));
		}
		return super.use(world, player, hand);
	}
	
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		
		Minecraft mc = Minecraft.getInstance();
		if (mc.options.advancedItemTooltips && stack.getOrCreateTag().contains(DMNBTKeys.LINKED_ID)) {
			tooltip.add(Register.ID_TOOLTIP.apply(stack.getTag().getInt(DMNBTKeys.LINKED_ID)).withStyle(TextFormatting.DARK_GRAY));
		}
	}

}
