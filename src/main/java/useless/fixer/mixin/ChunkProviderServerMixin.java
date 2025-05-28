package useless.fixer.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.chunk.ChunkCoordinate;
import net.minecraft.server.world.WorldServer;
import net.minecraft.server.world.chunk.provider.ChunkProviderServer;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import useless.fixer.interfaces.ICoord;

import java.util.*;
/*
Replaces the Integer map with a (supposedly) slightly more performant ChunkCoordinate map
Replaces ChunkCoordinate instance creations on map query with a static instance
MAY BE FIXED IN 7.4
 */
@Mixin(value = ChunkProviderServer.class, remap = false)
public abstract class ChunkProviderServerMixin {
	@Mutable
	@Shadow
	@Final
	private List<Chunk> chunkList;
	@Shadow
	@Final
	private WorldServer world;
	@Shadow
	protected abstract void saveChunkToFile(Chunk chunk);
	@Unique
	private Set<ChunkCoordinate> droppedChunksSetCC = new HashSet<>();

	@Unique
	private static final ChunkCoordinate COORD = new ChunkCoordinate(0,0);

	@Unique
	private Map<ChunkCoordinate, Chunk> chunkMapCC = new HashMap<>();

	@Redirect(method = "dropChunk", at = @At(value = "INVOKE", target = "Ljava/util/Set;add(Ljava/lang/Object;)Z"))
	public boolean setAdd(Set<Object> instance, Object object, @Local(name = "chunkX") int chunkX, @Local(name = "chunkZ") int chunkZ){
		return droppedChunksSetCC.add(new ChunkCoordinate(chunkX, chunkZ));
	}

	@Redirect(method = {"prepareChunk", "regenerateChunk"}, at = @At(value = "INVOKE", target = "Ljava/util/Set;remove(Ljava/lang/Object;)Z"))
	public boolean setRemove(Set<Object> instance, Object object, @Local(name = "chunkX") int chunkX, @Local(name = "chunkZ") int chunkZ) {
		return droppedChunksSetCC.remove(getChunkCoordinate(chunkX, chunkZ));
	}

	@Redirect(method = "isChunkLoaded", at = @At(value = "INVOKE", target = "Ljava/util/Map;containsKey(Ljava/lang/Object;)Z"))
	public boolean mapContains(Map<Integer, Chunk> instance, Object object, @Local(name = "chunkX") int chunkX, @Local(name = "chunkZ") int chunkZ){
		return chunkMapCC.containsKey(getChunkCoordinate(chunkX,chunkZ));
	}

	@Redirect(method = "prepareChunk", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
	public Object mapPut(Map<Integer, Chunk> instance, Object key, Object val, @Local(name = "chunkX") int chunkX, @Local(name = "chunkZ") int chunkZ){
		return chunkMapCC.put(new ChunkCoordinate(chunkX, chunkZ), (Chunk) val);
	}

	@Redirect(method = {"prepareChunk", "provideChunk"}, at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
	public Object mapGet(Map<Integer, Chunk> instance, Object object, @Local(name = "chunkX") int chunkX, @Local(name = "chunkZ") int chunkZ){
		return chunkMapCC.get(getChunkCoordinate(chunkX, chunkZ));
	}

	@Redirect(method = "regenerateChunk", at = @At(value = "INVOKE", target = "Ljava/util/Map;remove(Ljava/lang/Object;)Ljava/lang/Object;"))
	public Object redundantDelete(Map<Integer, Chunk> instance, Object object, @Local(name = "chunkX") int chunkX, @Local(name = "chunkZ") int chunkZ){
		return null;
	}

	@Redirect(method = "regenerateChunk", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
	public Object mapDelete(Map<Integer, Chunk> instance, Object object, @Local(name = "chunkX") int chunkX, @Local(name = "chunkZ") int chunkZ){
		return chunkMapCC.remove(getChunkCoordinate(chunkX, chunkZ));
	}

	@Redirect(method = "regenerateChunk", at = @At(value = "INVOKE", target = "Ljava/util/Map;put(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"))
	public Object mapPut(Map<Integer, Chunk> instance, Object key, Object val, @Local(name = "chunkX") int chunkX, @Local(name = "chunkZ") int chunkZ, @Local(name = "chunk") Chunk chunk){
		return chunkMapCC.put(new ChunkCoordinate(chunkX, chunkZ), (Chunk) val);
	}

	@Redirect(method = "unloadAllChunks", at = @At(value = "INVOKE", target = "Ljava/util/Map;clear()V"))
	public void mapNew(Map<Integer, Chunk> instance){
		this.chunkMapCC = new HashMap<>();
	}

	@Redirect(method = "unloadAllChunks", at = @At(value = "INVOKE", target = "Ljava/util/Set;clear()V"))
	public void setNew(Set<Chunk> instance){
		this.droppedChunksSetCC = new HashSet<>();
	}

	@Redirect(method = "unloadAllChunks", at = @At(value = "INVOKE", target = "Ljava/util/List;clear()V"))
	public void listNew(List<Chunk> instance){
		this.chunkList = new ArrayList<>();
	}

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	public void cancelTick(CallbackInfoReturnable<Boolean> cir){
		if(!this.world.dontSave){
			for (int i = 0; i < 100 && !this.droppedChunksSetCC.isEmpty(); i++) {
				ChunkCoordinate cc = this.droppedChunksSetCC.iterator().next();
				Chunk chunk = this.chunkMapCC.get(cc);
				this.saveChunkToFile(chunk);
				chunk.onUnload();
				this.droppedChunksSetCC.remove(cc);
				this.chunkMapCC.remove(cc);
				this.chunkList.remove(chunk);
			}
		}
		cir.setReturnValue(false);
	}

	@Unique
	private ChunkCoordinate getChunkCoordinate(int x, int z) {
		((ICoord)COORD).btafixer$set(x, z);
		return COORD;
	}


}
