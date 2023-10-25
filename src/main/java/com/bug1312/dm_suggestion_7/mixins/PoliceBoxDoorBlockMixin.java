// Copyright 2023 Bug1312 (bug@bug1312.com)

package com.bug1312.dm_suggestion_7.mixins;

import java.util.Optional;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.bug1312.dm_suggestion_7.VoicePlugin;
import com.bug1312.dm_suggestion_7.call.CallScreen;
import com.swdteam.common.block.IBlockTooltip;
import com.swdteam.common.block.tardis.PoliceBoxDoorBlock;
import com.swdteam.common.init.DMTardis;

import de.maxhenkel.voicechat.api.Group;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

@Mixin(PoliceBoxDoorBlock.class)
public class PoliceBoxDoorBlockMixin implements IBlockTooltip {
    private static final VoxelShape N0 = VoxelShapes.box(15/16D,1/16D,2/16D,18/16D,7/16D,6/16D);
    private static final VoxelShape N1 = VoxelShapes.box(8/16D,1/16D,2/16D,11/16D,7/16D,6/16D);
    private static final VoxelShape N2 = VoxelShapes.box(1/16D,1/16D,2/16D,4/16D,7/16D,6/16D);

    private static final VoxelShape E0 = VoxelShapes.box(10/16D,1/16D,15/16D,14/16D,7/16D,18/16D);
    private static final VoxelShape E1 = VoxelShapes.box(10/16D,1/16D,8/16D,14/16D,7/16D,11/16D);
    private static final VoxelShape E2 = VoxelShapes.box(10/16D,1/16D,1/16D,14/16D,7/16D,4/16D);

    private static final VoxelShape S0 = VoxelShapes.box(-2/16D,1/16D,10/16D,1/16D,7/16D,14/16D);
    private static final VoxelShape S1 = VoxelShapes.box(5/16D,1/16D,10/16D,8/16D,7/16D,14/16D);
    private static final VoxelShape S2 = VoxelShapes.box(12/16D,1/16D,10/16D,15/16D,7/16D,14/16D);

    private static final VoxelShape W0 = VoxelShapes.box(2/16D,1/16D,-2/16D,6/16D,7/16D,1/16D);
    private static final VoxelShape W1 = VoxelShapes.box(2/16D,1/16D,5/16D,6/16D,7/16D,8/16D);
    private static final VoxelShape W2 = VoxelShapes.box(2/16D,1/16D,12/16D,6/16D,7/16D,15/16D);

    private static VoxelShape getPhoneShape(BlockState state) {
		switch(state.getValue(getOffset())) {
			case 0: default: 
				switch (state.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
					default:
					case NORTH:	return N0;
					case EAST: 	return E0;
					case SOUTH:	return S0;
					case WEST: 	return W0;
				}
			case 1: 
				switch (state.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
					default:
					case NORTH:	return N1;
					case EAST: 	return E1;
					case SOUTH:	return S1;
					case WEST: 	return W1;
				}
			case 2: 
				switch (state.getValue(BlockStateProperties.HORIZONTAL_FACING)) {
					default:
					case NORTH:	return N2;
					case EAST: 	return E2;
					case SOUTH:	return S2;
					case WEST: 	return W2;
				}
		}
    }
    

	private static boolean isBoxAvailable(BlockState state, BlockPos pos, IBlockReader worldIn) {
		return (
				state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.UPPER &&
				!(
					worldIn.getBlockEntity(pos.below()) != null &&
					worldIn.getBlockEntity(pos.below()).serializeNBT().contains("RightDoorOpen") &&
					worldIn.getBlockEntity(pos.below()).serializeNBT().getBoolean("RightDoorOpen")
				)
		);
	}
	
	private static boolean isMouseOnBox(BlockState state, BlockPos pos, Vector3d mouse, IBlockReader world) {
		return isBoxAvailable(state, pos, world) && getPhoneShape(state).bounds().inflate(0.5/16D).contains(mouse.subtract(pos.getX(), pos.getY(), pos.getZ()));
	}
    
	// Mixin stuff
	@Accessor("OFFSET") public static IntegerProperty getOffset() { throw new AssertionError(); }

	@Inject(at = @At("RETURN"), method = "getShape", cancellable = true)
	public void getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context, CallbackInfoReturnable<VoxelShape> ci) {
		if (isBoxAvailable(state, pos, world)) ci.setReturnValue(VoxelShapes.or(ci.getReturnValue(), getPhoneShape(state)));
	}
	
	@Inject(at = @At("HEAD"), method = "use", cancellable = true)
	public void use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult result, CallbackInfoReturnable<ActionResultType> ci) {
		if (!player.isShiftKeyDown() && isMouseOnBox(state, pos, result.getLocation(), world)) {
			int id = DMTardis.getIDForXZ(pos.getX(), pos.getZ());
			if (world.isClientSide && !VoicePlugin.callExists(id)) {
				Minecraft.getInstance().setScreen(new CallScreen());
			} else if (!world.isClientSide && VoicePlugin.callExists(id)) {
				VoicechatConnection connection = VoicePlugin.voicechatServerApi.getConnectionOf(player.getUUID());
				Optional<Group> callGroup = VoicePlugin.getCallGroup(id);
				if (connection != null && callGroup.isPresent()) connection.setGroup(callGroup.get());
			}
			
			ci.setReturnValue(ActionResultType.CONSUME);
		}
	}
	
	@Override
	public ITextComponent getName(BlockState state, BlockPos pos, Vector3d vector, PlayerEntity player) {
		if (player.isShiftKeyDown() || !isMouseOnBox(state, pos, vector, player.level)) return null;
		int id = DMTardis.getIDForXZ(pos.getX(), pos.getZ());
		return VoicePlugin.callExists(id) ? new TranslationTextComponent("tooltip.dm_suggestion_7.answer_phone") : new TranslationTextComponent("tooltip.dm_suggestion_7.call_tardis");
	}
	
}
