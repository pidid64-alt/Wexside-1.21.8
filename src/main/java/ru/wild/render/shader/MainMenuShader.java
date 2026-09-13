package ru.wild.render.shader;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import ru.wild.WildClient;
import ru.wild.gui.theme.ThemePalette;
import ru.wild.gui.theme.ThemePaletteRegistry;
import ru.wild.render.OpenGlStateSnapshot;
import ru.wild.render.VertexArrayBuffer;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.texture.OffscreenRenderTarget;
import ru.wild.util.render.PackedColor;
import ru.wild.util.render.RoundedRectRenderer;

public final class MainMenuShader implements AutoCloseable {
   private static final MainMenuShader instance = new MainMenuShader();
   private static final int data = 14;
   private static final String context = "assets/wild/shaders/mainmenu/menu_quad.vert";
   private static final ThemePaletteRegistry config = ThemePaletteRegistry.handle();
   private static final String[] state = prepare();
   private final ShaderFailureCache cache = new ShaderFailureCache();
   private final OffscreenRenderTarget output = new OffscreenRenderTarget();
   private VertexArrayBuffer current;
   private ShaderFailureCache.ShaderState active;
   private ShaderFailureCache.ShaderState mode;
   private ShaderFailureCache.ShaderState selection;
   private ShaderFailureCache.ShaderState enabled;
   private long renderer;
   private long handler;
   private long animationDraw;
   private float pointEncode;
   private float animator;
   private float source;
   private float target;
   private float pending;
   private float previous;
   private boolean latest;
   private int summary = -6357021;
   private int matrixBlend = -11341636;
   private ThemePalette vectorMatch = ThemePalette.AURORA;
   private boolean itemProject;
   private boolean responseCompute;
   private boolean providerFetch;
   private float profileDraw;
   private float vectorPerform = 1.0F;
   private boolean eventAttach;
   private long serverRead;

   public static MainMenuShader handle() {
      return instance;
   }

   public void handle(float var1, float var2) {
      this.profileDraw = handle(var1, 0.0F, 1.0F);
      this.vectorPerform = handle(var2, 0.0F, 1.0F);
   }

   public boolean process() {
      if (this.providerFetch && this.update()) {
         this.providerFetch = false;
      }

      return this.providerFetch;
   }

   public boolean handle(MinecraftClient var1, int var2, int var3) {
      return this.handle(var1, var2, var3, this.profileDraw, this.vectorPerform);
   }

   public void compute() {
      this.eventAttach = true;
      this.serverRead = System.nanoTime() + 650000000L;
   }

   public void resolve() {
      if (this.eventAttach && System.nanoTime() >= this.serverRead) {
         this.close();
      }
   }
   public boolean handle(MinecraftClient var1, int var2, int var3, float var4, float var5) {
      if (this.providerFetch) {
         if (!this.update()) {
            return false;
         }

         this.providerFetch = false;
      }

      if (var1 != null && var1.getWindow() != null) {
         Window var6 = var1.getWindow();
         if (!var6.hasZeroWidthOrHeight() && var6.getFramebufferWidth() > 0 && var6.getFramebufferHeight() > 0) {
            try {
               long var7 = System.nanoTime();
               if (this.renderer == 0L) {
                  this.renderer = var7;
                  this.handler = var7;
               }

               float var9 = Math.max(0.001F, Math.min(0.05F, (float)(var7 - this.handler) / 1.0E9F));
               this.handler = var7;
               float var10 = (float)(var7 - this.renderer) / 1.0E9F;
               var4 = handle(var4, 0.0F, 1.0F);
               var5 = handle(var5, 0.0F, 1.0F);
               this.eventAttach = false;
               this.serverRead = 0L;
               this.apply();
               this.handle(var6, var2, var3, var9);
               OpenGlStateSnapshot.NetworkState var11 = OpenGlStateSnapshot.handle();
               boolean var15 = false /* VF: Semaphore variable */;

               try {
                  var15 = true;
                  this.handle(var6.getFramebufferWidth(), var6.getFramebufferHeight(), var10, var4, var5);
                  this.resolve(var6.getFramebufferWidth(), var6.getFramebufferHeight(), var10, var4, var5);
                  var15 = false;
               } finally {
                  if (var15) {
                     GL13.glActiveTexture(33984);
                     GL11.glBindTexture(3553, 0);
                     GL20.glUseProgram(0);
                     OpenGlStateSnapshot.compute(var11);
                  }
               }

               GL13.glActiveTexture(33984);
               GL11.glBindTexture(3553, 0);
               GL20.glUseProgram(0);
               OpenGlStateSnapshot.compute(var11);
               return true;
            } catch (Throwable var17) {
               this.providerFetch = true;
               this.close();
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean update() {
      if (GLFW.glfwGetCurrentContext() == 0L) {
         return false;
      } else {
         MinecraftClient var1 = MinecraftClient.getInstance();
         if (var1 != null && var1.getWindow() != null) {
            Window var2 = var1.getWindow();
            return !var2.hasZeroWidthOrHeight() && var2.getFramebufferWidth() > 0 && var2.getFramebufferHeight() > 0;
         } else {
            return false;
         }
      }
   }

   private void handle(int var1, int var2, float var3, float var4, float var5) {
      this.cache.handle();
      this.execute();
      float var6 = 0.92F;
      int var7 = Math.max(420, Math.round(var1 * var6));
      int var8 = Math.max(240, Math.round(var2 * var6));
      int var9 = this.output.resolve();
      int var10 = this.output.update();
      int var11 = OpenGlStateSnapshot.handle(GL11.glGetInteger(36006));
      this.output.handle(var7, var8);
      boolean var12 = var9 != this.output.resolve() || var10 != this.output.update();
      long var13 = System.nanoTime();
      if (var12 || this.animationDraw == 0L || var13 - this.animationDraw >= 16666667L) {
         this.process(var1, var2, var3, var4, var5);
         this.animationDraw = var13;
      }

      OpenGlStateSnapshot.handle(36160, var11);
      GL11.glViewport(0, 0, var1, var2);
      GL11.glDisable(2929);
      GL11.glDisable(2884);
      GL11.glDisable(3089);
      GL11.glDisable(36281);
      GL11.glColorMask(true, true, true, true);
      this.handle(var1, var2, var3, var5);
      this.compute(var1, var2, var3, var4, var5);
   }

   private void process(int var1, int var2, float var3, float var4, float var5) {
      if (this.output.apply()) {
         this.output.handle();
         GL11.glDisable(3042);
         GL11.glDisable(2929);
         GL11.glDisable(2884);
         ShaderFailureCache.ShaderState var6 = this.vectorMatch == ThemePalette.SAKURA_BREEZE ? this.mode : this.active;
         if (this.vectorMatch == ThemePalette.SAKURA_BREEZE) {
            GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            GL11.glClear(16384);
            GL11.glEnable(3042);
            GL14.glBlendFuncSeparate(770, 771, 1, 771);
         }

         var6.handle();
         this.handle(var6, this.output.resolve(), this.output.update(), 0.0F, 0.0F, this.output.resolve(), this.output.update());
         var6.handle("uTime", var3);
         var6.handle("uResolution", this.output.resolve(), this.output.update());
         var6.handle("uMouse", this.handle(var1), this.process(var2));
         var6.handle("uMouseVelocity", this.pending, this.previous);
         var6.handle("uAccentTop", update(this.summary), apply(this.summary), execute(this.summary));
         var6.handle("uAccentBottom", update(this.matrixBlend), apply(this.matrixBlend), execute(this.matrixBlend));
         var6.handle("uActivity", handle(0.36F + var4 * 0.42F, 0.0F, 1.0F));
         var6.handle("uAlpha", var5);
         var6.handle("uLightMode", this.itemProject ? 1.0F : 0.0F);
         this.handle(var6);
         this.current.handle();
      }
   }

   private void handle(int var1, int var2, float var3, float var4) {
      if (this.output.apply()) {
         GL11.glDisable(3042);
         this.selection.handle();
         this.handle(this.selection, var1, var2, 0.0F, 0.0F, var1, var2);
         this.selection.handle("uTexture", 0);
         this.selection.handle("uTextureSize", this.output.resolve(), this.output.update());
         this.selection.handle("uParallax", this.compute(var1) * 0.0012F, this.resolve(var2) * 0.001F);
         this.selection.handle("uTime", var3);
         this.selection.handle("uEntry", var4);
         this.selection.handle("uClickFlash", 0.0F);
         this.selection.handle("uLightMode", this.itemProject ? 1.0F : 0.0F);
         this.selection.handle("uSakura", this.vectorMatch == ThemePalette.SAKURA_BREEZE ? 1.0F : 0.0F);
         GL13.glActiveTexture(33984);
         GL11.glBindTexture(3553, this.output.compute());
         this.current.handle();
      }
   }

   private void compute(int var1, int var2, float var3, float var4, float var5) {
      GL11.glEnable(3042);
      if (this.itemProject) {
         GL14.glBlendFuncSeparate(770, 771, 1, 771);
      } else {
         GL14.glBlendFuncSeparate(770, 1, 1, 1);
      }

      this.enabled.handle();
      this.handle(this.enabled, var1, var2, 0.0F, 0.0F, var1, var2);
      this.enabled.handle("uTime", var3);
      this.enabled.handle("uResolution", var1, var2);
      this.enabled.handle("uMouse", this.handle(var1), this.process(var2));
      this.enabled.handle("uParallax", this.compute(var1), this.resolve(var2));
      this.enabled.handle("uAccentTop", update(this.summary), apply(this.summary), execute(this.summary));
      this.enabled.handle("uAccentBottom", update(this.matrixBlend), apply(this.matrixBlend), execute(this.matrixBlend));
      this.enabled.handle("uEntry", handle(var5 * (0.55F + var4 * 0.45F), 0.0F, 1.0F));
      this.enabled.handle("uLightMode", this.itemProject ? 1.0F : 0.0F);
      this.handle(this.enabled);
      this.current.handle();
      GL14.glBlendFuncSeparate(770, 771, 1, 771);
   }

   private void resolve(int var1, int var2, float var3, float var4, float var5) {
      if (!(var5 <= 0.01F)) {
         try {
            WildClient.check();
            RoundedRectRenderer var6 = WildClient.handle();
            if (var6 == null) {
               return;
            }

            var6.handle(var1, var2);
            boolean var7 = false;

            try {
               float var8 = process(var1, var2);
               float var9 = var1 * 0.5F;
               float var10 = var2 * 0.5F - 58.0F * var8;
               float var11 = handle(Math.min(var1, var2) * 0.112F, 82.0F * var8, 132.0F * var8);
               float var12 = 0.5F + 0.5F * (float)Math.sin(var3 * 1.08F);
               this.handle(var6, var9, var10, var11, var12, var5);
               this.handle(var6, var1, var2, var4, var5, var8, var3);
               var6.process();
               var7 = true;
            } finally {
               if (!var7) {
                  var6.handle();
               }
            }
         } catch (Throwable var17) {
         }
      }
   }

   private void handle(RoundedRectRenderer var1, float var2, float var3, float var4, float var5, float var6) {
      float var7 = var4 * 0.98F;
      float var8 = var7 * (1.08F + var5 * 0.035F);
      float var9 = RoundedRectRenderer.handle(FontRegistry.current, "w", var7).instance;
      float var10 = RoundedRectRenderer.handle(FontRegistry.current, "w", var8).instance;
      float var11 = var3 + var4 * 0.148F;
      int var12 = this.itemProject
         ? (this.vectorMatch == ThemePalette.VERNAL_SOLSTICE ? handle(0.0196F, 0.0667F, 0.0196F, var6) : handle(0.1F, 0.1F, 0.1F, var6))
         : handle(1.0F, 1.0F, 1.0F, var6);
      var1.handle(FontRegistry.current, var2 - var10 * 0.5F, var11 + var4 * 0.002F, var8, "w", process(this.matrixBlend, this.summary, var5, 0.24F * var6));
      var1.handle(FontRegistry.current, var2 - var9 * 0.5F, var11, var7, "w", var12);
   }

   private void handle(RoundedRectRenderer var1, int var2, int var3, float var4, float var5, float var6, float var7) {
      float var8 = handle(var2 * 0.26F, 292.0F * var6, 520.0F * var6);
      float var9 = Math.max(8.0F * var6, 8.0F);
      float var10 = var2 * 0.5F - var8 * 0.5F;
      float var11 = var3 * 0.5F + 92.0F * var6;
      float var12 = var9 * 0.5F;
      float var13 = Math.max(var9, var8 * handle(var4, 0.0F, 1.0F));
      int var14 = this.itemProject ? handle(0.18F, 0.2F, 0.22F, 0.16F * var5) : handle(1.0F, 1.0F, 1.0F, 0.105F * var5);
      int var15 = this.itemProject ? handle(0.1F, 0.11F, 0.12F, 0.16F * var5) : handle(1.0F, 1.0F, 1.0F, 0.15F * var5);
      var1.handle(var10, var11, var8, var9, var12, 18.0F * var6, 0.9F, handle(this.matrixBlend, Math.round(70.0F * var5)));
      var1.handle(var10, var11, var8, var9, var12, var14);
      var1.handle(var10, var11, var13, var9, var12, process(this.matrixBlend, this.summary, 0.5F + 0.5F * (float)Math.sin(var7 * 1.15F), 0.86F * var5));
      float var16 = Math.max(46.0F * var6, var8 * 0.18F);
      float var17 = var10 + (var8 + var16) * handle(var4, 0.0F, 1.0F) - var16;
      float var18 = Math.max(var10, var17);
      float var19 = Math.min(var10 + var13, var17 + var16) - var18;
      if (var19 > 0.5F) {
         var1.handle(var18, var11 + var9 * 0.16F, var19, var9 * 0.25F, var9 * 0.125F, handle(1.0F, 1.0F, 1.0F, 0.2F * var5));
      }

      var1.handle(var10, var11, var8, 1.0F * var6, var12, var15);
      String var20 = Math.round(handle(var4, 0.0F, 1.0F) * 100.0F) + "%";
      float var21 = 25.0F * var6;
      var1.handle(
         FontRegistry.instance,
         var2 * 0.5F,
         var11 + 30.0F * var6,
         var21,
         var20,
         this.itemProject ? handle(0.12F, 0.13F, 0.14F, 0.52F * var5) : handle(0.88F, 0.92F, 0.96F, 0.54F * var5),
         "c"
      );
   }

   private void apply() {
      ThemePalette var1 = WildClient.instance != null && WildClient.instance.selection != null ? WildClient.instance.selection.process() : ThemePalette.AURORA;
      this.vectorMatch = var1;
      this.itemProject = config.compute(var1);
      this.summary = config.resolve(var1);
      this.matrixBlend = config.update(var1);
   }

   private void handle(Window var1, int var2, int var3, float var4) {
      float var5 = (float)(var2 * var1.getFramebufferWidth() / Math.max(1.0, var1.getScaledWidth()));
      float var6 = (float)(var3 * var1.getFramebufferHeight() / Math.max(1.0, var1.getScaledHeight()));
      if (!this.latest) {
         this.pointEncode = this.source = var5;
         this.animator = this.target = var6;
         this.pending = 0.0F;
         this.previous = 0.0F;
         this.latest = true;
      } else {
         this.pointEncode = var5;
         this.animator = var6;
         float var7 = this.source;
         float var8 = this.target;
         float var9 = compute(this.pointEncode - this.source, this.animator - this.target);
         float var10 = (1.0F - (float)Math.pow(3.5E-5F, var4)) * (0.72F + handle(var9 / 520.0F, 0.0F, 0.42F));
         this.source = this.source + (this.pointEncode - this.source) * handle(var10, 0.05F, 0.26F);
         this.target = this.target + (this.animator - this.target) * handle(var10, 0.05F, 0.26F);
         float var11 = handle((this.source - var7) / Math.max(1.0F, var1.getFramebufferWidth()) / var4, -1.8F, 1.8F);
         float var12 = handle((this.target - var8) / Math.max(1.0F, var1.getFramebufferHeight()) / var4, -1.8F, 1.8F);
         float var13 = 1.0F - (float)Math.pow(0.0025F, var4);
         this.pending = this.pending + (var11 - this.pending) * var13;
         this.previous = this.previous + (var12 - this.previous) * var13;
      }
   }

   private float handle(int var1) {
      return this.source / Math.max(1.0F, var1);
   }

   private float process(int var1) {
      return this.target / Math.max(1.0F, var1);
   }

   private float compute(int var1) {
      return (this.handle(var1) - 0.5F) * 10.0F;
   }

   private float resolve(int var1) {
      return (this.process(var1) - 0.5F) * 8.0F;
   }

   private void execute() {
      if (!this.responseCompute) {
         this.current = new VertexArrayBuffer();
         this.active = this.cache
            .handle("loading_liquid_neon_gas", "assets/wild/shaders/mainmenu/menu_quad.vert", "assets/wild/shaders/mainmenu/menu_aurora.frag");
         this.mode = this.cache
            .handle("loading_sakura_breeze", "assets/wild/shaders/mainmenu/menu_quad.vert", "assets/wild/shaders/mainmenu/sakura_breeze.frag");
         this.selection = this.cache
            .handle("loading_composite", "assets/wild/shaders/mainmenu/menu_quad.vert", "assets/wild/shaders/mainmenu/menu_composite.frag");
         this.enabled = this.cache
            .handle("loading_particles", "assets/wild/shaders/mainmenu/menu_quad.vert", "assets/wild/shaders/mainmenu/menu_particles.frag");
         this.responseCompute = true;
      }
   }

   private void handle(ShaderFailureCache.ShaderState var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      var1.handle("uViewport", var2, var3);
      var1.handle("uRect", var4, var5, var6, var7);
   }

   private void handle(ShaderFailureCache.ShaderState var1) {
      for (int var2 = 0; var2 < 14; var2++) {
         var1.handle(state[var2], 0.0F, 0.0F, 100.0F, 0.0F);
      }
   }

   private static String[] prepare() {
      String[] var0 = new String[14];

      for (int var1 = 0; var1 < var0.length; var1++) {
         var0[var1] = "uTrail[" + var1 + "]";
      }

      return var0;
   }

   @Override
   public void close() {
      this.output.close();
      if (this.current != null) {
         this.current.close();
         this.current = null;
      }

      this.cache.close();
      this.responseCompute = false;
      this.profileDraw = 0.0F;
      this.vectorPerform = 1.0F;
      this.eventAttach = false;
      this.serverRead = 0L;
      this.renderer = 0L;
      this.handler = 0L;
      this.animationDraw = 0L;
      this.pointEncode = 0.0F;
      this.animator = 0.0F;
      this.source = 0.0F;
      this.target = 0.0F;
      this.pending = 0.0F;
      this.previous = 0.0F;
      this.latest = false;
   }

   private static float process(float var0, float var1) {
      return handle(Math.min(var0 / 1920.0F, var1 / 1080.0F) * 1.16F, 0.72F, 1.38F);
   }

   private static float compute(float var0, float var1) {
      return (float)Math.sqrt(var0 * var0 + var1 * var1);
   }

   private static float handle(float var0, float var1, float var2) {
      return Math.max(var1, Math.min(var2, var0));
   }

   private static float update(int var0) {
      return (var0 >> 16 & 0xFF) / 255.0F;
   }

   private static float apply(int var0) {
      return (var0 >> 8 & 0xFF) / 255.0F;
   }

   private static float execute(int var0) {
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

   private static int process(int var0, int var1, float var2, float var3) {
      float var4 = handle(var2, 0.0F, 1.0F);
      int var5 = PackedColor.resolve(var0, var1, var4);
      int var6 = Math.round(handle(var3, 0.0F, 1.0F) * 255.0F);
      return var6 << 24 | var5;
   }
}
