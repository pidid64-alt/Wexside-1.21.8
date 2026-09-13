package ru.wild.core;

import com.mojang.blaze3d.systems.RenderSystem;
import java.nio.FloatBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;
import ru.wild.render.OpenGlStateSnapshot;
import ru.wild.render.VertexLayoutBinding;
import ru.wild.render.shader.ShaderBuildReporter;
import ru.wild.render.shader.ShaderViewportTracker;

public final class GlowEspRenderer implements AutoCloseable {
   private static final Logger config = LogManager.getLogger("GlowESP");
   private static final String state = "assets/wild/shaders/glowesp/fullscreen.vert";
   private static final String cache = "assets/wild/shaders/glowesp/mask.frag";
   private static final String output = "assets/wild/shaders/glowesp/shadow.frag";
   private static final String current = "assets/wild/shaders/glowesp/gradient.frag";
   private static final String active = "assets/wild/shaders/glowesp/dominant_color.frag";
   private static final String mode = "assets/wild/shaders/blur/blur_downsample.frag";
   private static final int selection = 63;
   private static final int enabled = 2;
   private static final float renderer = 8.0F;
   private final GlowEspRenderer.ColorState handler = new GlowEspRenderer.ColorState();
   private final GlowEspRenderer.ColorState animationDraw = new GlowEspRenderer.ColorState();
   private final GlowEspRenderer.ColorState pointEncode = new GlowEspRenderer.ColorState();
   private final GlowEspRenderer.ColorState animator = new GlowEspRenderer.ColorState();
   private final GlowEspRenderer.ColorState source = new GlowEspRenderer.ColorState();
   private ShaderBuildReporter target;
   private ShaderBuildReporter pending;
   private ShaderBuildReporter previous;
   private ShaderBuildReporter latest;
   private ShaderBuildReporter summary;
   private int matrixBlend;
   private int vectorMatch;
   private boolean itemProject;
   private boolean responseCompute;
   private String providerFetch = "not-run";
   private int profileDraw;
   private int vectorPerform;
   private int eventAttach = -1;
   private final float[] serverRead = new float[64];
   public static final int instance = 0;
   public static final int data = 1;
   public static final int context = 2;

   public boolean handle(int var1, int var2, int var3, int var4, GlowEspRenderer.ColorStop var5) {
      return this.handle(var1, var2, var1, var3, var4, var5, null);
   }

   public boolean handle(int var1, int var2, int var3, int var4, GlowEspRenderer.ColorStop var5, GlowEspRenderer.Bounds var6) {
      return this.handle(var1, var2, var1, var3, var4, var5, var6);
   }

   public boolean handle(int var1, int var2, int var3, int var4, GlowEspRenderer.ColorStop var5, GlowEspRenderer.Bounds var6, int var7, int var8, int var9) {
      return this.handle(var1, var2, var1, var3, var4, var5, var6, var7, var8, var9);
   }

   public boolean handle(int var1, int var2, int var3, int var4, int var5, GlowEspRenderer.ColorStop var6) {
      return this.handle(var1, var2, var3, var4, var5, var6, null);
   }

   public boolean handle(int var1, int var2, int var3, int var4, int var5, GlowEspRenderer.ColorStop var6, GlowEspRenderer.Bounds var7) {
      return this.handle(var1, var2, var3, var4, var5, var6, var7, 0, 0, 0);
   }

   public boolean handle(
      int var1, int var2, int var3, int var4, int var5, GlowEspRenderer.ColorStop var6, GlowEspRenderer.Bounds var7, int var8, int var9, int var10
   ) {
      if (this.responseCompute) {
         this.providerFetch = "renderer-broken";
         return false;
      }

      if (var1 <= 0 || var4 <= 0 || var5 <= 0 || var6 == null) {
         this.providerFetch = "invalid-input";
         return false;
      }

      if (!prepare()) {
         this.providerFetch = "no-render-context";
         return false;
      }

      try {
         boolean var11 = this.process(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
         this.providerFetch = var11 ? "rendered" : this.providerFetch;
         return var11;
      } catch (Throwable var12) {
         this.responseCompute = true;
         this.providerFetch = "exception:" + var12.getClass().getSimpleName() + ":" + var12.getMessage();
         config.warn("GlowESP renderer disabled", var12);
         return false;
      }
   }
   private boolean process(
      int var1, int var2, int var3, int var4, int var5, GlowEspRenderer.ColorStop var6, GlowEspRenderer.Bounds var7, int var8, int var9, int var10
   ) {
      OpenGlStateSnapshot.NetworkState var11 = OpenGlStateSnapshot.handle();
      boolean var19 = false /* VF: Semaphore variable */;

      int var21;
      label89: {
         int var22;
         label88: {
            boolean var23;
            label87: {
               try {
                  var19 = true;
                  this.resolve();
                  GlowEspRenderer.Bounds var12 = handle(var7, var4, var5);
                  if (!this.handle(this.handler, var4, var5)) {
                     this.providerFetch = "mask-target-incomplete";
                     var21 = 0;
                     var19 = false;
                     break label89;
                  }

                  if (var12 != null) {
                     this.process(this.handler, var12);
                  }

                  var21 = this.handle(var1, var2, var4, var5, var12, var8, var9, var10);
                  int var14 = 0;
                  if (var6.autoColor != 0) {
                     if (!this.handle(this.source, 1, 1)) {
                        this.providerFetch = "dominant-color-target-incomplete";
                        var22 = 0;
                        var19 = false;
                        break label88;
                     }

                     var14 = this.handle(var3 > 0 ? var3 : var1);
                  }

                  var22 = var21;
                  if (var6.glowStrength > 0.001F || var6.debugView == 2) {
                     var22 = this.handle(var21, var4, var5, var6.radius, var12);
                     if (var22 == 0) {
                        this.providerFetch = "blur-target-incomplete";
                        var23 = false;
                        var19 = false;
                        break label87;
                     }
                  }

                  var23 = this.handle(var21, var22, var14, var4, var5, var6, var11, var12);
                  var19 = false;
               } finally {
                  if (var19) {
                     GL20.glUseProgram(0);
                     GL30.glBindVertexArray(0);
                     GL13.glActiveTexture(33987);
                     GL11.glBindTexture(3553, 0);
                     GL13.glActiveTexture(33986);
                     GL11.glBindTexture(3553, 0);
                     GL13.glActiveTexture(33985);
                     GL11.glBindTexture(3553, 0);
                     GL13.glActiveTexture(33984);
                     GL11.glBindTexture(3553, 0);
                     OpenGlStateSnapshot.compute(var11);
                  }
               }

               GL20.glUseProgram(0);
               GL30.glBindVertexArray(0);
               GL13.glActiveTexture(33987);
               GL11.glBindTexture(3553, 0);
               GL13.glActiveTexture(33986);
               GL11.glBindTexture(3553, 0);
               GL13.glActiveTexture(33985);
               GL11.glBindTexture(3553, 0);
               GL13.glActiveTexture(33984);
               GL11.glBindTexture(3553, 0);
               OpenGlStateSnapshot.compute(var11);
               return var23;
            }

            GL20.glUseProgram(0);
            GL30.glBindVertexArray(0);
            GL13.glActiveTexture(33987);
            GL11.glBindTexture(3553, 0);
            GL13.glActiveTexture(33986);
            GL11.glBindTexture(3553, 0);
            GL13.glActiveTexture(33985);
            GL11.glBindTexture(3553, 0);
            GL13.glActiveTexture(33984);
            GL11.glBindTexture(3553, 0);
            OpenGlStateSnapshot.compute(var11);
            return var23;
         }

         GL20.glUseProgram(0);
         GL30.glBindVertexArray(0);
         GL13.glActiveTexture(33987);
         GL11.glBindTexture(3553, 0);
         GL13.glActiveTexture(33986);
         GL11.glBindTexture(3553, 0);
         GL13.glActiveTexture(33985);
         GL11.glBindTexture(3553, 0);
         GL13.glActiveTexture(33984);
         GL11.glBindTexture(3553, 0);
         OpenGlStateSnapshot.compute(var11);
         return var22 != 0;
      }

      GL20.glUseProgram(0);
      GL30.glBindVertexArray(0);
      GL13.glActiveTexture(33987);
      GL11.glBindTexture(3553, 0);
      GL13.glActiveTexture(33986);
      GL11.glBindTexture(3553, 0);
      GL13.glActiveTexture(33985);
      GL11.glBindTexture(3553, 0);
      GL13.glActiveTexture(33984);
      GL11.glBindTexture(3553, 0);
      OpenGlStateSnapshot.compute(var11);
      return var21 != 0;
   }

   private int handle(int var1, int var2, int var3, int var4, GlowEspRenderer.Bounds var5, int var6, int var7, int var8) {
      boolean var9 = var8 != 0 && var6 > 0;
      this.apply();
      this.target.handle();
      handle(this.target, "uSource", 0);
      handle(this.target, "uDepthSource", 1);
      handle(this.target, "uTagged", 2);
      handle(this.target, "uTaggedDepth", 3);
      handle(this.target, "uHasDepth", var2 > 0 ? 1 : 0);
      handle(this.target, "uTagMode", var9 ? var8 : 0);
      handle(this.target, "uThreshold", 0.05F);
      this.handle(this.handler, var5);
      GL13.glActiveTexture(33984);
      GL11.glBindTexture(3553, var1);
      GL13.glActiveTexture(33985);
      GL11.glBindTexture(3553, var2 > 0 ? var2 : var1);
      GL13.glActiveTexture(33986);
      GL11.glBindTexture(3553, var9 ? var6 : var1);
      GL13.glActiveTexture(33987);
      GL11.glBindTexture(3553, var9 && var7 > 0 ? var7 : var1);
      this.execute();
      return this.handler.data;
   }

   private int handle(int var1, int var2, int var3, float var4, GlowEspRenderer.Bounds var5) {
      int var6 = handle(var4, var2, var3);
      int var7 = var6 > 1 ? Math.max(1, var2 / var6) : var2;
      int var8 = var6 > 1 ? Math.max(1, var3 / var6) : var3;
      int var9 = var1;
      GlowEspRenderer.Bounds var10 = handle(var5, var7, var8, var2, var3);
      if (var6 > 1) {
         if (!this.handle(this.animationDraw, var7, var8)) {
            return 0;
         }

         if (var5 != null) {
            this.process(this.animationDraw, var10);
         }

         var9 = this.handle(var1, var2, var3, var10);
      }

      if (this.handle(this.pointEncode, var7, var8) && this.handle(this.animator, var7, var8)) {
         if (var5 != null) {
            this.process(this.pointEncode, var10);
            this.process(this.animator, var10);
         }

         int var11 = Math.max(1, Math.min(63, Math.round(var4 / var6)));
         float[] var12 = this.process(var11);
         this.apply();
         this.pending.handle();
         handle(this.pending, "uSource", 0);
         handle(this.pending, "uTexelSize", 1.0F / var7, 1.0F / var8);
         handle(this.pending, "uRadius", var11);
         int var13 = this.pending.handle("uKernel[0]");
         if (var13 >= 0) {
            GL20.glUniform1fv(var13, var12);
         }

         this.handle(this.pointEncode, var10);
         handle(this.pending, "uDirection", 1.0F, 0.0F);
         GL13.glActiveTexture(33984);
         GL11.glBindTexture(3553, var9);
         this.execute();
         this.handle(this.animator, var10);
         handle(this.pending, "uDirection", 0.0F, 1.0F);
         GL11.glBindTexture(3553, this.pointEncode.data);
         this.execute();
         return this.animator.data;
      } else {
         return 0;
      }
   }

   private int handle(int var1, int var2, int var3, GlowEspRenderer.Bounds var4) {
      this.apply();
      this.summary.handle();
      handle(this.summary, "uSource", 0);
      handle(this.summary, "uTexelSize", 1.0F / var2, 1.0F / var3);
      handle(this.summary, "uOffset", 1.0F);
      this.handle(this.animationDraw, var4);
      GL13.glActiveTexture(33984);
      GL11.glBindTexture(3553, var1);
      this.execute();
      return this.animationDraw.data;
   }

   private int handle(int var1) {
      this.apply();
      this.latest.handle();
      handle(this.latest, "uSource", 0);
      this.handle(this.source);
      GL13.glActiveTexture(33984);
      GL11.glBindTexture(3553, var1);
      this.execute();
      return this.source.data;
   }

   private boolean handle(
      int var1, int var2, int var3, int var4, int var5, GlowEspRenderer.ColorStop var6, OpenGlStateSnapshot.NetworkState var7, GlowEspRenderer.Bounds var8
   ) {
      GL30.glBindFramebuffer(36009, var7.instance);
      this.profileDraw = var7.instance;
      this.vectorPerform = GL30.glCheckFramebufferStatus(36009);
      if (this.vectorPerform != 36053) {
         this.providerFetch = "output-framebuffer-incomplete";
         return false;
      } else {
         GL11.glDrawBuffer(var7.context);
         GL11.glViewport(var7.state[0], var7.state[1], var7.state[2], var7.state[3]);
         handle(var8, var4, var5, var7);
         GL11.glDisable(2929);
         GL11.glDisable(2884);
         GL11.glDisable(36281);
         GL11.glEnable(3042);
         GL14.glBlendFuncSeparate(770, 771, 1, 771);
         GL11.glColorMask(true, true, true, true);
         GL11.glDepthMask(false);
         this.previous.handle();
         handle(this.previous, "uMask", 0);
         handle(this.previous, "uBlur", 1);
         handle(this.previous, "uAutoColor", 2);
         handle(this.previous, "uAutoColorEnabled", var6.autoColor);
         handle(this.previous, "uTexelSize", 1.0F / var4, 1.0F / var5);
         handle(this.previous, "uOutlineWidth", var6.outlineWidth);
         handle(this.previous, "uGlowStrength", var6.glowStrength);
         handle(this.previous, "uOutlineStrength", var6.outlineStrength);
         handle(this.previous, "uOpacity", var6.opacity);
         handle(this.previous, "uDebugView", var6.debugView);
         handle(this.previous, "uColorStyle", var6.colorStyle);
         handle(this.previous, "uTime", (float)(System.nanoTime() % 30000000000L) / 1.0E9F);
         handle(this.previous, "uColorTop", var6.topR, var6.topG, var6.topB, 1.0F);
         handle(this.previous, "uColorBottom", var6.bottomR, var6.bottomG, var6.bottomB, 1.0F);
         GL13.glActiveTexture(33984);
         GL11.glBindTexture(3553, var1);
         GL13.glActiveTexture(33985);
         GL11.glBindTexture(3553, var2);
         GL13.glActiveTexture(33986);
         GL11.glBindTexture(3553, var3);
         GL30.glBindVertexArray(this.matrixBlend);
         this.execute();
         return true;
      }
   }

   public String handle() {
      return this.providerFetch;
   }

   public int process() {
      return this.profileDraw;
   }

   public int compute() {
      return this.vectorPerform;
   }

   private void resolve() {
      if (!this.itemProject) {
         this.target = ShaderBuildReporter.handle("assets/wild/shaders/glowesp/fullscreen.vert", "assets/wild/shaders/glowesp/mask.frag");
         this.pending = ShaderBuildReporter.handle("assets/wild/shaders/glowesp/fullscreen.vert", "assets/wild/shaders/glowesp/shadow.frag");
         this.previous = ShaderBuildReporter.handle("assets/wild/shaders/glowesp/fullscreen.vert", "assets/wild/shaders/glowesp/gradient.frag");
         this.latest = ShaderBuildReporter.handle("assets/wild/shaders/glowesp/fullscreen.vert", "assets/wild/shaders/glowesp/dominant_color.frag");
         this.summary = ShaderBuildReporter.handle("assets/wild/shaders/glowesp/fullscreen.vert", "assets/wild/shaders/blur/blur_downsample.frag");
         this.matrixBlend = GL30.glGenVertexArrays();
         this.vectorMatch = GL15.glGenBuffers();
         GL30.glBindVertexArray(this.matrixBlend);
         GL15.glBindBuffer(34962, this.vectorMatch);
         float[] var1 = new float[]{-1.0F, -1.0F, 0.0F, 0.0F, 1.0F, -1.0F, 1.0F, 0.0F, -1.0F, 1.0F, 0.0F, 1.0F, 1.0F, 1.0F, 1.0F, 1.0F};
         GL15.glBufferData(34962, var1, 35044);
         byte var2 = 16;
         GL20.glEnableVertexAttribArray(0);
         GL20.glVertexAttribPointer(0, 2, 5126, false, var2, 0L);
         GL20.glEnableVertexAttribArray(1);
         GL20.glVertexAttribPointer(1, 2, 5126, false, var2, 8L);
         this.itemProject = true;
      }
   }

   private boolean handle(GlowEspRenderer.ColorState var1, int var2, int var3) {
      if (var1.data != 0 && (var1.context != var2 || var1.config != var3 || var1.instance == 0)) {
         this.process(var1);
      }

      if (var1.data == 0) {
         var1.data = GL11.glGenTextures();
         GL11.glBindTexture(3553, var1.data);
         GL11.glTexParameteri(3553, 10241, 9729);
         GL11.glTexParameteri(3553, 10240, 9729);
         GL11.glTexParameteri(3553, 10242, 33071);
         GL11.glTexParameteri(3553, 10243, 33071);
         VertexLayoutBinding.handle(32856, var2, var3, 6408, 5121);
         var1.instance = GL30.glGenFramebuffers();
         GL30.glBindFramebuffer(36160, var1.instance);
         GL30.glFramebufferTexture2D(36160, 36064, 3553, var1.data, 0);
         GL11.glDrawBuffer(36064);
         if (GL30.glCheckFramebufferStatus(36160) != 36053) {
            this.process(var1);
            return false;
         }

         var1.state = true;
         var1.cache = null;
      }

      var1.context = var2;
      var1.config = var3;
      return true;
   }

   private void handle(GlowEspRenderer.ColorState var1) {
      GL30.glBindFramebuffer(36160, var1.instance);
      GL11.glDrawBuffer(36064);
      GL11.glViewport(0, 0, var1.context, var1.config);
   }

   private void handle(GlowEspRenderer.ColorState var1, GlowEspRenderer.Bounds var2) {
      this.handle(var1);
      process(var2, var1.context, var1.config);
      if (var2 == null) {
         var1.state = true;
         var1.cache = null;
      }
   }

   private void process(GlowEspRenderer.ColorState var1, GlowEspRenderer.Bounds var2) {
      this.handle(var1);
      GL11.glColorMask(true, true, true, true);
      if (var2 != null && !var1.state) {
         GlowEspRenderer.Bounds var3 = var1.cache;
         if (var3 != null) {
            process(var3, var1.context, var1.config);
            this.update();
         }

         process(var2, var1.context, var1.config);
         this.update();
         var1.cache = var2;
      } else {
         GL11.glDisable(3089);
         this.update();
         var1.state = false;
         var1.cache = var2;
      }
   }

   private void update() {
      MemoryStack var1 = MemoryStack.stackPush();

      try {
         FloatBuffer var2 = var1.floats(0.0F, 0.0F, 0.0F, 0.0F);
         GL30.glClearBufferfv(6144, 0, var2);
      } catch (Throwable var5) {
         if (var1 != null) {
            try {
               var1.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (var1 != null) {
         var1.close();
      }
   }

   private void apply() {
      GL11.glDisable(3089);
      GL11.glDisable(2929);
      GL11.glDisable(2884);
      GL11.glDisable(3042);
      GL11.glDisable(36281);
      GL11.glColorMask(true, true, true, true);
      GL11.glDepthMask(false);
      GL30.glBindVertexArray(this.matrixBlend);
   }

   private float[] process(int var1) {
      if (this.eventAttach == var1) {
         return this.serverRead;
      }

      for (int var2 = 0; var2 < this.serverRead.length; var2++) {
         this.serverRead[var2] = 0.0F;
      }

      float var7 = Math.max(var1 * 0.5F, 0.5F);
      float var3 = 2.0F * var7 * var7;
      float var4 = 0.0F;

      for (int var5 = 0; var5 <= var1; var5++) {
         float var6 = (float)Math.exp(-(var5 * var5) / var3);
         this.serverRead[var5] = var6;
         var4 += var5 == 0 ? var6 : var6 * 2.0F;
      }

      float var8 = var4 > 0.0F ? 1.0F / var4 : 1.0F;

      for (int var9 = 0; var9 <= var1; var9++) {
         this.serverRead[var9] = this.serverRead[var9] * var8;
      }

      this.eventAttach = var1;
      return this.serverRead;
   }

   private static int handle(float var0, int var1, int var2) {
      return !(var0 < 8.0F) && var1 >= 2 && var2 >= 2 ? 2 : 1;
   }

   private static GlowEspRenderer.Bounds handle(GlowEspRenderer.Bounds var0, int var1, int var2) {
      if (var0 != null && var1 > 0 && var2 > 0) {
         int var3 = Math.max(0, var0.x);
         int var4 = Math.max(0, var0.y);
         int var5 = Math.min(var1, var0.x + var0.width);
         int var6 = Math.min(var2, var0.y + var0.height);
         int var7 = var5 - var3;
         int var8 = var6 - var4;
         if (var7 > 0 && var8 > 0) {
            long var9 = (long)var7 * var8;
            long var11 = (long)var1 * var2;
            return var9 >= var11 * 9L / 10L ? null : new GlowEspRenderer.Bounds(var3, var4, var7, var8);
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private static GlowEspRenderer.Bounds handle(GlowEspRenderer.Bounds var0, int var1, int var2, int var3, int var4) {
      if (var0 != null && var1 > 0 && var2 > 0 && var3 > 0 && var4 > 0) {
         float var5 = (float)var1 / var3;
         float var6 = (float)var2 / var4;
         int var7 = (int)Math.floor(var0.x * var5);
         int var8 = (int)Math.floor(var0.y * var6);
         int var9 = (int)Math.ceil((var0.x + var0.width) * var5);
         int var10 = (int)Math.ceil((var0.y + var0.height) * var6);
         return handle(new GlowEspRenderer.Bounds(var7, var8, var9 - var7, var10 - var8), var1, var2);
      } else {
         return null;
      }
   }

   private static void process(GlowEspRenderer.Bounds var0, int var1, int var2) {
      if (var0 == null) {
         GL11.glDisable(3089);
      } else {
         GL11.glEnable(3089);
         GL11.glScissor(var0.x, var2 - var0.y - var0.height, var0.width, var0.height);
      }
   }

   private static void handle(GlowEspRenderer.Bounds var0, int var1, int var2, OpenGlStateSnapshot.NetworkState var3) {
      GlowEspRenderer.Bounds var4 = handle(var0, var3.state[2], var3.state[3], var1, var2);
      if (var4 == null) {
         GL11.glDisable(3089);
      } else {
         GL11.glEnable(3089);
         GL11.glScissor(var3.state[0] + var4.x, var3.state[1] + var3.state[3] - var4.y - var4.height, var4.width, var4.height);
      }
   }

   private void execute() {
      ShaderViewportTracker.handle().handle(2);
      GL11.glDrawArrays(5, 0, 4);
   }

   private static void handle(ShaderBuildReporter var0, String var1, int var2) {
      int var3 = var0.handle(var1);
      if (var3 >= 0) {
         GL20.glUniform1i(var3, var2);
      }
   }

   private static void handle(ShaderBuildReporter var0, String var1, float var2) {
      int var3 = var0.handle(var1);
      if (var3 >= 0) {
         GL20.glUniform1f(var3, var2);
      }
   }

   private static void handle(ShaderBuildReporter var0, String var1, float var2, float var3) {
      int var4 = var0.handle(var1);
      if (var4 >= 0) {
         GL20.glUniform2f(var4, var2, var3);
      }
   }

   private static void handle(ShaderBuildReporter var0, String var1, float var2, float var3, float var4, float var5) {
      int var6 = var0.handle(var1);
      if (var6 >= 0) {
         GL20.glUniform4f(var6, var2, var3, var4, var5);
      }
   }

   private static boolean prepare() {
      return RenderSystem.isOnRenderThread() && GLFW.glfwGetCurrentContext() != 0L;
   }

   private void process(GlowEspRenderer.ColorState var1) {
      if (var1.instance != 0) {
         GL30.glDeleteFramebuffers(var1.instance);
      }

      if (var1.data != 0) {
         GL11.glDeleteTextures(var1.data);
      }

      var1.instance = 0;
      var1.data = 0;
      var1.context = 0;
      var1.config = 0;
      var1.state = true;
      var1.cache = null;
   }

   @Override
   public void close() {
      if (!prepare()) {
         this.itemProject = false;
         this.responseCompute = false;
      } else {
         this.process(this.handler);
         this.process(this.animationDraw);
         this.process(this.pointEncode);
         this.process(this.animator);
         this.process(this.source);
         if (this.matrixBlend != 0) {
            GL30.glDeleteVertexArrays(this.matrixBlend);
            this.matrixBlend = 0;
         }

         if (this.vectorMatch != 0) {
            GL15.glDeleteBuffers(this.vectorMatch);
            this.vectorMatch = 0;
         }

         if (this.target != null) {
            this.target.process();
            this.target = null;
         }

         if (this.pending != null) {
            this.pending.process();
            this.pending = null;
         }

         if (this.previous != null) {
            this.previous.process();
            this.previous = null;
         }

         if (this.latest != null) {
            this.latest.process();
            this.latest = null;
         }

         if (this.summary != null) {
            this.summary.process();
            this.summary = null;
         }

         this.eventAttach = -1;
         this.itemProject = false;
         this.responseCompute = false;
      }
   }

   public record Bounds(int x, int y, int width, int height) {
   }

   static final class ColorState {
      int instance;
      int data;
      int context;
      int config;
      boolean state = true;
      GlowEspRenderer.Bounds cache;
   }

   public record ColorStop(
      float radius,
      float outlineWidth,
      float glowStrength,
      float outlineStrength,
      float opacity,
      int debugView,
      int colorStyle,
      int autoColor,
      float topR,
      float topG,
      float topB,
      float bottomR,
      float bottomG,
      float bottomB
   ) {
   }
}
