// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_242.mixins.minecraft;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.bug1312.dm_suggestion_242.tardis.SiegeTardisItem;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;

@Mixin(Container.class)
public abstract class ContainerMixin {
	Container _this = (Container) ((Object) this);
	
    @Inject(method = "doClick", at = @At(value = "HEAD"), cancellable = true)
	private void doClick(int slotIndex, int button, ClickType actionType, PlayerEntity player, CallbackInfoReturnable<ItemStack> ci) {
    	if (slotIndex >= 0 && slotIndex < _this.slots.size()) {
    		Runnable cancel = () -> { ci.setReturnValue(ItemStack.EMPTY); ci.cancel(); };
    		ItemStack hoverStack = _this.slots.get(slotIndex).getItem();
        	ItemStack heldStack = player.inventory.getCarried();
        	
    		if (actionType == ClickType.QUICK_MOVE && !(_this instanceof PlayerContainer) && hoverStack.getItem() instanceof SiegeTardisItem) cancel.run();

    		if ((actionType == ClickType.PICKUP || actionType == ClickType.QUICK_CRAFT || actionType == ClickType.PICKUP_ALL) && heldStack != null && heldStack.getItem() instanceof SiegeTardisItem) {
            	_this.setItem(slotIndex, heldStack);
            	if (!player.inventory.contains(heldStack)) {
                	_this.setItem(slotIndex, hoverStack);
                	cancel.run();
            	}
            	_this.setItem(slotIndex, hoverStack);
            }

    		if (actionType == ClickType.SWAP) {
            	ItemStack hotbarSlotItem = player.inventory.getItem(button);
            	
            	if (hotbarSlotItem.getItem() instanceof SiegeTardisItem || hoverStack.getItem() instanceof SiegeTardisItem) {
                    player.inventory.setItem(button, hoverStack);
                    _this.setItem(slotIndex, hotbarSlotItem);
                	if (
                			(hotbarSlotItem.getItem() instanceof SiegeTardisItem && !player.inventory.contains(hotbarSlotItem)) ||
                			(hoverStack.getItem() instanceof SiegeTardisItem && !player.inventory.contains(hoverStack))
            			) {
                        player.inventory.setItem(button, hotbarSlotItem);
                        _this.setItem(slotIndex, hoverStack);
                    	cancel.run();
                	}
                    player.inventory.setItem(button, hotbarSlotItem);
                    _this.setItem(slotIndex, hoverStack);
            	}
       		}
    	}
    }

}
