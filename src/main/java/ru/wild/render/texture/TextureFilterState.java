package ru.wild.render.texture;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import ru.wild.render.font.FontRegistry;

public final class TextureFilterState {
   private static long instance;
   private static long data;

   private TextureFilterState() {
   }

   public static void handle() {
      long var0 = System.nanoTime();
      if (var0 - instance >= 2000000L) {
         instance = var0;
         resolve();
      }
   }

   public static void process() {
      resolve();
   }

   private static void resolve() {
      try {
         GL13.glActiveTexture(33984);
         GL11.glPixelStorei(3317, 4);
         GL12.glPixelStorei(3314, 0);
      } catch (Throwable var1) {
      }
   }

   public static void compute() {
      long var0 = System.nanoTime();
      if (var0 - data >= 250000000L) {
         data = var0;

         try {
            GL13.glActiveTexture(33984);
            GL11.glPixelStorei(3317, 4);
            GL12.glPixelStorei(3314, 0);
            FontRegistry.process();
         } catch (Throwable var3) {
         }
      }
   }
}
