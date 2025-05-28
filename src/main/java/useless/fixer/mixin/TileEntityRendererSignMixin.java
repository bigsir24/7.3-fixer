package useless.fixer.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.LightmapHelper;
import net.minecraft.client.render.tessellator.Tessellator;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.tileentity.TileEntityRendererSign;
import net.minecraft.core.block.entity.TileEntitySign;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
/*
Fixes sign transparency issues introduced in 7.3_03
TO BE REMOVED IN VERSIONS PAST 7.3_03
 */
@Mixin(value = TileEntityRendererSign.class, remap = false)
public abstract class TileEntityRendererSignMixin {
	@Redirect(method = "doRender(Lnet/minecraft/client/render/tessellator/Tessellator;Lnet/minecraft/core/block/entity/TileEntitySign;DDDF)V", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glColor4f(FFFF)V", ordinal = 0))
	public void glLeakFix(float r, float g, float b, float a, @Local(name = "tileEntity") TileEntitySign tileEntity) {
		if (LightmapHelper.isLightmapEnabled()) {
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		}else {
			int x = tileEntity.x;
			int y = tileEntity.y;
			int z = tileEntity.z;
			float brightness = Minecraft.getMinecraft().fullbright || tileEntity.worldObj == null ? 1.0F : tileEntity.worldObj.getBrightness(x, y, z, 0);
			GL11.glColor4f(brightness, brightness, brightness, 1.0F);
		}
	}

	@Inject(method = "drawTexturedModalRect", at = @At(value = "HEAD"))
	private static void fixBlendHead(double width, double height, int color, IconCoordinate coordinate, CallbackInfo ci) {
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	@Inject(method = "drawTexturedModalRect", at = @At(value = "TAIL"))
	private static void fixBlendTail(double width, double height, int color, IconCoordinate coordinate, CallbackInfo ci) {
		GL11.glDisable(GL11.GL_BLEND);
	}
}
