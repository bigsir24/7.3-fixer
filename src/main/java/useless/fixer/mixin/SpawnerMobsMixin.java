package useless.fixer.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.world.SpawnerMobs;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.ChunkCoordinate;
import net.minecraft.core.world.chunk.ChunkPosition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
/*
Fixes sign transparency issues introduced in 7.3_03
UNLIKELY TO BE FIXED, DO NOT REMOVE
 */
@Mixin(value = SpawnerMobs.class, remap = false)
public abstract class SpawnerMobsMixin {

	@Unique
	private static int randXZ(World world, int chunkXZ) {
		return chunkXZ * 16 + world.rand.nextInt(16);
	}

	@Unique
	private static int randY(World world) {
		return world.rand.nextInt(world.getHeightBlocks());
	}


	@Inject(method = "getRandomSpawningPointInChunk", at = @At("HEAD"), cancellable = true)
	private static void returnNull(World world, int chunkX, int chunkZ, CallbackInfoReturnable<ChunkPosition> cir) {
		cir.setReturnValue(null);
	}

	@Redirect(method = "performSpawning", at = @At(value = "FIELD", target = "Lnet/minecraft/core/world/chunk/ChunkPosition;x:I"))
	private static int modX(ChunkPosition instance, @Local(name = "chunk") ChunkCoordinate chunk, @Local(name = "world") World world){
		return randXZ(world, chunk.x);
	}

	@Redirect(method = "performSpawning", at = @At(value = "FIELD", target = "Lnet/minecraft/core/world/chunk/ChunkPosition;y:I"))
	private static int modY(ChunkPosition instance, @Local(name = "chunk") ChunkCoordinate chunk, @Local(name = "world") World world){
		return randY(world);
	}

	@Redirect(method = "performSpawning", at = @At(value = "FIELD", target = "Lnet/minecraft/core/world/chunk/ChunkPosition;z:I"))
	private static int modZ(ChunkPosition instance, @Local(name = "chunk") ChunkCoordinate chunk, @Local(name = "world") World world){
		return randXZ(world, chunk.z);
	}
}
