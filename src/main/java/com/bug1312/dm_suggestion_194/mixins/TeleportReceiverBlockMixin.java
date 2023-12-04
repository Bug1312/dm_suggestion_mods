package com.bug1312.dm_suggestion_194.mixins;

import static net.minecraft.state.properties.BlockStateProperties.POWERED;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.swdteam.common.block.TeleportReceiverBlock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(TeleportReceiverBlock.class)
public class TeleportReceiverBlockMixin extends Block {
	public TeleportReceiverBlockMixin(Properties properties) { super(properties); }
	public TeleportReceiverBlock _this = (TeleportReceiverBlock) ((Object) this);

	@Inject(at = @At("RETURN"), method = "<init>*")
	public void constructor(final CallbackInfo ci) {
		this.registerDefaultState(_this.defaultBlockState().setValue(POWERED, false));
	}
	
	@Override
	protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder);
		builder.add(POWERED);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return super.getStateForPlacement(context).setValue(POWERED, context.getLevel().hasNeighborSignal(context.getClickedPos()));
	}

	@Override
	public void neighborChanged(BlockState state, World world, BlockPos pos, Block neighborBlock, BlockPos neighborPos, boolean notify) {
		boolean powered = world.hasNeighborSignal(pos);
		if (neighborBlock != this && powered != state.getValue(POWERED)) world.setBlock(pos, state.setValue(POWERED, powered), 2);
	}
	
}