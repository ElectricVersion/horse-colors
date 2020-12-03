package sekelsta.horse_colors.client.renderer;

import net.minecraft.client.renderer.texture.*;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.IOException;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CustomLayeredTexture extends Texture {
    public final TextureLayerGroup layerGroup;

    public CustomLayeredTexture(TextureLayerGroup layers) {
        this.layerGroup = layers;
        if (this.layerGroup.layers.isEmpty()) {
            throw new IllegalStateException("Layered texture with no layers.");
        }
    }


    public void loadTexture(IResourceManager manager) throws IOException {
        NativeImage image = layerGroup.getLayer(manager);

        if (!RenderSystem.isOnRenderThreadOrInit()) {
            RenderSystem.recordRenderCall(() -> {
                this.loadImage(image);
            });
        } else {
            this.loadImage(image);
        }
   }

   private void loadImage(NativeImage imageIn) {
      TextureUtil.prepareImage(this.getGlTextureId(), imageIn.getWidth(), imageIn.getHeight());
      imageIn.uploadTextureSub(0, 0, 0, true);
   }


}
