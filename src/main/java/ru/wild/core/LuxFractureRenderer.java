package ru.wild.core;

import net.minecraft.client.MinecraftClient;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import ru.wild.WildClient;
import ru.wild.gui.theme.ThemeRenderer;
import ru.wild.render.OpenGlStateSnapshot;
import ru.wild.render.shader.ShaderBuildReporter;
import ru.wild.render.shader.ShaderRenderer;
import ru.wild.util.render.RoundedRectRenderer;

public final class LuxFractureRenderer {
   private static final LuxFractureRenderer instance = new LuxFractureRenderer();
   private static final String data = "assets/wild/shaders/blur/blur_fullscreen.vert";
   private static final String context = "assets/wild/shaders/hud/lux_fracture_edge.frag";
   private static final long config = 3000000L;
   private static final float state = 1.0F;
   private static final float cache = 2.5F;
   private ShaderBuildReporter output;
   private int current;
   private int active;
   private int mode = -1;
   private int selection = -1;
   private int enabled = -1;
   private int renderer = -1;
   private int handler = -1;
   private int animationDraw = -1;
   private int pointEncode = -1;
   private int animator = -1;
   private int source = -1;
   private int target = -1;
   private int pending = -1;
   private int previous = -1;
   private int latest = -1;
   private int summary = -1;
   private int matrixBlend = -1;
   private int vectorMatch = -1;
   private int itemProject = -1;
   private int responseCompute = -1;
   private int providerFetch = -1;
   private int profileDraw = -1;
   private int vectorPerform = -1;
   private int eventAttach = -1;
   private int serverRead = -1;
   private boolean positionAdvance;
   private boolean frameCheck;
   private int moduleCollect;
   private int providerClose;
   private int presetSave;
   private long windowConvert = Long.MIN_VALUE;
   private float presetWrite;
   private float colorMeasure;
   private float animationSchedule;
   private long rendererScan = Long.MIN_VALUE;

   private LuxFractureRenderer() {
   }

   static boolean handle(
      RoundedRectRenderer var0,
      float var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      boolean var7,
      int var8,
      int var9,
      int var10,
      int var11,
      boolean var12,
      boolean var13,
      float var14
   ) {
      return instance.process(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14);
   }

   private boolean process(
      RoundedRectRenderer var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      boolean var8,
      int var9,
      int var10,
      int var11,
      int var12,
      boolean var13,
      boolean var14,
      float var15
   ) {
      MinecraftClient var16 = MinecraftClient.getInstance();
      if (!this.frameCheck && var1 != null && var16 != null && var16.getWindow() != null && !(var4 <= 1.0F) && !(var5 <= 1.0F) && !(var7 <= 0.001F)) {
         int var17 = var16.getWindow().getFramebufferWidth();
         int var18 = var16.getWindow().getFramebufferHeight();
         if (var17 > 1 && var18 > 1 && this.handle()) {
            var1.compute();
            int var19 = this.handle(var17, var18);
            float var20 = Math.max(56.0F, var6 * 5.0F);
            float var21 = ThemeRenderer.handle().execute();
            float var22 = ThemeRenderer.handle().prepare();
            float var23 = this.handle(var21, var22);
            OpenGlStateSnapshot.NetworkState var24 = OpenGlStateSnapshot.handle();

            try {
               GL11.glViewport(0, 0, var17, var18);
               GL11.glDisable(2929);
               GL11.glDisable(2884);
               GL11.glDisable(3089);
               GL11.glDepthMask(false);
               GL11.glColorMask(true, true, true, true);
               GL11.glEnable(3042);
               GL14.glBlendFuncSeparate(770, 771, 1, 771);
               GL11.glDisable(36281);
               this.output.handle();
               if (this.mode >= 0) {
                  GL20.glUniform2f(this.mode, var17, var18);
               }

               if (this.selection >= 0) {
                  GL20.glUniform1f(this.selection, (float)(System.nanoTime() % 720000000000L) / 1.0E9F);
               }

               if (this.enabled >= 0) {
                  GL20.glUniform4f(this.enabled, var2 - var20, var3 - var20, var4 + var20 * 2.0F, var5 + var20 * 2.0F);
               }

               if (this.renderer >= 0) {
                  GL20.glUniform4f(this.renderer, var2, var3, var4, var5);
               }

               if (this.handler >= 0) {
                  GL20.glUniform1f(this.handler, Math.max(0.0F, var6));
               }

               if (this.animationDraw >= 0) {
                  GL20.glUniform1f(this.animationDraw, handle(var7));
               }

               if (this.pointEncode >= 0) {
                  GL20.glUniform1f(this.pointEncode, var8 ? 1.0F : 0.0F);
               }

               if (this.animator >= 0) {
                  compute(this.animator, var9);
               }

               if (this.source >= 0) {
                  compute(this.source, var10);
               }

               if (this.target >= 0) {
                  process(this.target, var11);
               }

               if (this.pending >= 0) {
                  process(this.pending, var12);
               }

               if (this.previous >= 0) {
                  GL20.glUniform2f(this.previous, var21, var22);
               }

               if (this.vectorPerform >= 0) {
                  GL20.glUniform1f(this.vectorPerform, var23);
               }

               if (this.eventAttach >= 0) {
                  GL20.glUniform1f(this.eventAttach, 1.0F);
               }

               if (this.serverRead >= 0) {
                  GL20.glUniform1f(this.serverRead, 2.5F);
               }

               if (this.latest >= 0) {
                  GL20.glUniform1f(this.latest, var13 ? 1.0F : 0.0F);
               }

               if (this.summary >= 0) {
                  GL20.glUniform1f(this.summary, var14 ? 1.0F : 0.0F);
               }

               if (this.matrixBlend >= 0) {
                  GL20.glUniform1f(this.matrixBlend, handle(var15));
               }

               if (this.vectorMatch >= 0) {
                  GL20.glUniform1f(this.vectorMatch, var20);
               }

               if (this.itemProject >= 0) {
                  GL20.glUniform1f(this.itemProject, 0.6F);
               }

               if (this.responseCompute >= 0) {
                  GL13.glActiveTexture(33984);
                  GL11.glBindTexture(3553, Math.max(var19, 0));
                  GL20.glUniform1i(this.responseCompute, 0);
               }

               if (this.providerFetch >= 0) {
                  GL20.glUniform2f(this.providerFetch, this.providerClose > 0 ? this.providerClose : var17, this.presetSave > 0 ? this.presetSave : var18);
               }

               if (this.profileDraw >= 0) {
                  GL20.glUniform1f(this.profileDraw, var19 > 0 ? 1.0F : 0.0F);
               }

               GL30.glBindVertexArray(this.current);
               GL11.glDrawArrays(4, 0, 6);
               GL30.glBindVertexArray(0);
               return true;
            } catch (Throwable var30) {
               System.err.println("[LuxFracture] surface draw disabled: " + var30.getMessage());
               var30.printStackTrace();
               this.frameCheck = true;
               return false;
            } finally {
               GL20.glUseProgram(0);
               OpenGlStateSnapshot.compute(var24);
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private float handle(float var1, float var2) {
      long var3 = System.nanoTime();
      if (var3 - this.rendererScan > 3000000L) {
         float var5 = var1 - this.presetWrite;
         float var6 = var2 - this.colorMeasure;
         float var7 = (float)Math.sqrt(var5 * var5 + var6 * var6);
         this.animationSchedule = this.animationSchedule * 0.55F + Math.min(1.0F, var7 / 36.0F) * 0.45F;
         if (this.animationSchedule < 8.0E-4F) {
            this.animationSchedule = 0.0F;
         }

         this.presetWrite = var1;
         this.colorMeasure = var2;
         this.rendererScan = var3;
      }

      return this.animationSchedule;
   }

   private int handle(int var1, int var2) {
      long var3 = System.nanoTime();
      if (this.moduleCollect > 0 && this.providerClose == var1 && this.presetSave == var2 && var3 - this.windowConvert < 3000000L) {
         return this.moduleCollect;
      }

      try {
         ShaderRenderer var5 = WildClient.compute();
         if (var5 == null) {
            this.moduleCollect = 0;
            return 0;
         } else {
            ShaderRenderer.Bounds var6 = var5.resolve();
            if (var6 != null && var6.colorTexture() > 0 && var6.width() > 0 && var6.height() > 0) {
               this.moduleCollect = var6.colorTexture();
               this.providerClose = var6.width();
               this.presetSave = var6.height();
               this.windowConvert = var3;
               return this.moduleCollect;
            } else {
               this.moduleCollect = 0;
               return 0;
            }
         }
      } catch (Throwable var7) {
         this.moduleCollect = 0;
         return 0;
      }
   }

   private boolean handle() {
      if (!this.positionAdvance) {
         this.positionAdvance = true;

         try {
            this.output = ShaderBuildReporter.handle("assets/wild/shaders/blur/blur_fullscreen.vert", "assets/wild/shaders/hud/lux_fracture_edge.frag");
            this.mode = this.output.handle("uResolution");
            this.selection = this.output.handle("uTime");
            this.enabled = this.output.handle("uDrawRect");
            this.renderer = this.output.handle("uElementRect");
            this.handler = this.output.handle("uRadius");
            this.animationDraw = this.output.handle("uAlpha");
            this.pointEncode = this.output.handle("uInset");
            this.animator = this.output.handle("uSurfaceColor");
            this.source = this.output.handle("uOutlineColor");
            this.target = this.output.handle("uAccentTop");
            this.pending = this.output.handle("uAccentBottom");
            this.previous = this.output.handle("uMouse");
            this.latest = this.output.handle("uShadow");
            this.summary = this.output.handle("uOutline");
            this.matrixBlend = this.output.handle("uLightMode");
            this.vectorMatch = this.output.handle("uPad");
            this.itemProject = this.output.handle("uSweepSpeed");
            this.responseCompute = this.output.handle("uScene");
            this.providerFetch = this.output.handle("uSceneSize");
            this.profileDraw = this.output.handle("uHasScene");
            this.vectorPerform = this.output.handle("uMouseVel");
            this.eventAttach = this.output.handle("uSpectralBloomStrength");
            this.serverRead = this.output.handle("uRefractionDensityFade");
            this.current = GL30.glGenVertexArrays();
            this.active = GL15.glGenBuffers();
            GL30.glBindVertexArray(this.current);
            GL15.glBindBuffer(34962, this.active);
            float[] var1 = new float[]{
               -1.0F,
               -1.0F,
               0.0F,
               0.0F,
               1.0F,
               -1.0F,
               1.0F,
               0.0F,
               1.0F,
               1.0F,
               1.0F,
               1.0F,
               -1.0F,
               -1.0F,
               0.0F,
               0.0F,
               1.0F,
               1.0F,
               1.0F,
               1.0F,
               -1.0F,
               1.0F,
               0.0F,
               1.0F
            };
            GL15.glBufferData(34962, var1, 35044);
            byte var2 = 16;
            GL20.glEnableVertexAttribArray(0);
            GL20.glVertexAttribPointer(0, 2, 5126, false, var2, 0L);
            GL20.glEnableVertexAttribArray(1);
            GL20.glVertexAttribPointer(1, 2, 5126, false, var2, 8L);
            GL15.glBindBuffer(34962, 0);
            GL30.glBindVertexArray(0);
            return true;
         } catch (Throwable var3) {
            System.err.println("[LuxFracture] surface shader failed to load (style will fall back): " + var3.getMessage());
            var3.printStackTrace();
            this.frameCheck = true;
            this.output = null;
            return false;
         }
      } else {
         return this.output != null && this.current != 0;
      }
   }

   private static void process(int var0, int var1) {
      GL20.glUniform3f(var0, handle(var1), process(var1), compute(var1));
   }

   private static void compute(int var0, int var1) {
      GL20.glUniform4f(var0, handle(var1), process(var1), compute(var1), resolve(var1));
   }

   private static float handle(int var0) {
      return (var0 >>> 16 & 0xFF) / 255.0F;
   }

   private static float process(int var0) {
      return (var0 >>> 8 & 0xFF) / 255.0F;
   }

   private static float compute(int var0) {
      return (var0 & 0xFF) / 255.0F;
   }

   private static float resolve(int var0) {
      return (var0 >>> 24 & 0xFF) / 255.0F;
   }

   private static float handle(float var0) {
      return Math.max(0.0F, Math.min(1.0F, var0));
   }
}
