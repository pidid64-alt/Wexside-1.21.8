package ru.wild.render.shader;

import com.mojang.blaze3d.opengl.GlStateManager;
import java.util.Map;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import ru.wild.gui.theme.LivePreviewRenderer;
import ru.wild.gui.theme.PresetManager;
import ru.wild.gui.theme.ThemeColors;
import ru.wild.render.GlErrorNames;
import ru.wild.render.OpenGlStateSnapshot;
import ru.wild.render.RenderDiagnostics;
import ru.wild.render.VertexArrayBuffer;

public final class DiffuseShaderRenderer {
   private DiffuseShaderRenderer() {
   }

   public static boolean handle(
      LivePreviewRenderer var0, float var1, float var2, float var3, float var4, int var5, int var6, float var7, float var8, ThemeColors var9, float var10
   ) {
      if (var0 != null && !(var3 <= 1.0F) && !(var4 <= 1.0F) && var5 > 0 && var6 > 0 && !(var10 <= 0.001F)) {
         ShaderBuildResult var11 = PresetManager.handle().process(var0);
         if (var11 == null) {
            return false;
         }

         ShaderBuildReporter var12 = ThemeShaderProgramCache.handle().handle(var0, var11);
         return handle(
            var12,
            var11,
            PresetManager.handle().prepare(var0),
            var1,
            var2,
            var3,
            var4,
            var1,
            var2,
            var3,
            var4,
            0.0F,
            var5,
            var6,
            var7,
            var8,
            var9,
            var10,
            ThemeShaderProgramCache.handle().update()
         );
      } else {
         return false;
      }
   }

   public static boolean handle(
      String var0, float var1, float var2, float var3, float var4, int var5, int var6, float var7, float var8, ThemeColors var9, float var10
   ) {
      if (var0 != null && !(var3 <= 1.0F) && !(var4 <= 1.0F) && var5 > 0 && var6 > 0 && !(var10 <= 0.001F)) {
         ShaderBuildResult var11 = PresetManager.handle().process(var0);
         if (var11 == null) {
            return false;
         }

         ShaderBuildReporter var12 = ThemeShaderProgramCache.handle().handle(var0, var11);
         return handle(
            var12,
            var11,
            PresetManager.handle().check(var0),
            var1,
            var2,
            var3,
            var4,
            var1,
            var2,
            var3,
            var4,
            0.0F,
            var5,
            var6,
            var7,
            var8,
            var9,
            var10,
            ThemeShaderProgramCache.handle().update()
         );
      } else {
         return false;
      }
   }

   public static boolean handle(
      String var0, int var1, float var2, float var3, float var4, float var5, int var6, int var7, float var8, float var9, ThemeColors var10, float var11
   ) {
      if (var0 != null && !(var4 <= 1.0F) && !(var5 <= 1.0F) && var6 > 0 && var7 > 0 && !(var11 <= 0.001F)) {
         ShaderBuildResult var12 = PresetManager.handle().process(var0);
         if (var12 == null) {
            return false;
         }

         ShaderBuildReporter var13 = ThemeShaderProgramCache.handle().handle(var0, var12);
         int var14 = var1 > 0 ? var1 : ThemeShaderProgramCache.handle().update();
         return handle(
            var13, var12, PresetManager.handle().check(var0), var2, var3, var4, var5, var2, var3, var4, var5, 0.0F, var6, var7, var8, var9, var10, var11, var14
         );
      } else {
         return false;
      }
   }

   public static boolean handle(
      LivePreviewRenderer var0,
      int var1,
      float var2,
      float var3,
      float var4,
      float var5,
      int var6,
      int var7,
      float var8,
      float var9,
      ThemeColors var10,
      float var11
   ) {
      if (var0 != null && !(var4 <= 1.0F) && !(var5 <= 1.0F) && var6 > 0 && var7 > 0 && !(var11 <= 0.001F)) {
         ShaderBuildResult var12 = PresetManager.handle().process(var0);
         if (var12 == null) {
            return false;
         }

         ShaderBuildReporter var13 = ThemeShaderProgramCache.handle().handle(var0, var12);
         int var14 = var1 > 0 ? var1 : ThemeShaderProgramCache.handle().update();
         return handle(
            var13,
            var12,
            PresetManager.handle().prepare(var0),
            var2,
            var3,
            var4,
            var5,
            var2,
            var3,
            var4,
            var5,
            0.0F,
            var6,
            var7,
            var8,
            var9,
            var10,
            var11,
            var14
         );
      } else {
         return false;
      }
   }

   public static boolean handle(
      String var0,
      float var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      int var10,
      int var11,
      float var12,
      float var13,
      ThemeColors var14,
      float var15
   ) {
      if (var0 != null && !(var3 <= 1.0F) && !(var4 <= 1.0F) && !(var7 <= 1.0F) && !(var8 <= 1.0F) && var10 > 0 && var11 > 0 && !(var15 <= 0.001F)) {
         ShaderBuildResult var16 = PresetManager.handle().process(var0);
         if (var16 == null) {
            return false;
         }

         ShaderBuildReporter var17 = ThemeShaderProgramCache.handle().handle(var0, var16);
         return handle(
            var17,
            var16,
            PresetManager.handle().check(var0),
            var1,
            var2,
            var3,
            var4,
            var5,
            var6,
            var7,
            var8,
            var9,
            var10,
            var11,
            var12,
            var13,
            var14,
            var15,
            ThemeShaderProgramCache.handle().update()
         );
      } else {
         return false;
      }
   }

   public static boolean handle(
      LivePreviewRenderer var0,
      float var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      int var10,
      int var11,
      float var12,
      float var13,
      ThemeColors var14,
      float var15
   ) {
      if (var0 != null && !(var3 <= 1.0F) && !(var4 <= 1.0F) && !(var7 <= 1.0F) && !(var8 <= 1.0F) && var10 > 0 && var11 > 0 && !(var15 <= 0.001F)) {
         ShaderBuildResult var16 = PresetManager.handle().process(var0);
         if (var16 == null) {
            return false;
         }

         ShaderBuildReporter var17 = ThemeShaderProgramCache.handle().handle(var0, var16);
         return handle(
            var17,
            var16,
            PresetManager.handle().prepare(var0),
            var1,
            var2,
            var3,
            var4,
            var5,
            var6,
            var7,
            var8,
            var9,
            var10,
            var11,
            var12,
            var13,
            var14,
            var15,
            ThemeShaderProgramCache.handle().update()
         );
      } else {
         return false;
      }
   }

   public static boolean handle(
      String var0,
      ShaderBuildResult var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      int var11,
      int var12,
      float var13,
      float var14,
      ThemeColors var15,
      float var16
   ) {
      if (var0 != null
         && !var0.isBlank()
         && var1 != null
         && !(var4 <= 1.0F)
         && !(var5 <= 1.0F)
         && !(var8 <= 1.0F)
         && !(var9 <= 1.0F)
         && var11 > 0
         && var12 > 0
         && !(var16 <= 0.001F)) {
         ShaderBuildReporter var17 = ThemeShaderProgramCache.handle().handle(var0, var1);
         return handle(
            var17,
            var1,
            Map.of(),
            var2,
            var3,
            var4,
            var5,
            var6,
            var7,
            var8,
            var9,
            var10,
            var11,
            var12,
            var13,
            var14,
            var15,
            var16,
            ThemeShaderProgramCache.handle().update()
         );
      } else {
         return false;
      }
   }

   public static boolean handle(
      String var0,
      ShaderBuildResult var1,
      float var2,
      float var3,
      float var4,
      float var5,
      int var6,
      int var7,
      float var8,
      float var9,
      ThemeColors var10,
      float var11
   ) {
      if (var0 != null && !var0.isBlank() && var1 != null && !(var4 <= 1.0F) && !(var5 <= 1.0F) && var6 > 0 && var7 > 0 && !(var11 <= 0.001F)) {
         ShaderBuildReporter var12 = ThemeShaderProgramCache.handle().handle(var0, var1);
         return handle(
            var12,
            var1,
            Map.of(),
            var2,
            var3,
            var4,
            var5,
            var2,
            var3,
            var4,
            var5,
            0.0F,
            var6,
            var7,
            var8,
            var9,
            var10,
            var11,
            ThemeShaderProgramCache.handle().update()
         );
      } else {
         return false;
      }
   }

   public static boolean handle(
      String var0,
      ShaderBuildResult var1,
      int var2,
      float var3,
      float var4,
      float var5,
      float var6,
      int var7,
      int var8,
      float var9,
      float var10,
      ThemeColors var11,
      float var12
   ) {
      if (var0 != null && !var0.isBlank() && var1 != null && !(var5 <= 1.0F) && !(var6 <= 1.0F) && var7 > 0 && var8 > 0 && !(var12 <= 0.001F)) {
         ShaderBuildReporter var13 = ThemeShaderProgramCache.handle().handle(var0, var1);
         int var14 = var2 > 0 ? var2 : ThemeShaderProgramCache.handle().update();
         return handle(var13, var1, Map.of(), var3, var4, var5, var6, var3, var4, var5, var6, 0.0F, var7, var8, var9, var10, var11, var12, var14);
      } else {
         return false;
      }
   }
   private static boolean handle(
      ShaderBuildReporter var0,
      ShaderBuildResult var1,
      Map<String, float[]> var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      float var11,
      int var12,
      int var13,
      float var14,
      float var15,
      ThemeColors var16,
      float var17,
      int var18
   ) {
      if (var0 == null) {
         return false;
      }

      VertexArrayBuffer var19 = ThemeShaderProgramCache.handle().process();
      if (var19 == null) {
         return false;
      }

      int var20 = GlErrorNames.handle();
      int var21 = GlErrorNames.process();
      int var22 = GlErrorNames.compute();
      RenderDiagnostics.handle().check();
      OpenGlStateSnapshot.NetworkState var23 = OpenGlStateSnapshot.handle();
      boolean var46 = false /* VF: Semaphore variable */;

      boolean var28;
      try {
         var46 = true;
         GL11.glViewport(0, 0, var12, var13);
         GL11.glDisable(2929);
         GL11.glDisable(2884);
         GL11.glDepthMask(false);
         GlStateManager._enableBlend();
         GL11.glEnable(3042);
         GL14.glBlendFuncSeparate(770, 771, 1, 771);
         GL11.glDisable(36281);
         var0.handle();
         GL13.glActiveTexture(33984);
         GL11.glBindTexture(3553, var18);
         handle(var0, "u_DiffuseMap", 0);
         handle(var0, "uViewport", var12, var13);
         handle(var0, "uRect", var3, var4, var5, var6);
         handle(var0, "u_ElementRect", var7, var8, var9, var10);
         handle(var0, "u_ElementRadius", Math.max(0.0F, var11));
         handle(var0, "u_Time", ThemeShaderProgramCache.handle().compute());
         handle(var0, "u_Resolution", Math.max(1.0F, var12), Math.max(1.0F, var13));
         handle(var0, "u_GlobalUV", var7 / Math.max(1.0F, var12), var8 / Math.max(1.0F, var13));
         handle(var0, "u_Mouse", var14 - var7, var15 - var8);
         int var24 = var16 == null ? -1 : var16.save();
         int var25 = var16 == null ? -16777216 : var16.submit();
         int var26 = var16 == null ? -15724520 : var16.apply();
         int var27 = var16 == null ? -14671832 : var16.execute();
         handle(var0, "u_AccentTop", handle(var24), process(var24), compute(var24));
         handle(var0, "u_AccentBottom", handle(var25), process(var25), compute(var25));
         handle(var0, "u_ThemeColors[0]", handle(var26), process(var26), compute(var26), resolve(var26));
         handle(var0, "u_ThemeColors[1]", handle(var27), process(var27), compute(var27), resolve(var27));
         handle(var0, "u_ThemeColors[2]", handle(var24), process(var24), compute(var24), var17);
         handle(var0, "u_ThemeColors[3]", handle(var25), process(var25), compute(var25), var17);
         handle(var0, "u_Alpha", var17);
         handle(var0, var1, var2);
         var19.handle();
         var28 = true;
         var46 = false;
      } catch (Throwable var49) {
         RenderDiagnostics.handle().process("ThemeShaderDispatcher.drawProgram", var49);
         throw new IllegalStateException("unreachable shader failure", var49);
      } finally {
         if (var46) {
            boolean var41 = false /* VF: Semaphore variable */;

            try {
               var41 = true;
               GL13.glActiveTexture(33984);
               GL11.glBindTexture(3553, 0);
               GL20.glUseProgram(0);
               OpenGlStateSnapshot.compute(var23);
               var41 = false;
            } finally {
               if (var41) {
                  RenderDiagnostics.handle().handle(var20, var21, var22);
               }
            }

            RenderDiagnostics.handle().handle(var20, var21, var22);
         }
      }

      boolean var36 = false /* VF: Semaphore variable */;

      try {
         var36 = true;
         GL13.glActiveTexture(33984);
         GL11.glBindTexture(3553, 0);
         GL20.glUseProgram(0);
         OpenGlStateSnapshot.compute(var23);
         var36 = false;
      } finally {
         if (var36) {
            RenderDiagnostics.handle().handle(var20, var21, var22);
         }
      }

      RenderDiagnostics.handle().handle(var20, var21, var22);
      return var28;
   }

   private static void handle(ShaderBuildReporter var0, String var1, float var2) {
      int var3 = var0.handle(var1);
      if (var3 >= 0) {
         GL20.glUniform1f(var3, var2);
      }
   }

   private static void handle(ShaderBuildReporter var0, String var1, int var2) {
      int var3 = var0.handle(var1);
      if (var3 >= 0) {
         GL20.glUniform1i(var3, var2);
      }
   }

   private static void handle(ShaderBuildReporter var0, String var1, float var2, float var3) {
      int var4 = var0.handle(var1);
      if (var4 >= 0) {
         GL20.glUniform2f(var4, var2, var3);
      }
   }

   private static void handle(ShaderBuildReporter var0, String var1, float var2, float var3, float var4) {
      int var5 = var0.handle(var1);
      if (var5 >= 0) {
         GL20.glUniform3f(var5, var2, var3, var4);
      }
   }

   private static void handle(ShaderBuildReporter var0, String var1, float var2, float var3, float var4, float var5) {
      int var6 = var0.handle(var1);
      if (var6 >= 0) {
         GL20.glUniform4f(var6, var2, var3, var4, var5);
      }
   }

   private static void handle(ShaderBuildReporter var0, ShaderBuildResult var1, Map<String, float[]> var2) {
      if (var0 != null && var1 != null && !var1.exposedUniforms().isEmpty()) {
         for (ShaderParameter var4 : var1.exposedUniforms()) {
            float[] var5 = var2 == null ? null : (float[])var2.get(var4.uniformName());
            if (var5 == null || var5.length == 0) {
               var5 = var4.defaults();
            }

            if (var4.kind() == ShaderParameter.Mode.FLOAT) {
               handle(var0, var4.uniformName(), var5[0]);
            } else {
               float var6 = var5.length > 0 ? var5[0] : 0.0F;
               float var7 = var5.length > 1 ? var5[1] : 0.0F;
               float var8 = var5.length > 2 ? var5[2] : 0.0F;
               float var9 = var5.length > 3 ? var5[3] : 1.0F;
               handle(var0, var4.uniformName(), var6, var7, var8, var9);
            }
         }
      }
   }

   private static float handle(int var0) {
      return (var0 >> 16 & 0xFF) / 255.0F;
   }

   private static float process(int var0) {
      return (var0 >> 8 & 0xFF) / 255.0F;
   }

   private static float compute(int var0) {
      return (var0 & 0xFF) / 255.0F;
   }

   private static float resolve(int var0) {
      return (var0 >>> 24 & 0xFF) / 255.0F;
   }
}
