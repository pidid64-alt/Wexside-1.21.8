package ru.wild.render.shader;

import com.mojang.blaze3d.opengl.GlStateManager;
import java.lang.reflect.Method;
import java.nio.FloatBuffer;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.system.MemoryUtil;
import ru.wild.WildClient;
import ru.wild.gui.theme.LivePreviewRenderer;
import ru.wild.gui.theme.PresetManager;
import ru.wild.gui.theme.ThemeColors;
import ru.wild.gui.theme.ThemePalette;
import ru.wild.gui.theme.ThemePaletteRegistry;
import ru.wild.gui.theme.ThemeRenderer;
import ru.wild.modules.visuals.Hud;
import ru.wild.render.OpenGlStateSnapshot;
import ru.wild.render.RenderDiagnostics;
import ru.wild.render.VertexArrayBuffer;
import ru.wild.util.render.RoundedRectRenderer;

public final class ThemeShaderApplier {
   private static final String instance = "assets/wild/shaders/mainmenu/menu_quad.vert";
   private static final String data = "assets/wild/shaders/advanced_neumorphism.frag";
   private static final String context = "assets/wild/shaders/advanced_neumorphism_batch.vert";
   private static final String config = "assets/wild/shaders/advanced_neumorphism_batch.frag";
   private static final int state = 128;
   private static final int cache = 7;
   private static final int output = 3;
   private static final ThemePaletteRegistry current = ThemePaletteRegistry.handle();
   private static Boolean active;
   private static Method mode;
   private static Method selection;
   private static ShaderBuildReporter enabled;
   private static ShaderBuildReporter renderer;
   private static int handler;
   private static int animationDraw;
   private static int pointEncode;
   private static int animator;
   private static final ThemeShaderApplier.ColorState[] source = render();
   private static final FloatBuffer target = MemoryUtil.memAllocFloat(3584);
   private static String pending = "";

   private ThemeShaderApplier() {
   }

   public static void handle() {
      if (pointEncode++ == 0) {
         animator = 0;
         RoundedRectRenderer var0 = WildClient.handle();
         if (var0 != null) {
            try {
               var0.compute();
            } catch (Throwable var2) {
            }
         }
      }
   }

   public static void process() {
      if (animator > 0) {
         RoundedRectRenderer var0 = WildClient.handle();
         if (var0 != null) {
            try {
               var0.compute();
            } catch (Throwable var2) {
            }
         }

         check();
         animator = 0;
      }
   }
   public static void compute() {
      if (pointEncode <= 0) {
         pointEncode = 0;
      } else {
         boolean var2 = false /* VF: Semaphore variable */;

         try {
            var2 = true;
            process();
            var2 = false;
         } finally {
            if (var2) {
               pointEncode--;
               if (pointEncode == 0) {
                  animator = 0;
               }
            }
         }

         pointEncode--;
         if (pointEncode == 0) {
            animator = 0;
         }
      }
   }

   public static boolean handle(LivePreviewRenderer var0) {
      return PresetManager.handle().update(var0);
   }

   public static boolean handle(
      LivePreviewRenderer var0, float var1, float var2, float var3, float var4, int var5, int var6, float var7, float var8, ThemeColors var9, float var10
   ) {
      return !handle(var0) ? false : DiffuseShaderRenderer.handle(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public static String process(LivePreviewRenderer var0) {
      return ThemeShaderProgramCache.handle().process(var0);
   }

   public static String compute(LivePreviewRenderer var0) {
      return ThemeShaderProgramCache.handle().handle(var0);
   }

   public static boolean handle(
      String var0, float var1, float var2, float var3, float var4, int var5, int var6, float var7, float var8, ThemeColors var9, float var10
   ) {
      return PresetManager.handle().update(var0)
         ? DiffuseShaderRenderer.handle(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10)
         : DiffuseQuadShader.handle().handle(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public static boolean handle(MatrixStack var0, float var1, float var2, float var3, float var4, float var5) {
      return handle(var0, var1, var2, var3, var4, var5, 1.0F);
   }

   public static boolean handle(MatrixStack var0, float var1, float var2, float var3, float var4, float var5, float var6) {
      String var7 = Hud.refresh();
      return handle(var0, var7, var1, var2, var3, var4, var5, var6);
   }

   public static boolean handle(MatrixStack var0, String var1, float var2, float var3, float var4, float var5, float var6) {
      return handle(var0, var1, var2, var3, var4, var5, var6, 1.0F);
   }

   public static boolean handle(MatrixStack var0, float var1, float var2, float var3, float var4, float var5, boolean var6) {
      return handle(var0, var1, var2, var3, var4, var5, var6, 1.0F);
   }

   public static boolean handle(MatrixStack var0, float var1, float var2, float var3, float var4, float var5, boolean var6, float var7) {
      return handle(var0, var1, var2, var3, var4, var5, var6, var7, select());
   }

   public static boolean handle(
      MatrixStack var0, float var1, float var2, float var3, float var4, float var5, boolean var6, float var7, ThemeShaderApplier.DataRecord var8
   ) {
      ThemeShaderApplier.DataRecord var9 = var8 == null ? select() : var8;
      return process(var0, var1, var2, var3, var4, var5, var9.distance(), var9.blur(), var9.intensity(), var9.shape(), var6, var7);
   }

   public static boolean handle(
      MatrixStack var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, int var9, boolean var10
   ) {
      return handle(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, 1.0F);
   }

   public static boolean handle(
      MatrixStack var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, int var9, boolean var10, float var11
   ) {
      return process(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   public static void process(
      MatrixStack var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, int var9, boolean var10
   ) {
      process(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, 1.0F);
   }

   private static boolean process(
      MatrixStack var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, int var9, boolean var10, float var11
   ) {
      MinecraftClient var12 = MinecraftClient.getInstance();
      if (var12 != null && var12.getWindow() != null && !(var3 <= 1.0F) && !(var4 <= 1.0F) && !(var11 <= 0.001F)) {
         int var13 = var12.getWindow().getFramebufferWidth();
         int var14 = var12.getWindow().getFramebufferHeight();
         if (var13 > 0 && var14 > 0) {
            RoundedRectRenderer var15 = WildClient.handle();
            if (pointEncode <= 0 && var15 != null) {
               try {
                  var15.compute();
               } catch (Throwable var32) {
               }
            }

            ThemeShaderApplier.PrimaryDataRecord var16 = handle(var15, var0, var1, var2, var3, var4);
            float var17 = var16.maxX - var16.minX;
            float var18 = var16.maxY - var16.minY;
            if (!(var17 <= 1.0F) && !(var18 <= 1.0F)) {
               ThemeShaderApplier.DataRecord var19 = new ThemeShaderApplier.DataRecord(var6, var7, var8, var9);
               float var20 = Math.min(var17 / Math.max(var3, 1.0F), var18 / Math.max(var4, 1.0F));
               float var21 = Math.max(0.0F, var5 * var20);
               float var22 = Math.max(0.5F, var19.distance() * var20);
               float var23 = Math.max(1.0F, var19.blur() * var20);
               ThemeShaderApplier.DataRecord var24 = new ThemeShaderApplier.DataRecord(var22, var23, var19.intensity(), var19.shape());
               float var25 = var10 ? Math.max(2.0F, Math.min(18.0F, var22 + var23 * 0.32F)) : Math.max(6.0F, Math.min(96.0F, var22 + var23 * 1.35F));
               float var26 = var16.minX - var25;
               float var27 = var16.minY - var25;
               float var28 = var17 + var25 * 2.0F;
               float var29 = var18 + var25 * 2.0F;
               ThemePaletteRegistry.ColorStop var30 = ThemePaletteRegistry.handle(resolve());
               if (pointEncode <= 0) {
                  ShaderBuildReporter var31 = execute();
                  return var31 == null
                     ? false
                     : handle(
                        var31,
                        var26,
                        var27,
                        var28,
                        var29,
                        var16.minX,
                        var16.minY,
                        var17,
                        var18,
                        var21,
                        var13,
                        var14,
                        var30,
                        var10,
                        Math.min(1.0F, var11),
                        var24
                     );
               }

               if (prepare() == null) {
                  return false;
               }

               handle(var26, var27, var28, var29, var16.minX, var16.minY, var17, var18, var21, var13, var14, var30, var10, Math.min(1.0F, var11), var24);
               return true;
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static ThemeShaderApplier.DataRecord handle(float var0, float var1, float var2, String var3) {
      return new ThemeShaderApplier.DataRecord(var0, var1, var2, resolve(var3));
   }

   public static boolean resolve() {
      return current.compute(apply());
   }

   public static int handle(float var0) {
      return handle(ThemePaletteRegistry.handle(resolve()).baseColor(), var0);
   }

   public static int process(float var0) {
      return handle(resolve() ? -14670285 : -591617, var0);
   }

   public static int compute(float var0) {
      return handle(resolve() ? -10194811 : -5524281, var0);
   }

   public static boolean handle(MatrixStack var0, String var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      String var8 = var1 == null ? "" : var1.trim();
      if (var8.isBlank()) {
         return false;
      }

      MinecraftClient var9 = MinecraftClient.getInstance();
      if (var9 != null && var9.getWindow() != null && !(var4 <= 1.0F) && !(var5 <= 1.0F) && !(var7 <= 0.001F)) {
         int var10 = var9.getWindow().getFramebufferWidth();
         int var11 = var9.getWindow().getFramebufferHeight();
         if (var10 > 0 && var11 > 0) {
            LivePreviewRenderer var12 = compute(var8);
            if (var12 != LivePreviewRenderer.HUD) {
               return false;
            }

            ShaderBuildReporter var13 = ShaderTargetResolver.resolve(var8);
            ShaderBuildResult var14 = PresetManager.handle().process(var8);
            if (var13 != null && var14 != null) {
               RoundedRectRenderer var15 = WildClient.handle();
               if (var15 != null) {
                  try {
                     var15.compute();
                  } catch (Throwable var24) {
                  }
               }

               ThemeShaderApplier.PrimaryDataRecord var16 = handle(var15, var0, var2, var3, var4, var5);
               float var17 = var16.maxX - var16.minX;
               float var18 = var16.maxY - var16.minY;
               if (!(var17 <= 1.0F) && !(var18 <= 1.0F)) {
                  float var19 = Math.min(var17 / Math.max(var4, 1.0F), var18 / Math.max(var5, 1.0F));
                  float var20 = Math.max(0.0F, var6 * var19);
                  float var21 = ThemeRenderer.handle().execute();
                  float var22 = ThemeRenderer.handle().prepare();
                  ThemeColors var23 = update();
                  return handle(
                     var13,
                     var14,
                     PresetManager.handle().check(var8),
                     var16.minX,
                     var16.minY,
                     var17,
                     var18,
                     var16.minX,
                     var16.minY,
                     var17,
                     var18,
                     var20,
                     var10,
                     var11,
                     var21,
                     var22,
                     var23,
                     Math.min(1.0F, var7)
                  );
               } else {
                  return false;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean handle(
      MatrixStack var0,
      LivePreviewRenderer var1,
      float var2,
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
      if (var1 != null && var1.resolve() == LivePreviewRenderer.HUD && !(var4 <= 1.0F) && !(var5 <= 1.0F) && var7 > 0 && var8 > 0 && !(var12 <= 0.001F)) {
         RoundedRectRenderer var13 = WildClient.handle();
         if (var13 != null) {
            try {
               var13.compute();
            } catch (Throwable var25) {
            }
         }

         ThemeShaderApplier.PrimaryDataRecord var14 = handle(var13, var0, var2, var3, var4, var5);
         float var15 = var14.maxX - var14.minX;
         float var16 = var14.maxY - var14.minY;
         if (!(var15 <= 1.0F) && !(var16 <= 1.0F)) {
            float var17 = Math.min(var15 / Math.max(var4, 1.0F), var16 / Math.max(var5, 1.0F));
            float var18 = Math.max(0.0F, var6 * var17);
            float var19 = Math.max(12.0F, Math.min(64.0F, Math.min(var15, var16) * 0.38F));
            float var20 = var14.minX - var19;
            float var21 = var14.minY - var19;
            float var22 = var15 + var19 * 2.0F;
            float var23 = var16 + var19 * 2.0F;
            ThemeColors var24 = var11 == null ? update() : var11;
            return DiffuseShaderRenderer.handle(
               var1, var20, var21, var22, var23, var14.minX, var14.minY, var15, var16, var18, var7, var8, var9, var10, var24, Math.min(1.0F, var12)
            );
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static boolean handle(
      String var0, int var1, float var2, float var3, float var4, float var5, int var6, int var7, float var8, float var9, ThemeColors var10, float var11
   ) {
      return !PresetManager.handle().update(var0)
         ? false
         : DiffuseShaderRenderer.handle(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   public static void handle(String var0) {
      DiffuseQuadShader.handle().handle(var0);
   }

   private static ThemeColors update() {
      ThemePalette var0 = apply();
      return ThemeColors.handle(var0, current.compute(var0));
   }

   private static ThemePalette apply() {
      return WildClient.instance != null && WildClient.instance.selection != null ? WildClient.instance.selection.process() : ThemePalette.WILD;
   }

   private static boolean process(String var0) {
      return compute(var0) == LivePreviewRenderer.HUD;
   }

   private static LivePreviewRenderer compute(String var0) {
      ShaderGraph var1 = PresetManager.handle().compute(var0);
      return var1 == null ? LivePreviewRenderer.PREVIEW_ONLY : LivePreviewRenderer.handle(var1.process()).resolve();
   }

   private static synchronized ShaderBuildReporter execute() {
      if (enabled != null) {
         return enabled;
      }

      try {
         enabled = ShaderBuildReporter.handle("assets/wild/shaders/mainmenu/menu_quad.vert", "assets/wild/shaders/advanced_neumorphism.frag");
         pending = "";
         return enabled;
      } catch (Throwable var1) {
         pending = var1.getMessage() == null ? var1.getClass().getSimpleName() : var1.getMessage();
         enabled = null;
         RenderDiagnostics.handle().process("ThemeShaderApply.acquireNeumorphicProgram", var1);
         throw new IllegalStateException("unreachable shader failure", var1);
      }
   }

   private static synchronized ShaderBuildReporter prepare() {
      if (renderer != null) {
         return renderer;
      }

      try {
         renderer = ShaderBuildReporter.handle("assets/wild/shaders/advanced_neumorphism_batch.vert", "assets/wild/shaders/advanced_neumorphism_batch.frag");
         int var0 = GL31.glGetUniformBlockIndex(renderer.compute(), "NeumorphicPlateBlock");
         if (var0 >= 0) {
            GL31.glUniformBlockBinding(renderer.compute(), var0, 3);
         }

         if (handler == 0) {
            handler = GL30.glGenVertexArrays();
         }

         if (animationDraw == 0) {
            animationDraw = GL15.glGenBuffers();
            GL15.glBindBuffer(35345, animationDraw);
            GL15.glBufferData(35345, target.capacity() * 4L, 35040);
            GL15.glBindBuffer(35345, 0);
         }

         pending = "";
         return renderer;
      } catch (Throwable var1) {
         pending = var1.getMessage() == null ? var1.getClass().getSimpleName() : var1.getMessage();
         renderer = null;
         RenderDiagnostics.handle().process("ThemeShaderApply.acquireNeumorphicBatchProgram", var1);
         throw new IllegalStateException("unreachable shader failure", var1);
      }
   }

   private static void handle(
      float var0,
      float var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      int var9,
      int var10,
      ThemePaletteRegistry.ColorStop var11,
      boolean var12,
      float var13,
      ThemeShaderApplier.DataRecord var14
   ) {
      if (animator >= 128) {
         process();
      }

      ThemeShaderApplier.ColorState var15 = source[animator++];
      var15.handle(
         var0,
         var1,
         var2,
         var3,
         var4,
         var5,
         var6,
         var7,
         var8,
         var14.distance(),
         var14.blur(),
         var14.intensity(),
         var14.shape(),
         var12 ? 1 : 0,
         var9,
         var10,
         var11.baseColor(),
         var11.darkShadowColor(),
         var11.lightShadowColor(),
         var13
      );
   }
   private static void check() {
      ShaderBuildReporter var0 = prepare();
      if (var0 != null && animator > 0) {
         int var1 = 0;
         int var2 = 0;

         for (int var3 = 0; var3 < animator; var3++) {
            var1 = Math.max(var1, source[var3].animationDraw);
            var2 = Math.max(var2, source[var3].pointEncode);
         }

         if (var1 > 0 && var2 > 0) {
            onTick();
            OpenGlStateSnapshot.NetworkState var11 = OpenGlStateSnapshot.handle();
            boolean var8 = false /* VF: Semaphore variable */;

            label90: {
               try {
                  var8 = true;
                  GL11.glViewport(0, 0, var1, var2);
                  GL11.glDisable(2929);
                  GL11.glDisable(2884);
                  GL11.glDepthMask(false);
                  GlStateManager._enableBlend();
                  GL11.glEnable(3042);
                  GL14.glBlendFuncSeparate(770, 771, 1, 771);
                  GL11.glDisable(36281);
                  var0.handle();
                  handle(var0, "uViewport", var1, var2);
                  handle(var0, "u_LightDirection", -1.0F, -1.0F);
                  GL15.glBindBuffer(35345, animationDraw);
                  GL15.glBufferSubData(35345, 0L, target);
                  GL30.glBindBufferBase(35345, 3, animationDraw);
                  GL30.glBindVertexArray(handler);
                  GL31.glDrawArraysInstanced(4, 0, 6, animator);
                  GL30.glBindVertexArray(0);
                  GL30.glBindBufferBase(35345, 3, 0);
                  GL15.glBindBuffer(35345, 0);
                  var8 = false;
                  break label90;
               } catch (Throwable var9) {
                  pending = var9.getMessage() == null ? var9.getClass().getSimpleName() : var9.getMessage();
                  RenderDiagnostics.handle().process("ThemeShaderApply.drawNeumorphicBatch", var9);
                  var8 = false;
               } finally {
                  if (var8) {
                     GL20.glUseProgram(0);
                     OpenGlStateSnapshot.compute(var11);
                     refresh();
                  }
               }

               GL20.glUseProgram(0);
               OpenGlStateSnapshot.compute(var11);
               refresh();
               return;
            }

            GL20.glUseProgram(0);
            OpenGlStateSnapshot.compute(var11);
            refresh();
         }
      }
   }

   private static void onTick() {
      target.clear();
      short var0 = 128;
      byte var1 = 0;
      int var2 = var0 * 4;
      int var3 = var0 * 8;
      int var4 = var0 * 12;
      int var5 = var0 * 16;
      int var6 = var0 * 20;
      int var7 = var0 * 24;

      for (int var8 = 0; var8 < animator; var8++) {
         ThemeShaderApplier.ColorState var9 = source[var8];
         handle(var1 + var8 * 4, var9.instance, var9.data, var9.context, var9.config);
         handle(var2 + var8 * 4, var9.state, var9.cache, var9.output, var9.current);
         handle(var3 + var8 * 4, var9.active, var9.mode, var9.selection, var9.enabled);
         handle(var4 + var8 * 4, handle(var9.animator), process(var9.animator), compute(var9.animator), var9.pending);
         handle(var5 + var8 * 4, handle(var9.source), process(var9.source), compute(var9.source), resolve(var9.source));
         handle(var6 + var8 * 4, handle(var9.target), process(var9.target), compute(var9.target), resolve(var9.target));
         handle(var7 + var8 * 4, var9.renderer, var9.handler, 0.0F, 0.0F);
      }

      target.position(0);
      target.limit(target.capacity());
   }

   private static void handle(int var0, float var1, float var2, float var3, float var4) {
      target.put(var0, var1);
      target.put(var0 + 1, var2);
      target.put(var0 + 2, var3);
      target.put(var0 + 3, var4);
   }
   private static boolean handle(
      ShaderBuildReporter var0,
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
      ThemePaletteRegistry.ColorStop var12,
      boolean var13,
      float var14,
      ThemeShaderApplier.DataRecord var15
   ) {
      VertexArrayBuffer var16 = ThemeShaderProgramCache.handle().process();
      if (var0 != null && var16 != null && var12 != null) {
         OpenGlStateSnapshot.NetworkState var17 = OpenGlStateSnapshot.handle();
         boolean var22 = false /* VF: Semaphore variable */;

         boolean var18;
         try {
            var22 = true;
            GL11.glViewport(0, 0, var10, var11);
            GL11.glDisable(2929);
            GL11.glDisable(2884);
            GL11.glDepthMask(false);
            GlStateManager._enableBlend();
            GL11.glEnable(3042);
            GL14.glBlendFuncSeparate(770, 771, 1, 771);
            GL11.glDisable(36281);
            var0.handle();
            handle(var0, "uViewport", var10, var11);
            handle(var0, "uRect", var1, var2, var3, var4);
            handle(var0, "u_ElementRect", var5, var6, var7, var8);
            handle(var0, "u_Resolution", Math.max(1.0F, var10), Math.max(1.0F, var11));
            handle(var0, "u_Radius", Math.max(0.0F, var9));
            handle(var0, "u_ElementRadius", Math.max(0.0F, var9));
            handle(var0, "u_BaseColor", handle(var12.baseColor()), process(var12.baseColor()), compute(var12.baseColor()));
            handle(var0, "u_LightShadowColor", handle(var12.lightShadowColor()), process(var12.lightShadowColor()), compute(var12.lightShadowColor()));
            handle(var0, "u_DarkShadowColor", handle(var12.darkShadowColor()), process(var12.darkShadowColor()), compute(var12.darkShadowColor()));
            handle(var0, "u_LightShadowAlpha", resolve(var12.lightShadowColor()));
            handle(var0, "u_DarkShadowAlpha", resolve(var12.darkShadowColor()));
            handle(var0, "u_Alpha", var14);
            handle(var0, "u_Inset", var13 ? 1 : 0);
            handle(var0, "u_Distance", var15.distance());
            handle(var0, "u_Blur", var15.blur());
            handle(var0, "u_Intensity", var15.intensity());
            handle(var0, "u_ShapeType", var15.shape());
            handle(var0, "u_Shape", var15.shape());
            handle(var0, "u_LightDirection", -1.0F, -1.0F);
            var16.handle();
            var18 = true;
            var22 = false;
         } catch (Throwable var23) {
            pending = var23.getMessage() == null ? var23.getClass().getSimpleName() : var23.getMessage();
            RenderDiagnostics.handle().process("ThemeShaderApply.drawNeumorphicProgram", var23);
            throw new IllegalStateException("unreachable shader failure", var23);
         } finally {
            if (var22) {
               GL20.glUseProgram(0);
               OpenGlStateSnapshot.compute(var17);
               refresh();
            }
         }

         GL20.glUseProgram(0);
         OpenGlStateSnapshot.compute(var17);
         refresh();
         return var18;
      } else {
         return false;
      }
   }

   private static ThemeShaderApplier.DataRecord select() {
      try {
         ThemeRenderer.SecondaryCacheEntry var0 = ThemeRenderer.handle().instance;
         return handle(var0.config.compute(), var0.state.compute(), var0.cache.compute(), var0.output.compute());
      } catch (Throwable var1) {
         return new ThemeShaderApplier.DataRecord(5.5F, 18.0F, 0.72F, 1);
      }
   }

   private static int resolve(String var0) {
      if ("Вогнутая".equals(var0)) {
         return 2;
      } else {
         return "Выпуклая".equals(var0) ? 1 : 0;
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
      float var17
   ) {
      VertexArrayBuffer var18 = ThemeShaderProgramCache.handle().process();
      if (var0 != null && var1 != null && var18 != null) {
         OpenGlStateSnapshot.NetworkState var19 = OpenGlStateSnapshot.handle();

         try {
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
            GL11.glBindTexture(3553, ThemeShaderProgramCache.handle().update());
            handle(var0, "u_DiffuseMap", 0);
            handle(var0, "uViewport", var12, var13);
            handle(var0, "uRect", var3, var4, var5, var6);
            handle(var0, "u_ElementRect", var7, var8, var9, var10);
            handle(var0, "u_ElementRadius", Math.max(0.0F, var11));
            handle(var0, "u_GlobalUV", var7 / Math.max(1.0F, var12), var8 / Math.max(1.0F, var13));
            handle(var0, "u_Resolution", Math.max(1.0F, var12), Math.max(1.0F, var13));
            handle(var0, "u_Time", ThemeShaderProgramCache.handle().compute());
            handle(var0, "u_Mouse", var14 - var7, var15 - var8);
            int var20 = var16 == null ? -1 : var16.save();
            int var21 = var16 == null ? -16777216 : var16.submit();
            int var22 = var16 == null ? -15724520 : var16.apply();
            int var23 = var16 == null ? -14671832 : var16.execute();
            handle(var0, "u_AccentTop", handle(var20), process(var20), compute(var20));
            handle(var0, "u_AccentBottom", handle(var21), process(var21), compute(var21));
            handle(var0, "u_ThemeColors[0]", handle(var22), process(var22), compute(var22), resolve(var22));
            handle(var0, "u_ThemeColors[1]", handle(var23), process(var23), compute(var23), resolve(var23));
            handle(var0, "u_ThemeColors[2]", handle(var20), process(var20), compute(var20), var17);
            handle(var0, "u_ThemeColors[3]", handle(var21), process(var21), compute(var21), var17);
            handle(var0, "u_Alpha", var17);
            handle(var0, var1, var2);
            var18.handle();
            return true;
         } catch (Throwable var28) {
            RenderDiagnostics.handle().process("ThemeShaderApply.drawHudProgram", var28);
            throw new IllegalStateException("unreachable shader failure", var28);
         } finally {
            GL13.glActiveTexture(33984);
            GL11.glBindTexture(3553, 0);
            OpenGlStateSnapshot.compute(var19);
            refresh();
         }
      } else {
         return false;
      }
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

   private static void refresh() {
      GL20.glUseProgram(0);
      if (!Boolean.FALSE.equals(active)) {
         try {
            if (active == null) {
               Class var0 = Class.forName("com.mojang.blaze3d.systems.RenderSystem");
               Class var1 = Class.forName("net.minecraft.client.render.GameRenderer");
               mode = var0.getMethod("setShader", Supplier.class);
               selection = var1.getMethod("getPositionColorProgram");
               active = true;
            }

            Supplier var3 = () -> {
               try {
                  return selection.invoke(null);
               } catch (Throwable var1x) {
                  return null;
               }
            };
            mode.invoke(null, var3);
         } catch (Throwable var2) {
            active = false;
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

   private static int handle(int var0, float var1) {
      int var2 = Math.max(0, Math.min(255, Math.round(var1 * 255.0F)));
      return var0 & 16777215 | var2 << 24;
   }

   private static ThemeShaderApplier.PrimaryDataRecord handle(RoundedRectRenderer var0, MatrixStack var1, float var2, float var3, float var4, float var5) {
      float[] var6 = var0 == null ? null : var0.select().update();
      Matrix4f var7 = var1 == null ? null : new Matrix4f(var1.peek().getPositionMatrix());
      float var8 = var2;
      float var9 = var3;
      float var10 = var2 + var4;
      float var11 = var3 + var5;
      ThemeShaderApplier.SecondaryDataRecord var12 = handle(var6, var7, var8, var9);
      ThemeShaderApplier.SecondaryDataRecord var13 = handle(var6, var7, var10, var9);
      ThemeShaderApplier.SecondaryDataRecord var14 = handle(var6, var7, var10, var11);
      ThemeShaderApplier.SecondaryDataRecord var15 = handle(var6, var7, var8, var11);
      float var16 = Math.min(Math.min(var12.x, var13.x), Math.min(var14.x, var15.x));
      float var17 = Math.min(Math.min(var12.y, var13.y), Math.min(var14.y, var15.y));
      float var18 = Math.max(Math.max(var12.x, var13.x), Math.max(var14.x, var15.x));
      float var19 = Math.max(Math.max(var12.y, var13.y), Math.max(var14.y, var15.y));
      return new ThemeShaderApplier.PrimaryDataRecord(var16, var17, var18, var19);
   }

   private static ThemeShaderApplier.SecondaryDataRecord handle(float[] var0, Matrix4f var1, float var2, float var3) {
      float var4 = var0 != null && var0.length >= 6 ? var0[0] * var2 + var0[1] * var3 + var0[2] : var2;
      float var5 = var0 != null && var0.length >= 6 ? var0[3] * var2 + var0[4] * var3 + var0[5] : var3;
      if (var1 != null) {
         Vector4f var6 = var1.transform(new Vector4f(var4, var5, 0.0F, 1.0F));
         float var7 = Math.abs(var6.w) <= 1.0E-6F ? 1.0F : 1.0F / var6.w;
         var4 = var6.x * var7;
         var5 = var6.y * var7;
      }

      return new ThemeShaderApplier.SecondaryDataRecord(var4, var5);
   }

   static float handle(float var0, float var1, float var2) {
      return !Float.isFinite(var0) ? var1 : Math.max(var1, Math.min(var2, var0));
   }

   private static ThemeShaderApplier.ColorState[] render() {
      ThemeShaderApplier.ColorState[] var0 = new ThemeShaderApplier.ColorState[128];

      for (int var1 = 0; var1 < var0.length; var1++) {
         var0[var1] = new ThemeShaderApplier.ColorState();
      }

      return var0;
   }

   static final class ColorState {
      float instance;
      float data;
      float context;
      float config;
      float state;
      float cache;
      float output;
      float current;
      float active;
      float mode;
      float selection;
      float enabled;
      float renderer;
      float handler;
      int animationDraw;
      int pointEncode;
      int animator;
      int source;
      int target;
      float pending;

      void handle(
         float var1,
         float var2,
         float var3,
         float var4,
         float var5,
         float var6,
         float var7,
         float var8,
         float var9,
         float var10,
         float var11,
         float var12,
         int var13,
         int var14,
         int var15,
         int var16,
         int var17,
         int var18,
         int var19,
         float var20
      ) {
         this.instance = var1;
         this.data = var2;
         this.context = var3;
         this.config = var4;
         this.state = var5;
         this.cache = var6;
         this.output = var7;
         this.current = var8;
         this.active = var9;
         this.mode = var10;
         this.selection = var11;
         this.enabled = var12;
         this.renderer = var13;
         this.handler = var14;
         this.animationDraw = var15;
         this.pointEncode = var16;
         this.animator = var17;
         this.source = var18;
         this.target = var19;
         this.pending = var20;
      }
   }

   public record DataRecord(float distance, float blur, float intensity, int shape) {
      public DataRecord(float distance, float blur, float intensity, int shape) {
         distance = ThemeShaderApplier.handle(distance, 1.0F, 36.0F);
         blur = ThemeShaderApplier.handle(blur, 2.0F, 96.0F);
         intensity = ThemeShaderApplier.handle(intensity, 0.0F, 1.4F);
         shape = Math.max(0, Math.min(2, shape));
         this.distance = distance;
         this.blur = blur;
         this.intensity = intensity;
         this.shape = shape;
      }
   }

   record PrimaryDataRecord(float minX, float minY, float maxX, float maxY) {
   }

   record SecondaryDataRecord(float x, float y) {
   }
}
