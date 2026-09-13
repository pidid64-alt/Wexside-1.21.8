package ru.wild.render.shader;

import com.mojang.blaze3d.opengl.GlStateManager;
import java.util.HashMap;
import java.util.Map;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import ru.wild.config.FoundryStorage;
import ru.wild.gui.theme.LivePreviewRenderer;
import ru.wild.gui.theme.SavedThemePreset;
import ru.wild.gui.theme.ThemeColors;
import ru.wild.gui.theme.ThemeKeys;
import ru.wild.render.OpenGlStateSnapshot;
import ru.wild.render.RenderDiagnostics;
import ru.wild.render.VertexArrayBuffer;
import ru.wild.util.io.ResourceReader;

public final class DiffuseQuadShader {
   private static final DiffuseQuadShader instance = new DiffuseQuadShader();
   private final Map<String, DiffuseQuadShader.State> data = new HashMap<>();
   private ShaderGraphCompiler context;
   private ShaderNodeRegistry config;

   private DiffuseQuadShader() {
   }

   public static DiffuseQuadShader handle() {
      return instance;
   }

   public synchronized void handle(ShaderGraphCompiler var1, ShaderNodeRegistry var2) {
      this.context = var1;
      this.config = var2;
   }

   public synchronized boolean handle(
      String var1, float var2, float var3, float var4, float var5, int var6, int var7, float var8, float var9, ThemeColors var10, float var11
   ) {
      return this.handle(var1, null, var2, var3, var4, var5, var2, var3, var4, var5, 0.0F, var6, var7, var8, var9, var10, var11);
   }

   public synchronized boolean handle(
      String var1,
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
      return this.handle(var1, LivePreviewRenderer.HUD, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16);
   }

   private boolean handle(
      String var1,
      LivePreviewRenderer var2,
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
      if (var1 == null || var1.isBlank() || this.context == null || this.config == null) {
         return false;
      }

      if (!(var5 <= 1.0F) && !(var6 <= 1.0F) && !(var9 <= 1.0F) && !(var10 <= 1.0F) && !(var17 <= 0.001F)) {
         DiffuseQuadShader.State var18 = this.handle(var1, var2);
         if (var18 != null && var18.context != null) {
            VertexArrayBuffer var19 = ThemeShaderProgramCache.handle().process();
            if (var19 == null) {
               return false;
            }

            OpenGlStateSnapshot.NetworkState var20 = OpenGlStateSnapshot.handle();

            try {
               GL11.glViewport(0, 0, var12, var13);
               GL11.glDisable(2929);
               GL11.glDisable(2884);
               GlStateManager._enableBlend();
               GL11.glEnable(3042);
               GL14.glBlendFuncSeparate(770, 771, 1, 771);
               GL11.glDisable(36281);
               var18.context.handle();
               GL13.glActiveTexture(33984);
               GL11.glBindTexture(3553, ThemeShaderProgramCache.handle().update());
               handle(var18.context, "u_DiffuseMap", 0);
               handle(var18.context, "uViewport", var12, var13);
               handle(var18.context, "uRect", var3, var4, var5, var6);
               handle(var18.context, "u_ElementRect", var7, var8, var9, var10);
               handle(var18.context, "u_ElementRadius", Math.max(0.0F, var11));
               handle(var18.context, "u_Time", ThemeShaderProgramCache.handle().compute());
               handle(var18.context, "u_Resolution", Math.max(1.0F, var12), Math.max(1.0F, var13));
               handle(var18.context, "u_GlobalUV", var7 / Math.max(1.0F, var12), var8 / Math.max(1.0F, var13));
               handle(var18.context, "u_Mouse", var14 - var7, var15 - var8);
               int var21 = var16 == null ? -1 : var16.save();
               int var22 = var16 == null ? -16777216 : var16.submit();
               int var23 = var16 == null ? -15724520 : var16.apply();
               int var24 = var16 == null ? -14671832 : var16.execute();
               handle(var18.context, "u_AccentTop", handle(var21), process(var21), compute(var21));
               handle(var18.context, "u_AccentBottom", handle(var22), process(var22), compute(var22));
               handle(var18.context, "u_ThemeColors[0]", handle(var23), process(var23), compute(var23), resolve(var23));
               handle(var18.context, "u_ThemeColors[1]", handle(var24), process(var24), compute(var24), resolve(var24));
               handle(var18.context, "u_ThemeColors[2]", handle(var21), process(var21), compute(var21), var17);
               handle(var18.context, "u_ThemeColors[3]", handle(var22), process(var22), compute(var22), var17);
               handle(var18.context, "u_Alpha", var17);
               handle(var18.context, var18.data);
               var19.handle();
               return true;
            } catch (Throwable var29) {
               RenderDiagnostics.handle().process("NamedThemeCache.draw:" + var1, var29);
               throw new IllegalStateException("unreachable shader failure", var29);
            } finally {
               GL13.glActiveTexture(33984);
               GL11.glBindTexture(3553, 0);
               GL20.glUseProgram(0);
               OpenGlStateSnapshot.compute(var20);
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public synchronized void handle(String var1) {
      if (var1 != null) {
         String var2 = var1.trim();
         this.data.entrySet().removeIf(var1x -> {
            String var2x = var1x.getKey();
            boolean var3x = var2x.equals(var2) || var2x.startsWith(var2 + "#");
            if (var3x && var1x.getValue() != null && var1x.getValue().context != null) {
               var1x.getValue().context.process();
               var1x.getValue().context = null;
            }

            return var3x;
         });
         SavedThemePreset var3 = FoundryStorage.handle()
            .process()
            .stream()
            .filter(var1x -> var2.equals(var1x.process()) || var2.equals(var1x.handle()))
            .findFirst()
            .orElse(null);
         if (var3 != null && !var3.handle().equals(var2)) {
            String var4 = var3.handle();
            this.data.entrySet().removeIf(var1x -> {
               String var2x = var1x.getKey();
               boolean var3x = var2x.equals(var4) || var2x.startsWith(var4 + "#");
               if (var3x && var1x.getValue() != null && var1x.getValue().context != null) {
                  var1x.getValue().context.process();
                  var1x.getValue().context = null;
               }

               return var3x;
            });
         }
      }
   }

   public synchronized void process() {
      for (DiffuseQuadShader.State var2 : this.data.values()) {
         if (var2.context != null) {
            var2.context.process();
            var2.context = null;
         }
      }

      this.data.clear();
   }

   private DiffuseQuadShader.State handle(String var1, LivePreviewRenderer var2) {
      SavedThemePreset var3 = FoundryStorage.handle()
         .process()
         .stream()
         .filter(var1x -> var1.equals(var1x.handle()) || var1.equals(var1x.process()))
         .findFirst()
         .orElse(null);
      if (var3 == null) {
         return null;
      }

      String var4 = var2 == null ? var3.handle() : var3.handle() + "#" + var2.handle();
      DiffuseQuadShader.State var5 = this.data.get(var4);
      String var6 = var2 == null ? var3.resolve() : var3.resolve() + "#" + var2.handle();
      if (var5 != null && var5.instance != null && var5.instance.equals(var6) && var5.context != null) {
         return var5;
      }

      try {
         ShaderGraph var7 = ThemeKeys.handle(var3.resolve(), this.config);
         if (var2 != null) {
            var7.handle(var2.handle());
         }

         ShaderBuildResult var8 = this.context.handle(var7);
         if (var5 != null && var5.context != null) {
            var5.context.process();
            var5.context = null;
         }

         if (var5 == null) {
            var5 = new DiffuseQuadShader.State();
            this.data.put(var4, var5);
         }

         var5.instance = var6;
         var5.data = var8;
         var5.context = new ShaderBuildReporter(ResourceReader.handle("assets/wild/shaders/mainmenu/menu_quad.vert"), var8.fragmentSource());
         var5.config = var8.error();
         return var5;
      } catch (Throwable var9) {
         if (var5 == null) {
            var5 = new DiffuseQuadShader.State();
            this.data.put(var3.handle(), var5);
         }

         var5.config = var9.getMessage() == null ? var9.getClass().getSimpleName() : var9.getMessage();
         var5.context = null;
         var5.data = null;
         RenderDiagnostics.handle().process("NamedThemeCache.compile:" + var3.handle(), var9);
         throw new IllegalStateException("unreachable shader failure", var9);
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

   private static void handle(ShaderBuildReporter var0, ShaderBuildResult var1) {
      if (var0 != null && var1 != null && !var1.exposedUniforms().isEmpty()) {
         for (ShaderParameter var3 : var1.exposedUniforms()) {
            float[] var4 = var3.defaults();
            if (var3.kind() == ShaderParameter.Mode.FLOAT) {
               handle(var0, var3.uniformName(), var4[0]);
            } else {
               float var5 = var4.length > 0 ? var4[0] : 0.0F;
               float var6 = var4.length > 1 ? var4[1] : 0.0F;
               float var7 = var4.length > 2 ? var4[2] : 0.0F;
               float var8 = var4.length > 3 ? var4[3] : 1.0F;
               handle(var0, var3.uniformName(), var5, var6, var7, var8);
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

   static final class State {
      String instance = "";
      ShaderBuildResult data;
      ShaderBuildReporter context;
      String config = "";
   }
}
