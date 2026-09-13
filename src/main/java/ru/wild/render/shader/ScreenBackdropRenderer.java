package ru.wild.render.shader;

import com.mojang.blaze3d.opengl.GlStateManager;
import java.time.LocalTime;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.Window;
import net.minecraft.server.WorldGenerationProgressTracker;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import ru.wild.WildClient;
import ru.wild.gui.theme.ThemePalette;
import ru.wild.gui.theme.ThemePaletteRegistry;
import ru.wild.render.MainMenuBackgroundRenderer;
import ru.wild.render.OpenGlStateSnapshot;
import ru.wild.render.ScreenRenderDiagnostics;
import ru.wild.render.VertexArrayBuffer;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.texture.OffscreenRenderTarget;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RoundedRectRenderer;

public final class ScreenBackdropRenderer implements AutoCloseable {
   private static final ScreenBackdropRenderer instance = new ScreenBackdropRenderer();
   private static final String data = "assets/wild/shaders/mainmenu/menu_quad.vert";
   private static final ThemePaletteRegistry context = ThemePaletteRegistry.handle();
   private static final String[] config = resolve();
   private final ShaderFailureCache state = new ShaderFailureCache();
   private final OffscreenRenderTarget cache = new OffscreenRenderTarget();
   private VertexArrayBuffer output;
   private ShaderFailureCache.ShaderState current;
   private ShaderFailureCache.ShaderState active;
   private ShaderFailureCache.ShaderState mode;
   private ShaderFailureCache.ShaderState selection;
   private ShaderFailureCache.ShaderState enabled;
   private ShaderFailureCache.ShaderState renderer;
   private long handler;
   private long animationDraw;
   private long pointEncode;
   private float animator;
   private float source;
   private float target;
   private float pending;
   private float previous;
   private float latest;
   private boolean summary;
   private boolean matrixBlend;
   private int vectorMatch;
   private static final int itemProject = 4;
   private int responseCompute = -6357021;
   private int providerFetch = -11341636;
   private ThemePalette profileDraw = ThemePalette.AURORA;
   private boolean vectorPerform;
   private float eventAttach;
   private long serverRead;

   public static ScreenBackdropRenderer handle() {
      return instance;
   }

   public boolean handle(MinecraftClient var1, int var2, int var3, float var4) {
      return this.handle(var1, var2, var3, var4, null);
   }

   public boolean handle(MinecraftClient var1, int var2, int var3, float var4, Screen var5) {
      MainMenuBackgroundRenderer.Bounds var6 = MainMenuBackgroundRenderer.process(var1, 1, 1);
      if (var6 == null) {
         ScreenRenderDiagnostics.handle(var5, "WildScreenBackdrop", false, "invalid frame metrics");
         return false;
      }

      if (GLFW.glfwGetCurrentContext() == 0L) {
         ScreenRenderDiagnostics.handle(var5, "WildScreenBackdrop", false, "no gl context");
         return false;
      }

      Window var7 = var1.getWindow();
      if (var7 == null) {
         ScreenRenderDiagnostics.handle(var5, "WildScreenBackdrop", false, "window missing");
         return false;
      }

      long var8 = System.nanoTime();
      if (this.handler == 0L) {
         this.handler = var8;
         this.animationDraw = var8;
      }

      float var10 = Math.max(0.001F, Math.min(0.05F, (float)(var8 - this.animationDraw) / 1.0E9F));
      this.animationDraw = var8;
      float var11 = (float)(var8 - this.handler) / 1.0E9F;
      if (this.serverRead == 0L || var8 - this.serverRead >= 1000000000L) {
         this.eventAttach = LocalTime.now().toSecondOfDay() / 3600.0F;
         this.serverRead = var8;
      }

      this.compute();
      this.handle(var7, var2, var3, var10);
      OpenGlStateSnapshot.NetworkState var12 = OpenGlStateSnapshot.handle();

      try {
         this.state.handle();
         this.process();
         int var13 = var6.width();
         int var14 = var6.height();
         int var15 = OpenGlStateSnapshot.handle(GL11.glGetInteger(36006));
         int var16 = Math.max(420, Math.round(var13 * 0.88F));
         int var17 = Math.max(240, Math.round(var14 * 0.88F));
         int var18 = this.cache.resolve();
         int var19 = this.cache.update();
         this.cache.handle(var16, var17);
         if (!this.cache.apply()) {
            ScreenRenderDiagnostics.handle(var5, "WildScreenBackdrop", false, "gas target not ready");
            return false;
         }

         boolean var20 = var18 != this.cache.resolve() || var19 != this.cache.update();
         if (var20 || this.pointEncode == 0L || var8 - this.pointEncode >= 25000000L) {
            this.handle(var11);
            this.pointEncode = var8;
         }

         OpenGlStateSnapshot.handle(36160, var15);
         int var21 = GL30.glCheckFramebufferStatus(36009);
         if (var21 != 36053) {
            ScreenRenderDiagnostics.handle(var5, "WildScreenBackdrop", false, "draw framebuffer incomplete status=0x" + Integer.toHexString(var21));
            return false;
         }

         GL11.glViewport(0, 0, var13, var14);
         GL11.glDisable(2929);
         GL11.glDisable(2884);
         GL11.glDisable(3089);
         GL11.glDisable(36281);
         GL11.glColorMask(true, true, true, true);
         this.handle(var13, var14, var11, handle(var4, 0.0F, 1.0F));
         this.process(var13, var14, var11, handle(var4, 0.0F, 1.0F));
         this.vectorMatch = 0;
         if (ScreenRenderDiagnostics.handle()) {
            ScreenRenderDiagnostics.handle(var5, "WildScreenBackdrop", true, "size=" + var13 + "x" + var14);
         }

         return true;
      } catch (Throwable var26) {
         this.vectorMatch++;
         ScreenRenderDiagnostics.handle("WildScreenBackdrop", var5, "renderBackdrop failed (" + this.vectorMatch + "/4)", var26);
         if (this.vectorMatch >= 4) {
            this.vectorMatch = 0;
            this.close();
         }

         return false;
      } finally {
         GL13.glActiveTexture(33984);
         GL11.glBindTexture(3553, 0);
         GL20.glUseProgram(0);
         OpenGlStateSnapshot.compute(var12);
      }
   }

   public void handle(MinecraftClient var1, Text var2) {
      MainMenuBackgroundRenderer.Bounds var3 = MainMenuBackgroundRenderer.process(var1, 1, 1);
      if (var3 != null) {
         this.handle(var3.width(), var3.height(), var2 == null ? "Connecting" : var2.getString(), -1.0F);
      }
   }

   public void handle(MinecraftClient var1, WorldGenerationProgressTracker var2) {
      MainMenuBackgroundRenderer.Bounds var3 = MainMenuBackgroundRenderer.process(var1, 1, 1);
      if (var3 != null && var2 != null) {
         float var4 = handle(var2.getProgressPercentage() / 100.0F, 0.0F, 1.0F);
         this.handle(var3.width(), var3.height(), Math.round(var4 * 100.0F) + "%", var4);
      }
   }

   private void handle(int var1, int var2, String var3, float var4) {
      try {
         WildClient.check();
         RoundedRectRenderer var5 = WildClient.handle();
         if (var5 == null) {
            return;
         }

         var5.handle(var1, var2);
         boolean var6 = false;

         try {
            float var7 = handle((float)var1, (float)var2);
            float var8 = handle(var1 * 0.3F, 320.0F * var7, 560.0F * var7);
            float var9 = Math.max(8.0F * var7, 8.0F);
            float var10 = var1 * 0.5F - var8 * 0.5F;
            float var11 = var2 * 0.5F + 42.0F * var7;
            float var12 = var9 * 0.5F;
            float var13 = 0.5F + 0.5F * (float)Math.sin((float)(System.nanoTime() - Math.max(1L, this.handler)) / 1.0E9F * 1.2F);
            float var14 = var4 >= 0.0F ? var4 : 0.18F + 0.64F * var13;
            float var15 = Math.max(var9, var8 * handle(var14, 0.0F, 1.0F));
            int var16 = this.vectorPerform ? handle(0.12F, 0.13F, 0.15F, 0.18F) : handle(1.0F, 1.0F, 1.0F, 0.105F);
            int var17 = this.vectorPerform ? handle(0.07F, 0.08F, 0.09F, 0.88F) : handle(0.94F, 0.97F, 1.0F, 0.9F);
            int var18 = this.vectorPerform ? handle(0.22F, 0.23F, 0.24F, 0.48F) : handle(0.66F, 0.72F, 0.8F, 0.48F);
            var5.handle(var10, var11, var8, var9, var12, 18.0F * var7, 0.9F, handle(this.providerFetch, 78));
            var5.handle(var10, var11, var8, var9, var12, var16);
            var5.handle(var10, var11, var15, var9, var12, compute(this.providerFetch, this.responseCompute, var13, 0.88F));
            float var19 = 25.0F * var7;
            float var20 = RoundedRectRenderer.handle(FontRegistry.config, var3, var19).instance;
            float var21 = var11 - 22.0F * var7;
            var5.handle(FontRegistry.config, var1 * 0.5F - var20 * 0.5F, var21, var19, var3, var17);
            if (var4 >= 0.0F) {
               String var22 = "Loading world";
               float var23 = 14.0F * var7;
               float var24 = RoundedRectRenderer.handle(FontRegistry.instance, var22, var23).instance;
               var5.handle(FontRegistry.instance, var1 * 0.5F - var24 * 0.5F, var11 + 34.0F * var7, var23, var22, var18);
            }

            var5.process();
            var6 = true;
         } finally {
            if (!var6) {
               var5.handle();
            }
         }
      } catch (Throwable var29) {
      }
   }

   private void handle(float var1) {
      this.cache.handle();
      GL11.glDisable(3042);
      GL11.glDisable(2929);
      GL11.glDisable(2884);
      ShaderFailureCache.ShaderState var2 = this.profileDraw == ThemePalette.MIDNIGHT_AZURE
         ? this.selection
         : (this.profileDraw == ThemePalette.VERNAL_SOLSTICE ? this.mode : (this.profileDraw == ThemePalette.SAKURA_BREEZE ? this.active : this.current));
      if (this.profileDraw == ThemePalette.SAKURA_BREEZE || this.profileDraw == ThemePalette.VERNAL_SOLSTICE || this.profileDraw == ThemePalette.MIDNIGHT_AZURE
         )
       {
         GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
         GL11.glClear(16384);
         GlStateManager._enableBlend();
         GlStateManager._blendFuncSeparate(770, 771, 1, 771);
         GL11.glEnable(3042);
         GL14.glBlendFuncSeparate(770, 771, 1, 771);
      }

      var2.handle();
      this.handle(var2, this.cache.resolve(), this.cache.update(), 0.0F, 0.0F, this.cache.resolve(), this.cache.update());
      var2.handle("uTime", var1);
      var2.handle("uResolution", this.cache.resolve(), this.cache.update());
      var2.handle("uMouse", this.target / Math.max(1.0F, this.cache.resolve()), this.pending / Math.max(1.0F, this.cache.update()));
      var2.handle("uMouseVelocity", this.previous, this.latest);
      var2.handle("uAccentTop", handle(this.responseCompute), process(this.responseCompute), compute(this.responseCompute));
      var2.handle("uAccentBottom", handle(this.providerFetch), process(this.providerFetch), compute(this.providerFetch));
      var2.handle("uActivity", 0.54F);
      var2.handle("uAlpha", 1.0F);
      var2.handle("uLightMode", this.vectorPerform ? 1.0F : 0.0F);

      for (int var3 = 0; var3 < 14; var3++) {
         var2.handle(config[var3], 0.0F, 0.0F, 100.0F, 0.0F);
      }

      this.output.handle();
   }

   private void handle(int var1, int var2, float var3, float var4) {
      GL11.glDisable(3042);
      this.enabled.handle();
      this.handle(this.enabled, var1, var2, 0.0F, 0.0F, var1, var2);
      this.enabled.handle("uTexture", 0);
      this.enabled.handle("uTextureSize", this.cache.resolve(), this.cache.update());
      this.enabled.handle("uParallax", (this.target / Math.max(1.0F, var1) - 0.5F) * 0.01F, (this.pending / Math.max(1.0F, var2) - 0.5F) * 0.008F);
      this.enabled.handle("uTime", var3);
      this.enabled.handle("uEntry", var4);
      this.enabled.handle("uClickFlash", 0.0F);
      this.enabled.handle("uLightMode", this.vectorPerform ? 1.0F : 0.0F);
      this.enabled.handle("uSakura", this.profileDraw == ThemePalette.SAKURA_BREEZE ? 1.0F : 0.0F);
      this.enabled.handle("uVernal", this.profileDraw == ThemePalette.VERNAL_SOLSTICE ? 1.0F : 0.0F);
      this.enabled.handle("uHour", this.eventAttach);
      GL13.glActiveTexture(33984);
      GL11.glBindTexture(3553, this.cache.compute());
      this.output.handle();
   }

   private void process(int var1, int var2, float var3, float var4) {
      GL11.glEnable(3042);
      GL14.glBlendFuncSeparate(770, 771, 1, 771);
      this.renderer.handle();
      this.handle(this.renderer, var1, var2, 0.0F, 0.0F, var1, var2);
      this.renderer.handle("uBackground", 0);
      this.renderer.handle("uTextureSize", this.cache.resolve(), this.cache.update());
      this.renderer.handle("uTime", var3);
      this.renderer.handle("uAlpha", var4);
      this.renderer.handle("uAccentTop", handle(this.responseCompute), process(this.responseCompute), compute(this.responseCompute));
      this.renderer.handle("uAccentBottom", handle(this.providerFetch), process(this.providerFetch), compute(this.providerFetch));
      this.renderer.handle("uLightMode", this.vectorPerform ? 1.0F : 0.0F);
      GL13.glActiveTexture(33984);
      GL11.glBindTexture(3553, this.cache.compute());
      this.output.handle();
   }

   private void process() {
      if (!this.matrixBlend) {
         this.output = new VertexArrayBuffer();
         this.current = this.state
            .handle("screen_liquid_neon_gas", "assets/wild/shaders/mainmenu/menu_quad.vert", "assets/wild/shaders/mainmenu/menu_aurora.frag");
         this.active = this.state
            .handle("screen_sakura_breeze", "assets/wild/shaders/mainmenu/menu_quad.vert", "assets/wild/shaders/mainmenu/sakura_breeze.frag");
         this.mode = this.state
            .handle("screen_vernal_solstice", "assets/wild/shaders/mainmenu/menu_quad.vert", "assets/wild/shaders/mainmenu/vernal_solstice.frag");
         this.selection = this.state
            .handle("screen_midnight_azure", "assets/wild/shaders/mainmenu/menu_quad.vert", "assets/wild/shaders/mainmenu/midnight_azure.frag");
         this.enabled = this.state
            .handle("screen_composite", "assets/wild/shaders/mainmenu/menu_quad.vert", "assets/wild/shaders/mainmenu/menu_composite.frag");
         this.renderer = this.state
            .handle("screen_mica_wash", "assets/wild/shaders/mainmenu/menu_quad.vert", "assets/wild/shaders/mainmenu/menu_mica_wash.frag");
         this.matrixBlend = true;
      }
   }

   private void handle(ShaderFailureCache.ShaderState var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      var1.handle("uViewport", var2, var3);
      var1.handle("uRect", var4, var5, var6, var7);
   }

   private void compute() {
      ThemePalette var1 = WildClient.instance != null && WildClient.instance.selection != null ? WildClient.instance.selection.process() : ThemePalette.AURORA;
      this.profileDraw = var1;
      this.vectorPerform = context.compute(var1);
      this.responseCompute = context.resolve(var1);
      this.providerFetch = context.update(var1);
   }

   private void handle(Window var1, int var2, int var3, float var4) {
      float var5 = (float)(var2 * var1.getFramebufferWidth() / Math.max(1.0, var1.getScaledWidth()));
      float var6 = (float)(var3 * var1.getFramebufferHeight() / Math.max(1.0, var1.getScaledHeight()));
      if (!this.summary) {
         this.animator = this.target = var5;
         this.source = this.pending = var6;
         this.previous = 0.0F;
         this.latest = 0.0F;
         this.summary = true;
      } else {
         this.animator = var5;
         this.source = var6;
         float var7 = this.target;
         float var8 = this.pending;
         float var9 = process(this.animator - this.target, this.source - this.pending);
         float var10 = (1.0F - (float)Math.pow(3.5E-5F, var4)) * (0.72F + handle(var9 / 520.0F, 0.0F, 0.42F));
         this.target = this.target + (this.animator - this.target) * handle(var10, 0.05F, 0.26F);
         this.pending = this.pending + (this.source - this.pending) * handle(var10, 0.05F, 0.26F);
         float var11 = handle((this.target - var7) / Math.max(1.0F, var1.getFramebufferWidth()) / var4, -1.8F, 1.8F);
         float var12 = handle((this.pending - var8) / Math.max(1.0F, var1.getFramebufferHeight()) / var4, -1.8F, 1.8F);
         float var13 = 1.0F - (float)Math.pow(0.0025F, var4);
         this.previous = this.previous + (var11 - this.previous) * var13;
         this.latest = this.latest + (var12 - this.latest) * var13;
      }
   }

   @Override
   public void close() {
      this.cache.close();
      if (this.output != null) {
         this.output.close();
         this.output = null;
      }

      this.state.close();
      this.current = null;
      this.active = null;
      this.mode = null;
      this.selection = null;
      this.enabled = null;
      this.renderer = null;
      this.matrixBlend = false;
      this.pointEncode = 0L;
      this.animationDraw = 0L;
      this.handler = 0L;
      this.summary = false;
   }

   private static float handle(float var0, float var1) {
      return handle(Math.min(var0 / 1920.0F, var1 / 1080.0F) * 1.16F, 0.72F, 1.38F);
   }

   private static float process(float var0, float var1) {
      return (float)Math.sqrt(var0 * var0 + var1 * var1);
   }

   private static float handle(float var0, float var1, float var2) {
      return Math.max(var1, Math.min(var2, var0));
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

   private static int handle(float var0, float var1, float var2, float var3) {
      int var4 = Math.round(handle(var0, 0.0F, 1.0F) * 255.0F);
      int var5 = Math.round(handle(var1, 0.0F, 1.0F) * 255.0F);
      int var6 = Math.round(handle(var2, 0.0F, 1.0F) * 255.0F);
      int var7 = Math.round(handle(var3, 0.0F, 1.0F) * 255.0F);
      return var7 << 24 | var4 << 16 | var5 << 8 | var6;
   }

   private static int handle(int var0, int var1) {
      int var2 = Math.max(0, Math.min(255, var1));
      return var0 & 16777215 | var2 << 24;
   }

   private static int compute(int var0, int var1, float var2, float var3) {
      float var4 = handle(var2, 0.0F, 1.0F);
      int var5 = PackedColor.resolve(var0, var1, var4);
      int var6 = Math.round(handle(var3, 0.0F, 1.0F) * 255.0F);
      return var6 << 24 | var5;
   }

   private static String[] resolve() {
      String[] var0 = new String[14];

      for (int var1 = 0; var1 < var0.length; var1++) {
         var0[var1] = "uTrail[" + var1 + "]";
      }

      return var0;
   }
}
