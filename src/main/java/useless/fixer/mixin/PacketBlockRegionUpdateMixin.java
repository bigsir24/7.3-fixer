package useless.fixer.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.net.packet.PacketBlockRegionUpdate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import useless.fixer.interfaces.IBuffer;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Stack;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;
/*
Caches chunk arrays to be reused in other packets
UNLIKELY TO BE FIXED, DO NOT REMOVE
 */
@Mixin(value = PacketBlockRegionUpdate.class, remap = false)
public abstract class PacketBlockRegionUpdateMixin implements IBuffer {
	@Shadow
	public byte[] chunk;
	@Shadow
	private int chunkSize;
	@Unique
	private static Stack<byte[]> arrayCache = new Stack<>();

	@Inject(method = "read", at = @At(value = "INVOKE", target = "Ljava/io/DataInputStream;readFully([B)V", shift = At.Shift.AFTER), cancellable = true)
	public void getArray(DataInputStream dis, CallbackInfo ci, @Local(name = "deflatedBuffer") byte[] deflatedBuffer) throws IOException {
		Inflater inflater = new Inflater();
		try {
			inflater.setInput(deflatedBuffer);
			this.chunk = getFixedByteArray();
			inflater.inflate(this.chunk);
		} catch (DataFormatException var8) {
			throw new IOException("Bad compressed data format");
		} finally {
			inflater.end();
		}

		ci.cancel();
	}

	@Unique
	private byte[] getFixedByteArray(){
		if(arrayCache.isEmpty()){
			return new byte[16*16*256*8];
		}

		return arrayCache.pop();
	}

	@Override
	public void btafix$destroyCache() {
		arrayCache = new Stack<>();
	}

	@Override
	public void btafix$returnArray(byte[] array) {
		arrayCache.add(array);
	}
}
