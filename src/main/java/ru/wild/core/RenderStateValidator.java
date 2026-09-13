package ru.wild.core;

import com.mojang.blaze3d.systems.RenderSystem;
import java.io.DataOutputStream;
import java.io.IOException;
import org.joml.Matrix4f;
import ru.wild.security.BuildFingerprint;

public final class RenderStateValidator {
   private RenderStateValidator() {
   }

   public static boolean handle(BuildFingerprint var0) {
      try {
         Matrix4f var1 = RenderSystem.getModelViewMatrix();
         long var2 = handle(var1);
         if (var0 != null) {
            var0.process(var2);
         }

         return process(var1);
      } catch (Throwable var4) {
         if (var0 != null) {
            var0.handle(-1160725808);
         }

         return false;
      }
   }

   public static void handle(DataOutputStream var0) throws IOException {
      try {
         Matrix4f var1 = RenderSystem.getModelViewMatrix();
         var0.writeLong(handle(var1));
         var0.writeBoolean(process(var1));
         var0.writeFloat(var1.m00());
         var0.writeFloat(var1.m01());
         var0.writeFloat(var1.m02());
         var0.writeFloat(var1.m03());
         var0.writeFloat(var1.m10());
         var0.writeFloat(var1.m11());
         var0.writeFloat(var1.m12());
         var0.writeFloat(var1.m13());
         var0.writeFloat(var1.m20());
         var0.writeFloat(var1.m21());
         var0.writeFloat(var1.m22());
         var0.writeFloat(var1.m23());
         var0.writeFloat(var1.m30());
         var0.writeFloat(var1.m31());
         var0.writeFloat(var1.m32());
         var0.writeFloat(var1.m33());
      } catch (Throwable var3) {
         var0.writeLong(0L);
         var0.writeBoolean(false);

         for (int var2 = 0; var2 < 16; var2++) {
            var0.writeFloat(Float.NaN);
         }
      }
   }

   static long handle(Matrix4f var0) {
      if (var0 == null) {
         return 0L;
      }

      long var1 = -3750763034362895579L;
      var1 = handle(var1, var0.m00());
      var1 = handle(var1, var0.m01());
      var1 = handle(var1, var0.m02());
      var1 = handle(var1, var0.m03());
      var1 = handle(var1, var0.m10());
      var1 = handle(var1, var0.m11());
      var1 = handle(var1, var0.m12());
      var1 = handle(var1, var0.m13());
      var1 = handle(var1, var0.m20());
      var1 = handle(var1, var0.m21());
      var1 = handle(var1, var0.m22());
      var1 = handle(var1, var0.m23());
      var1 = handle(var1, var0.m30());
      var1 = handle(var1, var0.m31());
      var1 = handle(var1, var0.m32());
      return handle(var1, var0.m33());
   }

   private static long handle(long var0, float var2) {
      var0 ^= Float.floatToRawIntBits(var2);
      return var0 * 1099511628211L;
   }

   private static boolean process(Matrix4f var0) {
      return var0 != null
         && handle(var0.m00())
         && handle(var0.m01())
         && handle(var0.m02())
         && handle(var0.m03())
         && handle(var0.m10())
         && handle(var0.m11())
         && handle(var0.m12())
         && handle(var0.m13())
         && handle(var0.m20())
         && handle(var0.m21())
         && handle(var0.m22())
         && handle(var0.m23())
         && handle(var0.m30())
         && handle(var0.m31())
         && handle(var0.m32())
         && handle(var0.m33());
   }

   private static boolean handle(float var0) {
      return !Float.isNaN(var0) && !Float.isInfinite(var0);
   }
}
