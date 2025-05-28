package useless.fixer.mixin;

import net.minecraft.client.net.handler.PacketHandlerClient;
import net.minecraft.core.net.packet.PacketBlockRegionUpdate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import useless.fixer.interfaces.IBuffer;
/*
Caches chunk arrays to be reused in other packets
UNLIKELY TO BE FIXED, DO NOT REMOVE
 */
@Mixin(value = PacketHandlerClient.class, remap = false)
public abstract class PacketHandlerClientMixin {

	@Inject(method = "handleMapChunk", at = @At("TAIL"))
	public void completeProcess(PacketBlockRegionUpdate blockRegionUpdatePacket, CallbackInfo ci) {
		//Put the array back into the pool
		((IBuffer)blockRegionUpdatePacket).btafix$returnArray(blockRegionUpdatePacket.chunk);
	}

}
