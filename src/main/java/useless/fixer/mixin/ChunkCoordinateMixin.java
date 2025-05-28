package useless.fixer.mixin;

import net.minecraft.core.world.chunk.ChunkCoordinate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import useless.fixer.interfaces.ICoord;

/*
Helper Mixin to allow changing of final fields
DO NOT REMOVE
 */
@Mixin(value = ChunkCoordinate.class, remap = false)
public abstract class ChunkCoordinateMixin implements ICoord {
	@Mutable
	@Shadow
	@Final
	public int x;

	@Mutable
	@Shadow
	@Final
	public int z;

	@Override
	public void btafixer$set(int x, int z) {
		this.x = x;
		this.z = z;
	}
}
