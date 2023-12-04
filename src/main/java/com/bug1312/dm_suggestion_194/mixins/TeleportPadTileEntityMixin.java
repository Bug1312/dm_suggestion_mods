package com.bug1312.dm_suggestion_194.mixins;

import static net.minecraft.state.properties.BlockStateProperties.POWERED;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.swdteam.common.init.DMBlocks;
import com.swdteam.common.tileentity.DMTileEntityBase;
import com.swdteam.common.tileentity.TeleportPadTileEntity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.world.World;

@Mixin(TeleportPadTileEntity.class)
public abstract class TeleportPadTileEntityMixin extends DMTileEntityBase implements ITickableTileEntity {
	public TeleportPadTileEntityMixin(TileEntityType<?> tileEntityTypeIn) { super(tileEntityTypeIn); }
	public TeleportPadTileEntity _this = (TeleportPadTileEntity) ((Object) this);
	
	@Inject(at = @At("HEAD"), method = "tick", cancellable = true)
	public void tick(CallbackInfo ci) {
		if (!this.level.isClientSide()) {
			if (this.getBlockState().getValue(POWERED)) ci.cancel(); 
			
			List<Entity> entities = this.level.getEntitiesOfClass(Entity.class, TeleportPadTileEntity.bounds.move(this.getBlockPos()));
			entities.removeIf(entity -> entity.isPassenger());

			if (!entities.isEmpty()) {
				Entity entity = entities.get(0);
				if (_this.getExitDimension() != null && _this.getExitPosition() != null) {
					World world = entity.getServer().getLevel(_this.dimension());
					BlockState state = world.getBlockState(_this.getExitPosition().below());
					if (state.getBlock() == DMBlocks.TELEPORT_RECEIVER.get() && state.getValue(POWERED)) ci.cancel();
				}
			}
		}

	}

	
}
