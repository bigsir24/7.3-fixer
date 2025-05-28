package useless.fixer.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicTrapDoor;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.monster.MobCreeper;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pathfinder.Node;
import net.minecraft.core.world.pathfinder.PathFinder;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
/*
Fixes creepers not being able to pathfind under trapdoors
UNLIKELY TO BE FIXED, DO NOT REMOVE
 */
@Mixin(value = PathFinder.class, remap = false)
public abstract class PathFinderMixin {

	@Final
	@Shadow
	private WorldSource worldSource;

	@Inject(method = "isFree", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/block/Block;getMaterial()Lnet/minecraft/core/block/material/Material;", shift = At.Shift.BEFORE), cancellable = true)
	void isFree(Entity entity, int x, int y, int z, Node pathpoint, CallbackInfoReturnable<Integer> cir, @Local(name = "x1") int x1, @Local(name = "y1") int y1, @Local(name = "z1") int z1){
		int blockId = worldSource.getBlockId(x1, y1, z1);
		if(entity instanceof MobCreeper){
			int blockMetadata = worldSource.getBlockMetadata(x1, y1, z1);
			if(Block.hasLogicClass(Blocks.blocksList[blockId], BlockLogicTrapDoor.class)){
				boolean isTopClosedTrapdoor = !BlockLogicTrapDoor.isTrapdoorOpen(blockMetadata) && BlockLogicTrapDoor.isUpperHalf(blockMetadata);
				if(isTopClosedTrapdoor){
					cir.setReturnValue(1);
                }
			}
		}
	}
}
