// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_122;

import java.util.List;

import com.swdteam.common.init.DMNBTKeys;
import com.swdteam.common.item.StattenheimRemoteItem;
import com.swdteam.common.item.TardisKeyItem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class KeyChainItem extends Item {

	public static final int KEY_MAX = 5;
	private long useOnTime;
	
	public KeyChainItem(Properties properties) {
		super(properties);
	}
	
	@Override
	public ActionResultType useOn(ItemUseContext context) {
		useOnTime = context.getLevel().getGameTime();
		ItemStack keyChain = context.getItemInHand();
		ItemStack stack = getSelected(context.getItemInHand());
		PlayerEntity player = context.getPlayer();
		
		if (stack.isEmpty()) addItem(keyChain, stack);
		else if (!player.getCooldowns().isOnCooldown(stack.getItem())) {
			context.getPlayer().setItemInHand(context.getHand(), stack);
			
			BlockRayTraceResult blockraytraceresult = getPlayerPOVHitResult(context.getLevel(), context.getPlayer(), RayTraceContext.FluidMode.ANY);
			ItemUseContext newContext = new ItemUseContext(context.getPlayer(), context.getHand(), blockraytraceresult);
			ActionResultType result = stack.getItem().useOn(newContext);
						
			updateSelected(keyChain, stack);
			context.getPlayer().setItemInHand(context.getHand(), keyChain);
			
			return result;
		}
		
		return super.useOn(context);
	}
	
	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
		if (useOnTime != world.getGameTime()) {
			ItemStack keyringStack = player.getItemInHand(hand);
			ItemStack otherStack = player.getItemInHand(hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND);
			
			if (player.isShiftKeyDown()) swapItem(keyringStack);
			else addItem(keyringStack, otherStack);
		}
		return super.use(world, player, hand);
	}
	
	@Override @OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
		super.appendHoverText(stack, worldIn, tooltip, flagIn);
		Minecraft mc = Minecraft.getInstance();
		CompoundNBT tag = stack.getOrCreateTag();
		
		NonNullList<ItemStack> items = NonNullList.withSize(KEY_MAX, ItemStack.EMPTY);
		ItemStackHelper.loadAllItems(tag, items);
		int amount = (int) items.stream().filter(item -> !item.isEmpty()).count();

		tooltip.add(new TranslationTextComponent(String.format("tooltip.%s.capacity", ModMain.MOD_ID), amount, KEY_MAX).withStyle(TextFormatting.GREEN));		
		
		if (mc.options.advancedItemTooltips) {
			ItemStack selected = getSelected(stack);
			if (selected.getOrCreateTag().contains(DMNBTKeys.LINKED_ID)) tooltip.add(new TranslationTextComponent(String.format("tooltip.%s.linked", ModMain.MOD_ID), selected.getTag().getInt(DMNBTKeys.LINKED_ID)).withStyle(TextFormatting.DARK_GRAY));
		}
	}
		
	public boolean isAllowed(ItemStack newStack) {
		Item item = newStack.getItem();
		return item instanceof TardisKeyItem || item instanceof StattenheimRemoteItem;
	}
	
	public void updateSelected(ItemStack keyring, ItemStack newItem) {
		CompoundNBT tag = keyring.getOrCreateTag();
		if (!tag.contains("Items") | !tag.contains("Selected")) return;
		
		NonNullList<ItemStack> items = NonNullList.withSize(KEY_MAX, ItemStack.EMPTY);
		ItemStackHelper.loadAllItems(tag, items);
		
		items.set(tag.getInt("Selected"), newItem);
		
		ItemStackHelper.saveAllItems(tag, items);
	}
	
	public ItemStack getSelected(ItemStack keyring) {
		CompoundNBT tag = keyring.getOrCreateTag();
		if (!tag.contains("Items")) return ItemStack.EMPTY;
		
		if (!tag.contains("Selected")) swapItem(keyring);
		NonNullList<ItemStack> items = NonNullList.withSize(KEY_MAX, ItemStack.EMPTY);
		ItemStackHelper.loadAllItems(tag, items);
		
		return items.get(tag.getInt("Selected"));
	}
	
	public void swapItem(ItemStack keyring) {
		CompoundNBT tag = keyring.getOrCreateTag();
		if (tag.contains("Selected")) {
			NonNullList<ItemStack> items = NonNullList.withSize(KEY_MAX, ItemStack.EMPTY);
			ItemStackHelper.loadAllItems(tag, items);
			
			int max = (int) items.stream().filter(stack -> !stack.isEmpty()).count();
			tag.putInt("Selected", ((tag.getInt("Selected") + 1) % max));
		} else tag.putInt("Selected", 0);
	}
		
	public void addItem(ItemStack keyring, ItemStack newStack) {
		if (!isAllowed(newStack)) return;
		
		CompoundNBT tag = keyring.getOrCreateTag();
		NonNullList<ItemStack> items = NonNullList.withSize(KEY_MAX, ItemStack.EMPTY);
		ItemStackHelper.loadAllItems(tag, items);

		int emptyIndex = items.indexOf(ItemStack.EMPTY);
		if (emptyIndex != -1) {
			items.set(emptyIndex, newStack);
			ItemStackHelper.saveAllItems(tag, items);
			newStack.shrink(1);

			tag.putInt("Selected", emptyIndex);
		}
	}

}