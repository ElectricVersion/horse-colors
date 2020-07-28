package sekelsta.horse_colors.renderer;

import net.minecraft.client.renderer.texture.*;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import com.mojang.blaze3d.platform.TextureUtil;
import net.minecraft.resources.IResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CustomLayeredTexture extends Texture {
    public final List<TextureLayer> layers;

    public CustomLayeredTexture(List<TextureLayer> textures) {
        this.layers = Lists.newArrayList(textures);
        if (this.layers.isEmpty()) {
            throw new IllegalStateException("Layered texture with no layers.");
        }
    }

    public void loadTexture(IResourceManager manager) throws IOException {
        Iterator<TextureLayer> iterator = this.layers.iterator();
        TextureLayer baselayer = iterator.next();
        NativeImage baseimage = baselayer.getLayer(manager);
        if (baseimage == null) {
            // getLayer() will already have logged an error
            return;
        }
        baselayer.colorLayer(baseimage);

        while(iterator.hasNext()) {
            TextureLayer layer = iterator.next();
            if (layer == null) {
                continue;
            }
            if (layer.name != null) {
                NativeImage image = layer.getLayer(manager);
                if (image != null) {
                    layer.combineLayers(baseimage, image);
                }
            }
        }

        this.loadImage(baseimage);
   }

   private void loadImage(NativeImage imageIn) {
      TextureUtil.prepareImage(this.getGlTextureId(), imageIn.getWidth(), imageIn.getHeight());
      imageIn.uploadTextureSub(0, 0, 0, true);
   }


}
