package ru.wild.render.texture;

import com.mojang.logging.LogUtils;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class GifImageDecoder {
   private final List<GifImageDecoder.DataRecord> instance = new ArrayList<>();
   private int data = 0;

   public GifImageDecoder(Identifier var1) {
      Optional var2 = MinecraftClient.getInstance().getResourceManager().getResource(var1);
      if (var2.isEmpty()) {
         LogUtils.getLogger().error("GIF файл не найден по пути: {}", var1);
      } else {
         try (
            InputStream var3 = ((Resource)var2.get()).getInputStream();
            ImageInputStream var4 = ImageIO.createImageInputStream(var3);
         ) {
            ImageReader var5 = ImageIO.getImageReadersByFormatName("gif").next();
            var5.setInput(var4);
            int var6 = var5.getNumImages(true);
            BufferedImage var7 = null;
            Graphics2D var8 = null;
            int var9 = 0;
            int var10 = 0;

            for (int var11 = 0; var11 < var6; var11++) {
               BufferedImage var12 = var5.read(var11);
               if (var7 == null) {
                  var9 = var12.getWidth();
                  var10 = var12.getHeight();
                  var7 = new BufferedImage(var9, var10, 2);
                  var8 = var7.createGraphics();
                  var8.setBackground(new Color(0, 0, 0, 0));
                  var8.clearRect(0, 0, var9, var10);
               }

               int var13 = 0;
               int var14 = 0;
               int var15 = 100;
               String var16 = "none";

               try {
                  Node var17 = var5.getImageMetadata(var11).getAsTree("javax_imageio_gif_image_1.0");

                  for (int var18 = 0; var18 < var17.getChildNodes().getLength(); var18++) {
                     Node var19 = var17.getChildNodes().item(var18);
                     if (var19.getNodeName().equals("ImageDescriptor")) {
                        NamedNodeMap var20 = var19.getAttributes();
                        if (var20.getNamedItem("imageLeftPosition") != null) {
                           var13 = Integer.parseInt(var20.getNamedItem("imageLeftPosition").getNodeValue());
                        }

                        if (var20.getNamedItem("imageTopPosition") != null) {
                           var14 = Integer.parseInt(var20.getNamedItem("imageTopPosition").getNodeValue());
                        }
                     } else if (var19.getNodeName().equals("GraphicControlExtension")) {
                        NamedNodeMap var32 = var19.getAttributes();
                        if (var32.getNamedItem("delayTime") != null) {
                           var15 = Integer.parseInt(var32.getNamedItem("delayTime").getNodeValue()) * 10;
                        }

                        if (var32.getNamedItem("disposalMethod") != null) {
                           var16 = var32.getNamedItem("disposalMethod").getNodeValue();
                        }
                     }
                  }
               } catch (Exception var24) {
               }

               if (var15 <= 0) {
                  var15 = 100;
               }

               var8.drawImage(var12, var13, var14, null);
               int[] var28 = new int[var9 * var10];
               var7.getRGB(0, 0, var9, var10, var28, 0, var9);
               if (var16.equals("restoreToBackgroundColor")) {
                  var8.clearRect(var13, var14, var12.getWidth(), var12.getHeight());
               }

               ByteBuffer var29 = BufferUtils.createByteBuffer(var9 * var10 * 4);

               for (int var30 = 0; var30 < var10; var30++) {
                  for (int var33 = 0; var33 < var9; var33++) {
                     int var21 = var28[var30 * var9 + var33];
                     var29.put((byte)(var21 >> 16 & 0xFF));
                     var29.put((byte)(var21 >> 8 & 0xFF));
                     var29.put((byte)(var21 & 0xFF));
                     var29.put((byte)(var21 >> 24 & 0xFF));
                  }
               }

               var29.flip();
               int var31 = GL11.glGenTextures();
               GL11.glBindTexture(3553, var31);
               GL11.glPixelStorei(3317, 1);
               GL11.glTexParameteri(3553, 10241, 9729);
               GL11.glTexParameteri(3553, 10240, 9729);
               GL11.glTexParameteri(3553, 10242, 33071);
               GL11.glTexParameteri(3553, 10243, 33071);
               GL11.glTexImage2D(3553, 0, 32856, var9, var10, 0, 6408, 5121, var29);
               this.instance.add(new GifImageDecoder.DataRecord(var31, var15));
               this.data += var15;
            }

            if (var8 != null) {
               var8.dispose();
            }

            var5.dispose();
         } catch (Exception var27) {
            LogUtils.getLogger().error("Ошибка при обработке GIF файла", var27);
         }
      }
   }

   public int handle() {
      if (this.instance.isEmpty()) {
         return -1;
      }

      if (this.instance.size() == 1) {
         return this.instance.getFirst().id();
      }

      int var1 = (int)(System.currentTimeMillis() % Math.max(1, this.data));
      int var2 = 0;

      for (GifImageDecoder.DataRecord var4 : this.instance) {
         var2 += var4.delay();
         if (var1 <= var2) {
            return var4.id();
         }
      }

      return this.instance.getLast().id();
   }

   record DataRecord(int id, int delay) {
   }
}
