package ru.wild.render.texture;

public final class TextureCoordinateMath {
   private TextureCoordinateMath() {
   }

   public static float handle(float var0, float var1, float var2) {
      return (var0 + var1) * 0.5F * var2;
   }

   public static float handle(float var0, float var1, float var2, float var3) {
      return var0 - handle(var1, var2, var3);
   }
}
