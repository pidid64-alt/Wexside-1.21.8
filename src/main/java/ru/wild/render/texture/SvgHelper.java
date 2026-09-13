package ru.wild.render.texture;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;

public final class SvgHelper {
   private static final Map<SvgHelper.DataRecord, Integer> instance = new HashMap<>();
   private static final Map<SvgHelper.DataRecord, Identifier> data = new HashMap<>();

   private SvgHelper() {
   }

   public static int handle(float var0, float var1) {
      int var2 = Math.round(var0 * var1);
      int var3 = (var2 + 7) / 8 * 8;
      return Math.max(8, Math.min(512, var3));
   }

   public static int handle(Identifier var0, int var1, boolean var2) {
      if (var0 == null) {
         return -1;
      }

      SvgHelper.DataRecord var3 = new SvgHelper.DataRecord(var0, var1, var2);
      Integer var4 = instance.get(var3);
      if (var4 != null) {
         return var4;
      }

      int var5 = handle(var3);
      if (var5 > 0) {
         instance.put(var3, var5);
      }

      return var5;
   }

   private static int handle(SvgHelper.DataRecord var0) {
      MinecraftClient var1 = MinecraftClient.getInstance();

      try (InputStream var2 = var1.getResourceManager().open(var0.source())) {
         int var3 = Math.max(1, Math.min(3, 1024 / Math.max(1, var0.size())));
         int[] var4 = SvgDocumentRenderer.handle(var2, var0.size(), var0.size(), var0.tinted(), var3);
         BufferedImage var5 = new BufferedImage(var0.size(), var0.size(), 2);
         var5.setRGB(0, 0, var0.size(), var0.size(), var4, 0, var0.size());
         ByteArrayOutputStream var6 = new ByteArrayOutputStream(var0.size() * var0.size() * 4);
         ImageIO.write(var5, "png", var6);
         NativeImage var7 = NativeImage.read(new ByteArrayInputStream(var6.toByteArray()));
         NativeImageBackedTexture var8 = new NativeImageBackedTexture(() -> "wild_svg", var7);
         Identifier var9 = Identifier.of(
            "wild", "svg_" + var0.source().getPath().replace('/', '_').replace('.', '_') + "_" + var0.size() + (var0.tinted() ? "_t" : "")
         );
         var1.getTextureManager().registerTexture(var9, var8);
         data.put(var0, var9);
         AbstractTexture var10 = var1.getTextureManager().getTexture(var9);
         if (var10 != null && var10.getGlTexture() instanceof GlTexture var17) {
            int var18 = var17.getGlId();
            return var18 > 0 ? var18 : -1;
         } else {
            return -1;
         }
      } catch (Throwable var16) {
         return -1;
      }
   }

   public static void handle() {
      MinecraftClient var0 = MinecraftClient.getInstance();

      for (Identifier var2 : data.values()) {
         try {
            var0.getTextureManager().destroyTexture(var2);
         } catch (Throwable var4) {
         }
      }

      data.clear();
      instance.clear();
   }

   record DataRecord(Identifier source, int size, boolean tinted) {
   }
}
