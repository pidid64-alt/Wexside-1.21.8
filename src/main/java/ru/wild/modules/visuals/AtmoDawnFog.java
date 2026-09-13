package ru.wild.modules.visuals;

import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import java.nio.FloatBuffer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.render.Camera;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.client.util.Window;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.wild.module.api.Module;
import org.wild.module.api.ModuleRegister;
import ru.wild.WildClient;
import ru.wild.api.event.EventHandler;
import ru.wild.api.event.WorldRenderContext;
import ru.wild.api.module.ModuleCategory;
import ru.wild.api.setting.BooleanSetting;
import ru.wild.api.setting.ModeSetting;
import ru.wild.api.setting.NumberSetting;
import ru.wild.api.setting.WorldColorSetting;
import ru.wild.gui.theme.ThemePalette;
import ru.wild.render.OpenGlStateSnapshot;
import ru.wild.render.VertexLayoutBinding;
import ru.wild.render.shader.ShaderBuildReporter;
import ru.wild.render.shader.ShaderViewportTracker;

@ModuleRegister(name = "AtmoDawnFog", description = "Кинематографичная атмосфера: туман, лучи света, заря", category = ModuleCategory.Visuals)
public class AtmoDawnFog extends Module {
   private static final String responseCompute = "assets/wild/shaders/world/world_volume.vert";
   private static final String providerFetch = "assets/wild/shaders/dawnfog/world_fog_fresnel.frag";
   private static final String profileDraw = "Рассвет";
   private static final String vectorPerform = "Сумерки";
   private static final String eventAttach = "Тема";
   private static final float serverRead = 18.0F;
   private static final float positionAdvance = 1.0E-4F;
   private static final int frameCheck = 13203624;
   private static final int moduleCollect = 8230143;
   public final ModeSetting source = new ModeSetting("Режим", "Рассвет", "Рассвет", "Сумерки", "Тема");
   public final NumberSetting target = new NumberSetting("Плотность", 0.35F, 0.05F, 0.8F, 0.01F, false);
   public final NumberSetting pending = new NumberSetting("Высота рассеивания", 76.0F, 60.0F, 120.0F, 1.0F, false);
   public final NumberSetting previous = new NumberSetting("Лучи света", 0.75F, 0.0F, 1.0F, 0.01F, true);
   public final NumberSetting latest = new NumberSetting("Мягкость", 0.6F, 0.0F, 1.0F, 0.01F, true);
   public final BooleanSetting summary = new BooleanSetting("Радуга", true);
   public final NumberSetting matrixBlend = new NumberSetting("Яркость радуги", 0.55F, 0.1F, 1.0F, 0.01F, true).handle(() -> !this.summary.compute());
   public final NumberSetting vectorMatch = new NumberSetting("Размер радуги", 54.0F, 46.0F, 60.0F, 0.5F, false).handle(() -> !this.summary.compute());
   public final WorldColorSetting itemProject = new WorldColorSetting("Цвет зари", new Color(255, 173, 122)).handle(() -> !this.source.process("Рассвет"));
   private final Matrix4f providerClose = new Matrix4f();
   private final Matrix4f presetSave = new Matrix4f();
   private final Matrix4f windowConvert = new Matrix4f();
   private final Matrix4f presetWrite = new Matrix4f();
   private final Vector4f colorMeasure = new Vector4f();
   private final FloatBuffer animationSchedule = BufferUtils.createFloatBuffer(16);
   private ShaderBuildReporter rendererScan;
   private int sourceBuild = -1;
   private int outputCollapse = -1;
   private int profileInvoke = -1;
   private int sourceSchedule = -1;
   private int timerRender = -1;
   private int scaleSave = -1;
   private int colorCompute = -1;
   private int scaleAdapt = -1;
   private int textureRun = -1;
   private int indexBind = -1;
   private int actionRead = -1;
   private int configCollapse = -1;
   private int dataValidate = -1;
   private int scaleRender = -1;
   private int clientRefresh = -1;
   private int keyFilter = -1;
   private int requestAdapt = -1;
   private int timerMeasure = -1;
   private int vectorEncode = -1;
   private int requestReceive = -1;
   private int windowProcess = -1;
   private int packetSave = -1;
   private int entryAnimate = -1;
   private int playerCollect = -1;
   private int stateApply;
   private int matrixFilter;
   private int layerSample;
   private int worldSend;
   private int targetWrite;
   private int resultEncode;
   private int messageParse;
   private int providerRead;
   private boolean matrixBlend2;
   private boolean scalePerform;
   private float contextExpand = 0.5F;
   private float keyProcess = 0.5F;
   private float actionConvert;
   private float screenRead;
   private float animationExpand = -0.39F;
   private final float[] playerRun = new float[18];

   public AtmoDawnFog() {
      this.handle(this.source, this.target, this.pending, this.previous, this.latest, this.summary, this.matrixBlend, this.vectorMatch, this.itemProject);
   }

   public static boolean refresh() {
      if (WildClient.instance != null && WildClient.instance.data != null) {
         AtmoDawnFog var0 = WildClient.instance.data.handle(AtmoDawnFog.class);
         return var0 != null && var0.enabled && !var0.scalePerform;
      } else {
         return false;
      }
   }

   @Override
   public void process() {
      super.process();
      this.encodePoint();
   }
   @EventHandler(handle = 1)
   public void handle(WorldRenderContext var1) {
      if (this.enabled && !this.scalePerform && var1 != null) {
         if (RenderSystem.isOnRenderThread()) {
            MinecraftClient var2 = var1.compute();
            if (var2 != null && var2.world != null && var2.player != null && var1.update() != null) {
               Camera var3 = var1.update().handle();
               if (var3 != null) {
                  Window var4 = var2.getWindow();
                  if (var4 != null && !var4.hasZeroWidthOrHeight()) {
                     int var5 = var4.getFramebufferWidth();
                     int var6 = var4.getFramebufferHeight();
                     if (var5 > 1 && var6 > 1) {
                        Framebuffer var7 = var2.getFramebuffer();
                        if (var7 != null) {
                           int var8 = handle(var7.getColorAttachment());
                           int var9 = handle(var7.getDepthAttachment());
                           if (var8 > 0 && var9 > 0) {
                              if (!(this.target.compute() <= 1.0E-4F)) {
                                 float var10 = var1.check();
                                 float var11 = ((float)(var2.world.getTime() % 100000L) + var10) * 0.05F;
                                 float var12 = var2.world.getSkyAngleRadians(var10);
                                 float var13 = -((float)Math.sin(var12));
                                 int var14 = this.render();
                                 float var15 = this.handle(var14);
                                 float var16 = var13 >= 0.0F ? 1.0F : -1.0F;
                                 float var17 = var16 * (float)Math.cos(var15);
                                 float var18 = (float)Math.sin(var15);
                                 float var19 = 0.3F;
                                 this.screenRead = -var16 * (float)Math.cos(var19);
                                 this.animationExpand = -((float)Math.sin(var19));
                                 int var20 = this.process(var14);
                                 float var21 = 192.0F;
                                 if (var2.options != null) {
                                    var21 = ((Integer)var2.options.getViewDistance().getValue()).intValue() * 16.0F;
                                 }

                                 OpenGlStateSnapshot.NetworkState var22 = OpenGlStateSnapshot.handle();
                                 boolean var27 = false /* VF: Semaphore variable */;

                                 label220: {
                                    label207: {
                                       label221: {
                                          try {
                                             var27 = true;
                                             this.drawAnimation();
                                             if (!this.scalePerform) {
                                                if (this.process(var5, var6)) {
                                                   if (!this.handle(var8, var5, var6)) {
                                                      var27 = false;
                                                      break label220;
                                                   }

                                                   Vec3d var23 = var3.getPos();
                                                   this.windowConvert.set(var1.execute());
                                                   this.presetWrite.set(var1.prepare());
                                                   this.handle(var17, var18);
                                                   this.providerClose.set(this.presetWrite).invert();
                                                   this.presetSave.set(this.windowConvert).invert();
                                                   this.presetSave.m30((float)var23.x);
                                                   this.presetSave.m31((float)var23.y);
                                                   this.presetSave.m32((float)var23.z);
                                                   this.handle(var8, var9, var5, var6, var23, var11, var17, var18, var14, var20, var21);
                                                   var27 = false;
                                                   break label207;
                                                }

                                                var27 = false;
                                             } else {
                                                var27 = false;
                                             }
                                             break label221;
                                          } catch (Throwable var28) {
                                             this.scalePerform = true;
                                             System.err.println("[AtmoDawnFog] renderer disabled: " + var28.getMessage());
                                             var28.printStackTrace();
                                             var27 = false;
                                          } finally {
                                             if (var27) {
                                                if (this.resultEncode != 0) {
                                                   GL30.glBindFramebuffer(36160, this.resultEncode);
                                                   GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
                                                }

                                                GL13.glActiveTexture(33985);
                                                GL11.glBindTexture(3553, 0);
                                                GL13.glActiveTexture(33984);
                                                GL11.glBindTexture(3553, 0);
                                                GL20.glUseProgram(0);
                                                OpenGlStateSnapshot.compute(var22);
                                             }
                                          }

                                          if (this.resultEncode != 0) {
                                             GL30.glBindFramebuffer(36160, this.resultEncode);
                                             GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
                                          }

                                          GL13.glActiveTexture(33985);
                                          GL11.glBindTexture(3553, 0);
                                          GL13.glActiveTexture(33984);
                                          GL11.glBindTexture(3553, 0);
                                          GL20.glUseProgram(0);
                                          OpenGlStateSnapshot.compute(var22);
                                          return;
                                       }

                                       if (this.resultEncode != 0) {
                                          GL30.glBindFramebuffer(36160, this.resultEncode);
                                          GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
                                       }

                                       GL13.glActiveTexture(33985);
                                       GL11.glBindTexture(3553, 0);
                                       GL13.glActiveTexture(33984);
                                       GL11.glBindTexture(3553, 0);
                                       GL20.glUseProgram(0);
                                       OpenGlStateSnapshot.compute(var22);
                                       return;
                                    }

                                    if (this.resultEncode != 0) {
                                       GL30.glBindFramebuffer(36160, this.resultEncode);
                                       GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
                                    }

                                    GL13.glActiveTexture(33985);
                                    GL11.glBindTexture(3553, 0);
                                    GL13.glActiveTexture(33984);
                                    GL11.glBindTexture(3553, 0);
                                    GL20.glUseProgram(0);
                                    OpenGlStateSnapshot.compute(var22);
                                    return;
                                 }

                                 if (this.resultEncode != 0) {
                                    GL30.glBindFramebuffer(36160, this.resultEncode);
                                    GL30.glFramebufferTexture2D(36160, 36064, 3553, 0, 0);
                                 }

                                 GL13.glActiveTexture(33985);
                                 GL11.glBindTexture(3553, 0);
                                 GL13.glActiveTexture(33984);
                                 GL11.glBindTexture(3553, 0);
                                 GL20.glUseProgram(0);
                                 OpenGlStateSnapshot.compute(var22);
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void handle(int var1, int var2, int var3, int var4, Vec3d var5, float var6, float var7, float var8, int var9, int var10, float var11) {
      GL30.glBindFramebuffer(36160, this.resultEncode);
      GL30.glFramebufferTexture2D(36160, 36064, 3553, var1, 0);
      GL11.glDrawBuffer(36064);
      if (GL30.glCheckFramebufferStatus(36160) == 36053) {
         GL11.glViewport(0, 0, var3, var4);
         GL11.glDisable(3089);
         GL11.glDisable(2929);
         GL11.glDisable(2884);
         GL11.glDisable(3042);
         GL11.glDisable(36281);
         GL11.glColorMask(true, true, true, true);
         GL11.glDepthMask(false);
         this.rendererScan.handle();
         float var12 = this.pending.compute();
         float var13 = var12 - 18.0F;
         this.handle(var9, var10);
         if (this.sourceBuild >= 0) {
            GL20.glUniform1i(this.sourceBuild, 0);
         }

         if (this.outputCollapse >= 0) {
            GL20.glUniform1i(this.outputCollapse, 1);
         }

         if (this.profileInvoke >= 0) {
            GL20.glUniform2f(this.profileInvoke, var3, var4);
         }

         if (this.sourceSchedule >= 0) {
            GL20.glUniform1f(this.sourceSchedule, var6);
         }

         if (this.timerRender >= 0) {
            GL20.glUniform3f(this.timerRender, (float)var5.x, (float)var5.y, (float)var5.z);
         }

         if (this.scaleSave >= 0) {
            this.handle(this.scaleSave, this.providerClose);
         }

         if (this.colorCompute >= 0) {
            this.handle(this.colorCompute, this.presetSave);
         }

         if (this.scaleAdapt >= 0) {
            GL20.glUniform3f(this.scaleAdapt, var7, var8, 0.0F);
         }

         if (this.textureRun >= 0) {
            GL20.glUniform3f(this.textureRun, this.contextExpand, this.keyProcess, this.actionConvert);
         }

         if (this.indexBind >= 0) {
            GL20.glUniform1f(this.indexBind, handle(this.target.compute(), 0.05F, 0.8F));
         }

         if (this.actionRead >= 0) {
            GL20.glUniform1f(this.actionRead, var13);
         }

         if (this.configCollapse >= 0) {
            GL20.glUniform1f(this.configCollapse, var12);
         }

         if (this.dataValidate >= 0) {
            GL20.glUniform1f(this.dataValidate, var11);
         }

         if (this.scaleRender >= 0) {
            GL20.glUniform3f(this.scaleRender, this.playerRun[0], this.playerRun[1], this.playerRun[2]);
         }

         if (this.clientRefresh >= 0) {
            GL20.glUniform3f(this.clientRefresh, this.playerRun[3], this.playerRun[4], this.playerRun[5]);
         }

         if (this.keyFilter >= 0) {
            GL20.glUniform3f(this.keyFilter, this.playerRun[6], this.playerRun[7], this.playerRun[8]);
         }

         if (this.requestAdapt >= 0) {
            GL20.glUniform3f(this.requestAdapt, this.playerRun[9], this.playerRun[10], this.playerRun[11]);
         }

         if (this.timerMeasure >= 0) {
            GL20.glUniform3f(this.timerMeasure, this.playerRun[12], this.playerRun[13], this.playerRun[14]);
         }

         if (this.vectorEncode >= 0) {
            GL20.glUniform3f(this.vectorEncode, this.playerRun[15], this.playerRun[16], this.playerRun[17]);
         }

         if (this.requestReceive >= 0) {
            GL20.glUniform1f(this.requestReceive, this.summary.compute() ? handle(this.matrixBlend.compute(), 0.0F, 1.0F) : 0.0F);
         }

         if (this.windowProcess >= 0) {
            GL20.glUniform3f(this.windowProcess, this.screenRead, this.animationExpand, 0.0F);
         }

         if (this.packetSave >= 0) {
            GL20.glUniform1f(this.packetSave, handle(this.vectorMatch.compute(), 40.0F, 64.0F));
         }

         if (this.entryAnimate >= 0) {
            GL20.glUniform1f(this.entryAnimate, handle(this.previous.compute(), 0.0F, 1.0F));
         }

         if (this.playerCollect >= 0) {
            GL20.glUniform1f(this.playerCollect, handle(this.latest.compute(), 0.0F, 1.0F));
         }

         GL13.glActiveTexture(33984);
         GL11.glBindTexture(3553, this.matrixFilter);
         GL13.glActiveTexture(33985);
         GL11.glBindTexture(3553, var2);
         GL13.glActiveTexture(33984);
         GL30.glBindVertexArray(this.messageParse);
         ShaderViewportTracker.handle().handle(2);
         GL11.glDrawArrays(4, 0, 6);
         GL30.glBindVertexArray(0);
      }
   }

   private void handle(float var1, float var2) {
      this.contextExpand = 0.5F;
      this.keyProcess = 0.5F;
      this.actionConvert = 0.0F;
      this.colorMeasure.set(var1, var2, 0.0F, 0.0F);
      this.windowConvert.transform(this.colorMeasure);
      float var3 = this.colorMeasure.x;
      float var4 = this.colorMeasure.y;
      float var5 = this.colorMeasure.z;
      float var6 = -var5;
      if (!(var6 <= 1.0E-4F)) {
         this.colorMeasure.set(var3 * 1000.0F, var4 * 1000.0F, var5 * 1000.0F, 1.0F);
         this.presetWrite.transform(this.colorMeasure);
         if (!(this.colorMeasure.w <= 1.0E-4F)) {
            this.contextExpand = this.colorMeasure.x / this.colorMeasure.w * 0.5F + 0.5F;
            this.keyProcess = this.colorMeasure.y / this.colorMeasure.w * 0.5F + 0.5F;
            this.actionConvert = handle(var6 * 4.0F, 0.0F, 1.0F);
         }
      }
   }

   private void handle(int var1, int var2) {
      Color var3 = this.itemProject.compute();
      float var4 = var3.getRed() / 255.0F;
      float var5 = var3.getGreen() / 255.0F;
      float var6 = var3.getBlue() / 255.0F;
      float var7 = (var2 >> 16 & 0xFF) / 255.0F;
      float var8 = (var2 >> 8 & 0xFF) / 255.0F;
      float var9 = (var2 & 0xFF) / 255.0F;
      if (var1 == 1) {
         handle(this.playerRun, 0, 0.135F, 0.125F, 0.3F);
         handle(this.playerRun, 3, 0.89F, 0.46F, 0.55F);
         handle(this.playerRun, 6, 0.38F, 0.35F, 0.56F);
         handle(this.playerRun, 9, 0.8F, 0.52F, 0.62F);
         handle(this.playerRun, 12, 0.47F, 0.44F, 0.64F);
         handle(this.playerRun, 15, 0.92F, 0.56F, 0.72F);
      } else if (var1 == 2) {
         handle(this.playerRun, 0, 0.085F, 0.1F, 0.2F, var7, var8, var9, 0.3F);
         handle(this.playerRun, 3, var7, var8, var9, 1.0F, 0.93F, 0.82F, 0.35F);
         handle(this.playerRun, 6, 0.52F, 0.58F, 0.74F, var7, var8, var9, 0.28F);
         handle(this.playerRun, 9, var7, var8, var9, 0.97F, 0.93F, 0.88F, 0.45F);
         handle(this.playerRun, 12, 0.58F, 0.63F, 0.76F, var7, var8, var9, 0.35F);
         handle(this.playerRun, 15, var7, var8, var9, 1.0F, 0.96F, 0.88F, 0.3F);
      } else {
         handle(this.playerRun, 0, 0.16F, 0.19F, 0.38F, var4, var5, var6, 0.14F);
         handle(this.playerRun, 3, handle(var4 * 1.12F, 0.0F, 1.0F), handle(var5 * 0.88F, 0.0F, 1.0F), handle(var6 * 0.62F, 0.0F, 1.0F));
         handle(this.playerRun, 6, 0.56F, 0.62F, 0.8F);
         handle(this.playerRun, 9, var4, var5, var6, 0.95F, 0.55F, 0.63F, 0.42F);
         handle(this.playerRun, 12, 0.6F, 0.67F, 0.82F);
         handle(this.playerRun, 15, handle(var4 * 1.08F, 0.0F, 1.0F), handle(var5 * 0.94F, 0.0F, 1.0F), handle(var6 * 0.72F, 0.0F, 1.0F));
      }
   }

   private static void handle(float[] var0, int var1, float var2, float var3, float var4) {
      var0[var1] = var2;
      var0[var1 + 1] = var3;
      var0[var1 + 2] = var4;
   }

   private static void handle(float[] var0, int var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      float var9 = handle(var8, 0.0F, 1.0F);
      float var10 = handle(var2);
      float var11 = handle(var3);
      float var12 = handle(var4);
      float var13 = handle(var5);
      float var14 = handle(var6);
      float var15 = handle(var7);
      float var16 = (float)Math.cbrt(0.41222146F * var10 + 0.53633255F * var11 + 0.051445995F * var12);
      float var17 = (float)Math.cbrt(0.2119035F * var10 + 0.6806995F * var11 + 0.10739696F * var12);
      float var18 = (float)Math.cbrt(0.08830246F * var10 + 0.28171885F * var11 + 0.6299787F * var12);
      float var19 = (float)Math.cbrt(0.41222146F * var13 + 0.53633255F * var14 + 0.051445995F * var15);
      float var20 = (float)Math.cbrt(0.2119035F * var13 + 0.6806995F * var14 + 0.10739696F * var15);
      float var21 = (float)Math.cbrt(0.08830246F * var13 + 0.28171885F * var14 + 0.6299787F * var15);
      float var22 = var16 + (var19 - var16) * var9;
      float var23 = var17 + (var20 - var17) * var9;
      float var24 = var18 + (var21 - var18) * var9;
      float var25 = var22 * var22 * var22;
      float var26 = var23 * var23 * var23;
      float var27 = var24 * var24 * var24;
      float var28 = 4.0767417F * var25 - 3.3077116F * var26 + 0.23096994F * var27;
      float var29 = -1.268438F * var25 + 2.6097574F * var26 - 0.34131938F * var27;
      float var30 = -0.0041960864F * var25 - 0.7034186F * var26 + 1.7076147F * var27;
      var0[var1] = process(var28);
      var0[var1 + 1] = process(var29);
      var0[var1 + 2] = process(var30);
   }

   private static float handle(float var0) {
      return var0 <= 0.04045F ? var0 / 12.92F : (float)Math.pow((var0 + 0.055F) / 1.055F, 2.4);
   }

   private static float process(float var0) {
      var0 = handle(var0, 0.0F, 1.0F);
      return var0 <= 0.0031308F ? var0 * 12.92F : (float)(1.055 * Math.pow(var0, 0.4166666666666667) - 0.055);
   }

   private int render() {
      if (this.source.process("Сумерки")) {
         return 1;
      } else {
         return this.source.process("Тема") ? 2 : 0;
      }
   }

   private float handle(int var1) {
      if (var1 == 1) {
         return -0.045F;
      } else {
         return var1 == 2 ? 0.13F : 0.11F;
      }
   }

   private int process(int var1) {
      if (var1 == 1) {
         return 13203624;
      }

      if (var1 == 2) {
         try {
            if (WildClient.instance != null && WildClient.instance.selection != null) {
               ThemePalette var2 = WildClient.instance.selection.process();
               if (var2 == ThemePalette.CUSTOM && WildClient.instance.selection.data != null) {
                  return WildClient.instance.selection.data.prepare() & 16777215;
               }

               if (var2 != null && var2.handle() != null) {
                  return var2.handle().getRGB() & 16777215;
               }
            }
         } catch (Throwable var3) {
         }

         return 8230143;
      } else {
         return this.itemProject.prepare() & 16777215;
      }
   }

   private boolean handle(int var1, int var2, int var3) {
      if (var1 > 0 && this.stateApply != 0) {
         if (this.targetWrite == 0) {
            this.targetWrite = GL30.glGenFramebuffers();
         }

         GL11.glDisable(3089);
         GL11.glDisable(3042);
         GL11.glDisable(2884);
         GL11.glDisable(2929);
         GL11.glDisable(36281);
         GL30.glBindFramebuffer(36008, this.targetWrite);
         GL30.glFramebufferTexture2D(36008, 36064, 3553, var1, 0);
         if (GL30.glCheckFramebufferStatus(36008) != 36053) {
            GL30.glFramebufferTexture2D(36008, 36064, 3553, 0, 0);
            return false;
         } else {
            GL30.glBindFramebuffer(36009, this.stateApply);
            GL11.glReadBuffer(36064);
            GL11.glDrawBuffer(36064);
            GL30.glBlitFramebuffer(0, 0, var2, var3, 0, 0, var2, var3, 16384, 9728);
            GL30.glBindFramebuffer(36008, this.targetWrite);
            GL30.glFramebufferTexture2D(36008, 36064, 3553, 0, 0);
            return true;
         }
      } else {
         return false;
      }
   }

   private boolean process(int var1, int var2) {
      if (var1 > 0 && var2 > 0) {
         if (this.matrixFilter != 0 && (this.layerSample != var1 || this.worldSend != var2 || this.stateApply == 0)) {
            this.tick();
         }

         if (this.matrixFilter == 0) {
            this.matrixFilter = GL11.glGenTextures();
            GL11.glBindTexture(3553, this.matrixFilter);
            GL11.glTexParameteri(3553, 10241, 9729);
            GL11.glTexParameteri(3553, 10240, 9729);
            GL11.glTexParameteri(3553, 10242, 33071);
            GL11.glTexParameteri(3553, 10243, 33071);
            VertexLayoutBinding.handle(32856, var1, var2, 6408, 5121);
            this.stateApply = GL30.glGenFramebuffers();
            GL30.glBindFramebuffer(36160, this.stateApply);
            GL30.glFramebufferTexture2D(36160, 36064, 3553, this.matrixFilter, 0);
            GL11.glDrawBuffer(36064);
            if (GL30.glCheckFramebufferStatus(36160) != 36053) {
               this.tick();
               return false;
            }
         }

         this.layerSample = var1;
         this.worldSend = var2;
         return true;
      } else {
         return false;
      }
   }

   private void tick() {
      if (this.stateApply != 0) {
         GL30.glDeleteFramebuffers(this.stateApply);
         this.stateApply = 0;
      }

      if (this.matrixFilter != 0) {
         GL11.glDeleteTextures(this.matrixFilter);
         this.matrixFilter = 0;
      }

      this.layerSample = 0;
      this.worldSend = 0;
   }

   private void drawAnimation() {
      if (!this.matrixBlend2) {
         this.rendererScan = ShaderBuildReporter.handle("assets/wild/shaders/world/world_volume.vert", "assets/wild/shaders/dawnfog/world_fog_fresnel.frag");
         this.messageParse = GL30.glGenVertexArrays();
         this.providerRead = GL15.glGenBuffers();
         GL30.glBindVertexArray(this.messageParse);
         GL15.glBindBuffer(34962, this.providerRead);
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
         if (this.resultEncode == 0) {
            this.resultEncode = GL30.glGenFramebuffers();
         }

         this.sourceBuild = this.rendererScan.handle("u_ScreenTexture");
         this.outputCollapse = this.rendererScan.handle("u_DepthTexture");
         this.profileInvoke = this.rendererScan.handle("u_Resolution");
         this.sourceSchedule = this.rendererScan.handle("u_Time");
         this.timerRender = this.rendererScan.handle("u_CameraPos");
         this.scaleSave = this.rendererScan.handle("u_InverseProjectionMatrix");
         this.colorCompute = this.rendererScan.handle("u_InverseViewMatrix");
         this.scaleAdapt = this.rendererScan.handle("u_SunDirection");
         this.textureRun = this.rendererScan.handle("u_SunScreen");
         this.indexBind = this.rendererScan.handle("u_FogDensity");
         this.actionRead = this.rendererScan.handle("u_FogMinHeight");
         this.configCollapse = this.rendererScan.handle("u_FogMaxHeight");
         this.dataValidate = this.rendererScan.handle("u_ViewDistance");
         this.scaleRender = this.rendererScan.handle("u_PaletteZenith");
         this.clientRefresh = this.rendererScan.handle("u_PaletteHorizonWarm");
         this.keyFilter = this.rendererScan.handle("u_PaletteHorizonCool");
         this.requestAdapt = this.rendererScan.handle("u_PaletteFogWarm");
         this.timerMeasure = this.rendererScan.handle("u_PaletteFogCool");
         this.vectorEncode = this.rendererScan.handle("u_PaletteRay");
         this.requestReceive = this.rendererScan.handle("u_Rainbow");
         this.windowProcess = this.rendererScan.handle("u_RainbowDir");
         this.packetSave = this.rendererScan.handle("u_RainbowSize");
         this.entryAnimate = this.rendererScan.handle("u_GodRays");
         this.playerCollect = this.rendererScan.handle("u_Softness");
         this.matrixBlend2 = true;
      }
   }

   private void encodePoint() {
      if (RenderSystem.isOnRenderThread()) {
         this.tick();
         if (this.targetWrite != 0) {
            GL30.glDeleteFramebuffers(this.targetWrite);
            this.targetWrite = 0;
         }

         if (this.resultEncode != 0) {
            GL30.glDeleteFramebuffers(this.resultEncode);
            this.resultEncode = 0;
         }

         if (this.messageParse != 0) {
            GL30.glDeleteVertexArrays(this.messageParse);
            this.messageParse = 0;
         }

         if (this.providerRead != 0) {
            GL15.glDeleteBuffers(this.providerRead);
            this.providerRead = 0;
         }

         if (this.rendererScan != null) {
            this.rendererScan.process();
            this.rendererScan = null;
         }

         this.matrixBlend2 = false;
         this.scalePerform = false;
      }
   }

   private void handle(int var1, Matrix4f var2) {
      this.animationSchedule.clear();
      var2.get(this.animationSchedule);
      GL20.glUniformMatrix4fv(var1, false, this.animationSchedule);
   }

   private static int handle(Object var0) {
      return var0 instanceof GlTexture var1 ? var1.getGlId() : 0;
   }

   private static float handle(float var0, float var1, float var2) {
      return !Float.isFinite(var0) ? var1 : Math.max(var1, Math.min(var2, var0));
   }
}
