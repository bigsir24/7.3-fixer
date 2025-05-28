package useless.fixer.mixin;

import net.minecraft.client.world.chunk.provider.ChunkProviderClient;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.chunk.ChunkCoordinate;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import useless.fixer.interfaces.ICoord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/*
Properly clears chunks when unloading, fixing a memory leak when unloadAllChunks() is called
Replaces ChunkCoordinate instance creations on map query with a static instance
MAY BE FIXED IN 7.4
 */
@Mixin(value = ChunkProviderClient.class, remap = false)
public abstract class ChunkProviderClientMixin {
	@Mutable
	@Shadow
	@Final
	private Map<ChunkCoordinate, Chunk> chunkMapping;

	@Mutable
	@Shadow
	@Final
	private List<Chunk> chunks;

	@Unique
	private static final ChunkCoordinate COORD = new ChunkCoordinate(0,0);

	@Inject(method = "unloadAllChunks", at = @At("HEAD"))
	public void newInstances(CallbackInfo ci){
		this.chunkMapping = new HashMap<>();
		this.chunks = new ArrayList<>();
	}

	@Redirect(method = {"isChunkLoaded", "dropChunk", "prepareChunk", "provideChunk"}, at = @At(value = "NEW", target = "(II)Lnet/minecraft/core/world/chunk/ChunkCoordinate;"))
	public ChunkCoordinate staticInstance(int x, int z){
		return getChunkCoordinate(x,z);
	}

	@Unique
	private ChunkCoordinate getChunkCoordinate(int x, int z) {
		((ICoord)COORD).btafixer$set(x, z);
		return COORD;
	}
}
