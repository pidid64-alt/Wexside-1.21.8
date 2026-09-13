package ru.wild.render.shader;

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
import ru.wild.util.render.RoundedRectRenderer;

public final class PrismaticEdgeShader {
   private static final PrismaticEdgeShader instance = new PrismaticEdgeShader();
   private static final String data = "assets/wild/shaders/blur/blur_fullscreen.vert";
   private static final String context = "assets/wild/shaders/hud/prismatic_edge.frag";
   private static final long config = 3000000L;
   private static final float state = 1.45F;
   private static final float cache = 1.0F;
   private static final float output = 0.62F;
   private static final float current = 0.55F;
   private static final float active = 1.7F;
   private ShaderBuildReporter mode;
   private int selection;
   private int enabled;
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
   private int positionAdvance = -1;
   private int frameCheck = -1;
   private int moduleCollect = -1;
   private int providerClose = -1;
   private int presetSave = -1;
   private int windowConvert = -1;
   private boolean presetWrite;
   private boolean colorMeasure;
   private int animationSchedule;
   private int rendererScan;
   private int sourceBuild;
   private long outputCollapse = Long.MIN_VALUE;
   private float profileInvoke;
   private float sourceSchedule;
   private float timerRender;
   private long scaleSave = Long.MIN_VALUE;

   private PrismaticEdgeShader() {
   }

   public static boolean handle(
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
      if (!this.colorMeasure && var1 != null && var16 != null && var16.getWindow() != null && !(var4 <= 1.0F) && !(var5 <= 1.0F) && !(var7 <= 0.001F)) {
         int var17 = var16.getWindow().getFramebufferWidth();
         int var18 = var16.getWindow().getFramebufferHeight();
         if (var17 > 1 && var18 > 1 && this.handle()) {
            var1.compute();
            int var19 = this.handle(var17, var18);
            float var20 = var8 ? Math.max(18.0F, var6 * 2.5F) : Math.max(72.0F, var6 * 4.0F);
            float var21 = ThemeRenderer.handle().execute();
            float var22 = ThemeRenderer.handle().prepare();
            float var23 = this.handle(var21, var22);
            OpenGlStateSnapshot.NetworkState var24 = OpenGlStateSnapshot.handle();
            boolean var30 = false /* VF: Semaphore variable */;

            boolean var25;
            label315: {
               boolean var26;
               try {
                  var30 = true;
                  GL11.glViewport(0, 0, var17, var18);
                  GL11.glDisable(2929);
                  GL11.glDisable(2884);
                  GL11.glDisable(3089);
                  GL11.glDepthMask(false);
                  GL11.glColorMask(true, true, true, true);
                  GL11.glEnable(3042);
                  GL14.glBlendFuncSeparate(770, 771, 1, 771);
                  GL11.glDisable(36281);
                  this.mode.handle();
                  if (this.renderer >= 0) {
                     GL20.glUniform2f(this.renderer, var17, var18);
                  }

                  if (this.handler >= 0) {
                     GL20.glUniform1f(this.handler, (float)(System.nanoTime() % 720000000000L) / 1.0E9F);
                  }

                  if (this.animationDraw >= 0) {
                     GL20.glUniform4f(this.animationDraw, var2 - var20, var3 - var20, var4 + var20 * 2.0F, var5 + var20 * 2.0F);
                  }

                  if (this.pointEncode >= 0) {
                     GL20.glUniform4f(this.pointEncode, var2, var3, var4, var5);
                  }

                  if (this.animator >= 0) {
                     GL20.glUniform1f(this.animator, Math.max(0.0F, var6));
                  }

                  if (this.source >= 0) {
                     GL20.glUniform1f(this.source, handle(var7));
                  }

                  if (this.target >= 0) {
                     GL20.glUniform1f(this.target, var8 ? 1.0F : 0.0F);
                  }

                  if (this.pending >= 0) {
                     compute(this.pending, var9);
                  }

                  if (this.previous >= 0) {
                     compute(this.previous, var10);
                  }

                  if (this.latest >= 0) {
                     process(this.latest, var11);
                  }

                  if (this.summary >= 0) {
                     process(this.summary, var12);
                  }

                  if (this.matrixBlend >= 0) {
                     GL20.glUniform2f(this.matrixBlend, var21, var22);
                  }

                  if (this.positionAdvance >= 0) {
                     GL20.glUniform1f(this.positionAdvance, var23);
                  }

                  if (this.frameCheck >= 0) {
                     GL20.glUniform1f(this.frameCheck, 1.45F);
                  }

                  if (this.moduleCollect >= 0) {
                     GL20.glUniform1f(this.moduleCollect, 1.0F);
                  }

                  if (this.providerClose >= 0) {
                     GL20.glUniform1f(this.providerClose, 0.62F);
                  }

                  if (this.presetSave >= 0) {
                     GL20.glUniform1f(this.presetSave, 0.55F);
                  }

                  if (this.windowConvert >= 0) {
                     GL20.glUniform1f(this.windowConvert, 1.7F);
                  }

                  if (this.vectorMatch >= 0) {
                     GL20.glUniform1f(this.vectorMatch, var13 ? 1.0F : 0.0F);
                  }

                  if (this.itemProject >= 0) {
                     GL20.glUniform1f(this.itemProject, var14 ? 1.0F : 0.0F);
                  }

                  if (this.responseCompute >= 0) {
                     GL20.glUniform1f(this.responseCompute, handle(var15));
                  }

                  if (this.providerFetch >= 0) {
                     GL20.glUniform1f(this.providerFetch, var20);
                  }

                  if (this.profileDraw >= 0) {
                     GL20.glUniform1f(this.profileDraw, 0.6F);
                  }

                  if (this.vectorPerform >= 0) {
                     GL13.glActiveTexture(33984);
                     GL11.glBindTexture(3553, Math.max(var19, 0));
                     GL20.glUniform1i(this.vectorPerform, 0);
                  }

                  if (this.eventAttach >= 0) {
                     GL20.glUniform2f(this.eventAttach, this.rendererScan > 0 ? this.rendererScan : var17, this.sourceBuild > 0 ? this.sourceBuild : var18);
                  }

                  if (this.serverRead >= 0) {
                     GL20.glUniform1f(this.serverRead, var19 > 0 ? 1.0F : 0.0F);
                  }

                  GL30.glBindVertexArray(this.selection);
                  GL11.glDrawArrays(4, 0, 6);
                  GL30.glBindVertexArray(0);
                  var25 = true;
                  var30 = false;
                  break label315;
               } catch (Throwable var31) {
                  System.err.println("[Prismatic] surface draw disabled: " + var31.getMessage());
                  var31.printStackTrace();
                  this.colorMeasure = true;
                  var26 = false;
                  var30 = false;
               } finally {
                  if (var30) {
                     GL20.glUseProgram(0);
                     OpenGlStateSnapshot.compute(var24);
                  }
               }

               GL20.glUseProgram(0);
               OpenGlStateSnapshot.compute(var24);
               return var26;
            }

            GL20.glUseProgram(0);
            OpenGlStateSnapshot.compute(var24);
            return var25;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private float handle(float var1, float var2) {
      long var3 = System.nanoTime();
      if (var3 - this.scaleSave > 3000000L) {
         float var5 = var1 - this.profileInvoke;
         float var6 = var2 - this.sourceSchedule;
         float var7 = (float)Math.sqrt(var5 * var5 + var6 * var6);
         this.timerRender = this.timerRender * 0.55F + Math.min(1.0F, var7 / 36.0F) * 0.45F;
         if (this.timerRender < 8.0E-4F) {
            this.timerRender = 0.0F;
         }

         this.profileInvoke = var1;
         this.sourceSchedule = var2;
         this.scaleSave = var3;
      }

      return this.timerRender;
   }

   private int handle(int var1, int var2) {
      long var3 = System.nanoTime();
      if (this.animationSchedule > 0 && this.rendererScan == var1 && this.sourceBuild == var2 && var3 - this.outputCollapse < 3000000L) {
         return this.animationSchedule;
      }

      try {
         ShaderRenderer var5 = WildClient.compute();
         if (var5 == null) {
            this.animationSchedule = 0;
            return 0;
         } else {
            ShaderRenderer.Bounds var6 = var5.resolve();
            if (var6 != null && var6.colorTexture() > 0 && var6.width() > 0 && var6.height() > 0) {
               this.animationSchedule = var6.colorTexture();
               this.rendererScan = var6.width();
               this.sourceBuild = var6.height();
               this.outputCollapse = var3;
               return this.animationSchedule;
            } else {
               this.animationSchedule = 0;
               return 0;
            }
         }
      } catch (Throwable var7) {
         this.animationSchedule = 0;
         return 0;
      }
   }

   private boolean handle() {
      if (!this.presetWrite) {
         this.presetWrite = true;

         try {
            this.mode = ShaderBuildReporter.handle("assets/wild/shaders/blur/blur_fullscreen.vert", "assets/wild/shaders/hud/prismatic_edge.frag");
            this.renderer = this.mode.handle("uResolution");
            this.handler = this.mode.handle("uTime");
            this.animationDraw = this.mode.handle("uDrawRect");
            this.pointEncode = this.mode.handle("uElementRect");
            this.animator = this.mode.handle("uRadius");
            this.source = this.mode.handle("uAlpha");
            this.target = this.mode.handle("uInset");
            this.pending = this.mode.handle("uSurfaceColor");
            this.previous = this.mode.handle("uOutlineColor");
            this.latest = this.mode.handle("uAccentTop");
            this.summary = this.mode.handle("uAccentBottom");
            this.matrixBlend = this.mode.handle("uMouse");
            this.vectorMatch = this.mode.handle("uShadow");
            this.itemProject = this.mode.handle("uOutline");
            this.responseCompute = this.mode.handle("uLightMode");
            this.providerFetch = this.mode.handle("uPad");
            this.profileDraw = this.mode.handle("uSweepSpeed");
            this.vectorPerform = this.mode.handle("uScene");
            this.eventAttach = this.mode.handle("uSceneSize");
            this.serverRead = this.mode.handle("uHasScene");
            this.positionAdvance = this.mode.handle("uMouseVel");
            this.frameCheck = this.mode.handle("uIor");
            this.moduleCollect = this.mode.handle("uDispersion");
            this.providerClose = this.mode.handle("uDecay");
            this.presetSave = this.mode.handle("uCausticGain");
            this.windowConvert = this.mode.handle("uGlintGain");
            this.selection = GL30.glGenVertexArrays();
            this.enabled = GL15.glGenBuffers();
            GL30.glBindVertexArray(this.selection);
            GL15.glBindBuffer(34962, this.enabled);
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
            System.err.println("[Prismatic] surface shader failed to load (style will fall back): " + var3.getMessage());
            var3.printStackTrace();
            this.colorMeasure = true;
            this.mode = null;
            return false;
         }
      } else {
         return this.mode != null && this.selection != 0;
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
