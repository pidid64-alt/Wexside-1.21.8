package ru.wild.render.texture;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

public final class StudioTextureLoader {
   private static final StudioTextureLoader instance = new StudioTextureLoader();
   private static final int data = -1;
   private final Map<String, Integer> context = new HashMap<>();

   private StudioTextureLoader() {
   }

   public static StudioTextureLoader handle() {
      return instance;
   }

   public int handle(String var1, int var2, TextureHolder var3) {
      if (var3 == null) {
         return 0;
      }

      TextureHolder.PrimaryTextureState var4 = var3.handle(var2);
      if (var4 != null && var4.process().length != 0) {
         String var5 = var1 + "|" + var2;
         Integer var6 = this.context.get(var5);
         if (var6 != null) {
            return var6 == -1 ? 0 : var6;
         }

         int var7 = this.handle(var4);
         this.context.put(var5, var7 <= 0 ? -1 : var7);
         return var7 <= 0 ? 0 : var7;
      } else {
         return 0;
      }
   }
   private int handle(TextureHolder.PrimaryTextureState var1) {
      byte[] var2 = var1.process();
      ByteBuffer var3 = MemoryUtil.memAlloc(var2.length);

      int var23;
      try {
         var3.put(var2).flip();
         MemoryStack var4 = MemoryStack.stackPush();

         label366: {
            byte var24;
            label367: {
               int var57;
               label368: {
                  int var14;
                  try {
                     IntBuffer var5 = var4.mallocInt(1);
                     IntBuffer var6 = var4.mallocInt(1);
                     IntBuffer var7 = var4.mallocInt(1);
                     ByteBuffer var8 = STBImage.stbi_load_from_memory(var3, var5, var6, var7, 4);
                     if (var8 == null) {
                        System.out.println("[Studio] texture decode failed: " + STBImage.stbi_failure_reason());
                        var57 = 0;
                        break label368;
                     }

                     try {
                        var57 = var5.get(0);
                        int var10 = var6.get(0);
                        long var11 = (long)var57 * var10 * 4L;
                        int var13 = GL11.glGetInteger(3379);
                        if (var57 > 0 && var10 > 0 && var57 <= var13 && var10 <= var13 && var11 <= var8.remaining()) {
                           var14 = GL11.glGetInteger(32873);
                           int var15 = GL11.glGetInteger(35055);
                           int var16 = GL11.glGetInteger(3317);
                           int var17 = GL11.glGetInteger(3314);
                           int var18 = GL11.glGetInteger(3315);
                           int var19 = GL11.glGetInteger(3316);
                           int var20 = GL11.glGetInteger(3312);
                           int var21 = GL11.glGetInteger(3313);
                           int var22 = 0;
                           boolean var49 = false /* VF: Semaphore variable */;

                           label323: {
                              try {
                                 var49 = true;
                                 var22 = GL11.glGenTextures();
                                 GL11.glBindTexture(3553, var22);
                                 GL11.glTexParameteri(3553, 10241, 9728);
                                 GL11.glTexParameteri(3553, 10240, 9728);
                                 GL11.glTexParameteri(3553, 10242, 33071);
                                 GL11.glTexParameteri(3553, 10243, 33071);
                                 GL15.glBindBuffer(35052, 0);
                                 GL11.glPixelStorei(3317, 1);
                                 GL11.glPixelStorei(3314, 0);
                                 GL11.glPixelStorei(3315, 0);
                                 GL11.glPixelStorei(3316, 0);
                                 GL11.glPixelStorei(3312, 0);
                                 GL11.glPixelStorei(3313, 0);
                                 GL11.glTexImage2D(3553, 0, 32856, var57, var10, 0, 6408, 5121, var8);
                                 var23 = var22;
                                 var49 = false;
                                 break label323;
                              } catch (Throwable var51) {
                                 if (var22 > 0) {
                                    GL11.glDeleteTextures(var22);
                                 }

                                 System.out.println("[Studio] texture upload failed: " + var51.getClass().getSimpleName() + ": " + var51.getMessage());
                                 var24 = 0;
                                 var49 = false;
                              } finally {
                                 if (var49) {
                                    GL11.glPixelStorei(3313, var21);
                                    GL11.glPixelStorei(3312, var20);
                                    GL11.glPixelStorei(3316, var19);
                                    GL11.glPixelStorei(3315, var18);
                                    GL11.glPixelStorei(3314, var17);
                                    GL11.glPixelStorei(3317, var16);
                                    GL15.glBindBuffer(35052, var15);
                                    GL11.glBindTexture(3553, var14);
                                 }
                              }

                              GL11.glPixelStorei(3313, var21);
                              GL11.glPixelStorei(3312, var20);
                              GL11.glPixelStorei(3316, var19);
                              GL11.glPixelStorei(3315, var18);
                              GL11.glPixelStorei(3314, var17);
                              GL11.glPixelStorei(3317, var16);
                              GL15.glBindBuffer(35052, var15);
                              GL11.glBindTexture(3553, var14);
                              break label367;
                           }

                           GL11.glPixelStorei(3313, var21);
                           GL11.glPixelStorei(3312, var20);
                           GL11.glPixelStorei(3316, var19);
                           GL11.glPixelStorei(3315, var18);
                           GL11.glPixelStorei(3314, var17);
                           GL11.glPixelStorei(3317, var16);
                           GL15.glBindBuffer(35052, var15);
                           GL11.glBindTexture(3553, var14);
                           break label366;
                        }

                        System.out.println("[Studio] invalid texture dimensions: " + var57 + "x" + var10);
                        var14 = 0;
                     } finally {
                        STBImage.stbi_image_free(var8);
                     }
                  } catch (Throwable var54) {
                     if (var4 != null) {
                        try {
                           var4.close();
                        } catch (Throwable var50) {
                           var54.addSuppressed(var50);
                        }
                     }

                     throw var54;
                  }

                  if (var4 != null) {
                     var4.close();
                  }

                  return var14;
               }

               if (var4 != null) {
                  var4.close();
               }

               return var57;
            }

            if (var4 != null) {
               var4.close();
            }

            return var24;
         }

         if (var4 != null) {
            var4.close();
         }
      } catch (Throwable var55) {
         System.out.println("[Studio] texture upload failed: " + var55.getClass().getSimpleName() + ": " + var55.getMessage());
         return 0;
      } finally {
         MemoryUtil.memFree(var3);
      }

      return var23;
   }

   public void handle(String var1) {
      String var2 = var1 + "|";
      this.context.entrySet().removeIf(var1x -> {
         if (var1x.getKey().startsWith(var2)) {
            return false;
         }

         int var2x = var1x.getValue();
         if (var2x > 0) {
            try {
               GL11.glDeleteTextures(var2x);
            } catch (Throwable var4) {
            }
         }

         return true;
      });
   }
}
