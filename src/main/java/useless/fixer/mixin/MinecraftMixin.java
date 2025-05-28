package useless.fixer.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.core.net.packet.PacketBlockRegionUpdate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import useless.fixer.interfaces.IBuffer;
/*
Destroys the chunk cache when out of memory
UNLIKELY TO BE FIXED, DO NOT REMOVE
 */
@Mixin(value = Minecraft.class, remap = false)
public abstract class MinecraftMixin {
	@Inject(method = "freeUpMemory", at = @At("HEAD"))
	public void destroyCache(CallbackInfo ci) {
		//I don't think this will ever run, but who knows
		((IBuffer)new PacketBlockRegionUpdate()).btafix$destroyCache();
	}
}
