package useless.fixer.mixin;

import net.minecraft.core.net.NetworkManager;
import net.minecraft.core.net.packet.PacketBlockRegionUpdate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import useless.fixer.interfaces.IBuffer;
/*
Destroys the chunk cache on disconnect
UNLIKELY TO BE FIXED, DO NOT REMOVE
 */
@Mixin(value = NetworkManager.class, remap = false)
public abstract class NetworkManagerMixin {
	@Inject(method = "networkShutdown", at = @At("HEAD"))
	public void clearPacketCache(String reason, Object[] reasonObjects, CallbackInfo ci) {
		((IBuffer)new PacketBlockRegionUpdate()).btafix$destroyCache();
	}
}
