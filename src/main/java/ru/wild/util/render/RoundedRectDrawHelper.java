package ru.wild.util.render;

import org.joml.Vector4f;

public final class RoundedRectDrawHelper {
   private RoundedRectDrawHelper() {
   }

   public static void handle(float var0, float var1, float var2, float var3, Vector4f var4, int var5) {
   }

   public static void handle(RoundedRectRenderer var0, float var1, float var2, float var3, float var4, Vector4f var5, int var6) {
      var0.handle(var1, var2, var3, var4, var5.x, var5.y, var5.z, var5.w, var6);
   }

   public static void handle(RoundedRectRenderer var0, float var1, float var2, float var3, float var4, float var5, int var6) {
      var0.handle(var1, var2, var3, var4, var5, var5, var5, var5, var6);
   }

   public static class State {
      private State() {
      }
   }
}
