package ru.wild.render;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.texture.GlTexture;
import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import ru.wild.gui.screen.WildMainMenuScreen;
import ru.wild.render.font.FontRegistry;
import ru.wild.render.font.SdfTextRenderer;
import ru.wild.render.shader.ShaderFailureCache;
import ru.wild.render.texture.OffscreenRenderTarget;
import ru.wild.util.render.RoundedRectRenderer;

public final class MainMenuBackgroundRenderer implements AutoCloseable {
   public static final int instance = 14;
   private static final String data = "assets/wild/shaders/mainmenu/menu_quad.vert";
   private static final String[] context = resolve();
   private static final float config = 12.0F;
   private static final float state = 30.0F;
   private static final float cache = 0.22F;
   private static final float output = 0.17F;
   private static final float current = 0.62F;
   private static final float active = 3.0F;
   private static final float mode = 0.005F;
   private static final float selection = -0.62F;
   private static final float enabled = (float) (Math.PI * 2);
   private static final float renderer = 0.85F;
   private static final float handler = 0.3F;
   private static final float animationDraw = 0.52F;
   private static final float pointEncode = 0.004F;
   private static final float animator = 0.7139F;
   private static final float source = 24.0F;
   private static final float target = 0.6F;
   private static final float pending = 0.6F;
   private static final float previous = 1.0F;
   private static final float latest = 0.42F;
   private static final float summary = 7.0F;
   private static final float matrixBlend = 2.4F;
   private static final float vectorMatch = 2.2F;
   private static final float itemProject = 0.8F;
   private static final float responseCompute = 0.33F;
   private static final float providerFetch = 38.0F;
   private static final float profileDraw = 16.0F;
   private static final float vectorPerform = 9.0F;
   private static final float eventAttach = 3.0F;
   private static final float serverRead = 0.48F;
   private static final float positionAdvance = 3.4F;
   private static final float frameCheck = 6.0F;
   private final ShaderFailureCache moduleCollect = new ShaderFailureCache();
   private final OpenGlStateSnapshot.NetworkState providerClose = new OpenGlStateSnapshot.NetworkState();
   private final OpenGlStateSnapshot.NetworkState presetSave = new OpenGlStateSnapshot.NetworkState();
   private final OffscreenRenderTarget windowConvert = new OffscreenRenderTarget();
   private final OffscreenRenderTarget presetWrite = new OffscreenRenderTarget();
   private final OffscreenRenderTarget colorMeasure = new OffscreenRenderTarget();
   private final OffscreenRenderTarget animationSchedule = new OffscreenRenderTarget();
   private final OffscreenRenderTarget rendererScan = new OffscreenRenderTarget();
   private final OffscreenRenderTarget sourceBuild = new OffscreenRenderTarget();
   private final OffscreenRenderTarget outputCollapse = new OffscreenRenderTarget();
   private final OffscreenRenderTarget profileInvoke = new OffscreenRenderTarget();
   private final OffscreenRenderTarget sourceSchedule = new OffscreenRenderTarget();
   private final OffscreenRenderTarget timerRender = new OffscreenRenderTarget();
   private final OffscreenRenderTarget scaleSave = new OffscreenRenderTarget();
   private final OffscreenRenderTarget colorCompute = new OffscreenRenderTarget();
   private final OffscreenRenderTarget scaleAdapt = new OffscreenRenderTarget();
   private final OffscreenRenderTarget textureRun = new OffscreenRenderTarget();
   private ShaderFailureCache.ShaderState indexBind;
   private ShaderFailureCache.ShaderState actionRead;
   private float[] configCollapse = new float[8];
   private int dataValidate;
   private int scaleRender;
   private int clientRefresh;
   private int keyFilter;
   private float requestAdapt;
   private float timerMeasure;
   private float vectorEncode;
   private float requestReceive;
   private boolean windowProcess;
   private ShaderFailureCache.ShaderState packetSave;
   private ShaderFailureCache.ShaderState entryAnimate;
   private ShaderFailureCache.ShaderState playerCollect;
   private ShaderFailureCache.ShaderState stateApply;
   private ShaderFailureCache.ShaderState matrixFilter;
   private ShaderFailureCache.ShaderState layerSample;
   private ShaderFailureCache.ShaderState worldSend;
   private ShaderFailureCache.ShaderState targetWrite;
   private ShaderFailureCache.ShaderState resultEncode;
   private ShaderFailureCache.ShaderState messageParse;
   private float providerRead;
   private float matrixBlend2;
   private float scalePerform = 1.0F;
   private float contextExpand;
   private boolean keyProcess;
   private ShaderFailureCache.ShaderState actionConvert;
   private ShaderFailureCache.ShaderState screenRead;
   private ShaderFailureCache.ShaderState animationExpand;
   private ShaderFailureCache.ShaderState playerRun;
   private ShaderFailureCache.ShaderState matrixRender;
   private ShaderFailureCache.ShaderState moduleTick;
   private ShaderFailureCache.ShaderState playerCollapse;
   private ShaderFailureCache.ShaderState optionAdvance;
   private VertexArrayBuffer effectScan;
   private int optionParse;
   private int pointSubmit;
   private int listenerPerform;
   private int configMatch;
   private int actionRender;
   private int playerApply;
   private int bufferAdapt;
   private int playerUpdate;
   private float packetRead;
   private float rendererCancel;
   private float eventReceive;
   private float screenSubmit;
   private float cacheHandle;
   private float rangeRelease;
   private float indexSave;
   private float indexCheck;
   private int settingSchedule;
   private int inputAcquire;
   private float listenerRun = -1.0F;
   private float indexLoad;
   private float layoutSave;
   private float blockRun = 12.0F;
   private float playerEvaluate;
   private float outputFetch;
   private float scaleParse;
   private float sessionEncode;
   private float elementTick;
   private float regionAlign;
   private boolean resourceClamp;
   private boolean handlerRun;
   private boolean keyCheck;
   private boolean layerProject;
   private int entityFilter = -1;
   private int layerSample2 = -1;
   public boolean handle(WildMainMenuScreen.PrimaryNetworkState var1) {
      if (this.keyCheck) {
         return false;
      }

      if (var1 != null && var1.onTick() > 0 && var1.select() > 0) {
         long var2 = handle(MinecraftClient.getInstance(), var1.onTick(), var1.select());
         if (var2 >= 0L && handle(var2) == var1.onTick() && process(var2) == var1.select()) {
            OpenGlStateSnapshot.process(this.providerClose);
            boolean var4 = false;
            boolean var12 = false /* VF: Semaphore variable */;

            int var15;
            label142: {
               boolean var17;
               label141: {
                  label140: {
                     boolean var9;
                     label139: {
                        try {
                           var12 = true;
                           this.compute();
                           this.handle();
                           if (this.keyCheck) {
                              var15 = 0;
                              var12 = false;
                              break label142;
                           }

                           var15 = var1.onTick();
                           int var6 = var1.select();
                           GL11.glDisable(3089);
                           GL11.glDisable(36281);
                           GL11.glColorMask(true, true, true, true);
                           this.windowConvert.process(var15, var6);
                           if (!this.windowConvert.apply()) {
                              var17 = false;
                              var12 = false;
                              break label141;
                           }

                           this.compute(var1);
                           this.resolve(var1);
                           if (this.layerProject) {
                              var17 = false;
                              var12 = false;
                              break label140;
                           }

                           this.resourceClamp = this.process(var15, var6);
                           GL11.glDisable(2929);
                           GL11.glDisable(2884);
                           if (this.resourceClamp) {
                              this.handle(this.presetWrite, this.optionParse, this.pointSubmit);
                              this.handle(var1, this.optionParse, this.pointSubmit);
                              this.handle(var1, this.optionParse, this.pointSubmit, var1.closeProvider());
                              this.process();
                           }

                           var17 = this.prepare(var1) && this.check(var1);
                           if (var17 && this.windowProcess) {
                              this.handle(this.animationExpand, this.profileInvoke, 1.0F, 1.0F, this.sourceSchedule, this.clientRefresh, this.keyFilter);
                           }

                           GL30.glBindFramebuffer(36160, var1.refresh());
                           int var8 = GL30.glCheckFramebufferStatus(36009);
                           if (var8 != 36053) {
                              ScreenRenderDiagnostics.handle(
                                 "MainMenuRenderer", null, "draw framebuffer incomplete status=0x" + Integer.toHexString(var8), null
                              );
                              var9 = false;
                              var12 = false;
                              break label139;
                           }

                           GL11.glViewport(0, 0, var15, var6);
                           if (this.resourceClamp) {
                              this.encodePoint(var1);
                           } else {
                              this.handle(var1, var15, var6);
                              this.handle(var1, var15, var6, 1.0F);
                           }

                           if (var17) {
                              this.onTick(var1);
                           }

                           this.apply(var1);
                           this.tick(var1);
                           this.drawAnimation(var1);
                           this.execute(var1);
                           this.select(var1);
                           this.refresh(var1);
                           var4 = true;
                           var12 = false;
                        } finally {
                           if (var12) {
                              this.compute(2, 0);
                              this.compute(1, 0);
                              this.handle(0);
                              GL20.glUseProgram(0);
                              OpenGlStateSnapshot.compute(this.providerClose);
                           }
                        }

                        this.compute(2, 0);
                        this.compute(1, 0);
                        this.handle(0);
                        GL20.glUseProgram(0);
                        OpenGlStateSnapshot.compute(this.providerClose);
                        return var4;
                     }

                     this.compute(2, 0);
                     this.compute(1, 0);
                     this.handle(0);
                     GL20.glUseProgram(0);
                     OpenGlStateSnapshot.compute(this.providerClose);
                     return var9;
                  }

                  this.compute(2, 0);
                  this.compute(1, 0);
                  this.handle(0);
                  GL20.glUseProgram(0);
                  OpenGlStateSnapshot.compute(this.providerClose);
                  return var17;
               }

               this.compute(2, 0);
               this.compute(1, 0);
               this.handle(0);
               GL20.glUseProgram(0);
               OpenGlStateSnapshot.compute(this.providerClose);
               return var17;
            }

            this.compute(2, 0);
            this.compute(1, 0);
            this.handle(0);
            GL20.glUseProgram(0);
            OpenGlStateSnapshot.compute(this.providerClose);
            return var15 != 0;
         } else {
            ScreenRenderDiagnostics.handle("MainMenuRenderer", null, "frame metrics mismatch requested=" + var1.onTick() + "x" + var1.select(), null);
            return false;
         }
      } else {
         ScreenRenderDiagnostics.handle("MainMenuRenderer", null, "invalid state dimensions", null);
         return false;
      }
   }

   private void compute(WildMainMenuScreen.PrimaryNetworkState var1) {
      int var2 = var1.onTick();
      int var3 = var1.select();
      float var4 = Math.max(0.35F, Math.min(1.0F, var1.closeProvider()));
      this.optionParse = Math.max(2, Math.min(var2, Math.round(var2 * var4)));
      this.pointSubmit = Math.max(2, Math.min(var3, Math.round(var3 * var4)));
      int var5 = Math.max(2, var2 / 2);
      int var6 = Math.max(2, var3 / 2);
      int var7 = Math.max(2, var2 / 4);
      int var8 = Math.max(2, var3 / 4);
      this.actionRender = Math.max(2, Math.min(var5, Math.round(this.optionParse * 0.5F)));
      this.playerApply = Math.max(2, Math.min(var6, Math.round(this.pointSubmit * 0.5F)));
      this.bufferAdapt = Math.max(2, Math.min(var7, Math.round(this.optionParse * 0.25F)));
      this.playerUpdate = Math.max(2, Math.min(var8, Math.round(this.pointSubmit * 0.25F)));
      this.packetRead = (float)this.optionParse / var2;
      this.rendererCancel = (float)this.pointSubmit / var3;
      boolean var9 = var1.collapseOutput() || var1.scanRenderer() || var1.buildSource();
      float var10 = Math.max(0.42F, Math.min(1.0F, var4 * (var9 ? 1.0F : 0.6F)));
      this.listenerPerform = Math.max(2, Math.min(var2, Math.round(var2 * var10)));
      this.configMatch = Math.max(2, Math.min(var3, Math.round(var3 * var10)));
      this.eventReceive = (float)this.listenerPerform / var2;
      this.screenSubmit = (float)this.configMatch / var3;
      this.cacheHandle = (float)this.actionRender / var5;
      this.rangeRelease = (float)this.playerApply / var6;
      this.indexSave = (float)this.bufferAdapt / var7;
      this.indexCheck = (float)this.playerUpdate / var8;
   }

   private void handle(OffscreenRenderTarget var1, int var2, int var3) {
      var1.handle();
      GL11.glViewport(0, 0, var2, var3);
   }

   private void resolve(WildMainMenuScreen.PrimaryNetworkState var1) {
      ShaderFailureCache.ShaderState var2 = this.animate(var1);
      if (var2 == null) {
         this.layerProject = true;
      } else {
         this.layerProject = false;
         this.handle(this.windowConvert, this.listenerPerform, this.configMatch);
         GL11.glDisable(3042);
         GL11.glDisable(2929);
         GL11.glDisable(2884);
         if (var1.scanRenderer() || var1.buildSource() || var1.collapseOutput()) {
            GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            GL11.glClear(16384);
            GL11.glEnable(3042);
            GL14.glBlendFuncSeparate(770, 771, 1, 771);
         }

         var2.handle();
         this.handle(var2, this.listenerPerform, this.configMatch, 0.0F, 0.0F, this.listenerPerform, this.configMatch);
         var2.handle("uTime", var1.tick());
         var2.handle("uResolution", this.listenerPerform, this.configMatch);
         var2.handle("uMouse", var1.animate(), var1.load());
         var2.handle("uMouseVelocity", var1.save(), var1.submit());
         var2.handle("uAccentTop", var1.measure(), var1.blendMatrix(), var1.matchVector());
         var2.handle("uAccentBottom", var1.projectItem(), var1.computeResponse(), var1.fetchProvider());
         var2.handle("uActivity", var1.collectModule());
         var2.handle("uDetail", var1.savePreset());
         var2.handle("uAlpha", 1.0F);
         var2.handle("uLightMode", var1.invokeProfile() ? 1.0F : 0.0F);
         this.handle(var2, var1);
         this.effectScan.handle();
         GL11.glDisable(3042);
      }
   }

   private void handle(WildMainMenuScreen.PrimaryNetworkState var1, int var2, int var3) {
      if (this.matrixFilter != null) {
         GL11.glDisable(3042);
         this.matrixFilter.handle();
         this.handle(this.matrixFilter, var2, var3, 0.0F, 0.0F, var2, var3);
         this.matrixFilter.handle("uTexture", 0);
         this.matrixFilter.handle("uTextureSize", this.windowConvert.resolve(), this.windowConvert.update());
         this.matrixFilter.handle("uSourceScale", this.eventReceive, this.screenSubmit);
         this.matrixFilter.handle("uParallax", var1.drawProfile(), var1.performVector());
         this.matrixFilter.handle("uTime", var1.tick());
         this.matrixFilter.handle("uEntry", var1.writePreset());
         this.matrixFilter.handle("uClickFlash", var1.measureColor());
         this.matrixFilter.handle("uLightMode", var1.invokeProfile() ? 1.0F : 0.0F);
         this.matrixFilter.handle("uSakura", var1.scanRenderer() ? 1.0F : 0.0F);
         this.matrixFilter.handle("uVernal", var1.buildSource() ? 1.0F : 0.0F);
         this.matrixFilter.handle("uHour", var1.execute());
         this.handle(this.windowConvert.compute());
         this.effectScan.handle();
      }
   }

   private void handle(WildMainMenuScreen.PrimaryNetworkState var1, int var2, int var3, float var4) {
      float var5 = var1.convertWindow();
      if (this.layerSample != null && !(var5 <= 0.002F)) {
         GL11.glEnable(3042);
         if (var1.invokeProfile()) {
            GL14.glBlendFuncSeparate(770, 771, 1, 771);
         } else {
            GL14.glBlendFuncSeparate(770, 1, 1, 1);
         }

         this.layerSample.handle();
         this.handle(this.layerSample, var2, var3, 0.0F, 0.0F, var2, var3);
         this.layerSample.handle("uTime", var1.tick());
         this.layerSample.handle("uResolution", var2, var3);
         this.layerSample.handle("uMouse", var1.animate(), var1.load());
         this.layerSample.handle("uParallax", var1.attachEvent() * var4, var1.readServer() * var4);
         this.layerSample.handle("uAccentTop", var1.measure(), var1.blendMatrix(), var1.matchVector());
         this.layerSample.handle("uAccentBottom", var1.projectItem(), var1.computeResponse(), var1.fetchProvider());
         this.layerSample.handle("uEntry", var1.writePreset() * var5);
         this.layerSample.handle("uLightMode", var1.invokeProfile() ? 1.0F : 0.0F);
         this.handle(this.layerSample, var1);
         this.effectScan.handle();
         GL14.glBlendFuncSeparate(770, 771, 1, 771);
      }
   }

   private boolean update(WildMainMenuScreen.PrimaryNetworkState var1) {
      if (this.moduleTick != null && this.playerCollapse != null) {
         float var2 = WildMainMenuScreen.handle(var1.select());
         if (this.listenerRun == var2 && this.sourceBuild.apply()) {
            return true;
         }

         SdfTextRenderer var3 = RoundedRectRenderer.handle(FontRegistry.config);
         SdfTextRenderer var4 = RoundedRectRenderer.handle(FontRegistry.instance);
         SdfTextRenderer var5 = RoundedRectRenderer.handle(FontRegistry.current);
         if (var3 != null && var4 != null) {
            float var6 = Math.min(30.0F, Math.max(12.0F, var2 * 0.22F));
            float var7 = var6 + 4.0F;
            int var8 = Math.round(var6 + var2 * 0.62F) + 12;
            float var9 = WildMainMenuScreen.handle(var2) * 0.5F;
            float var10 = WildMainMenuScreen.compute(var2) * 0.5F;
            float var11 = WildMainMenuScreen.handle(var2) * 0.83F * 0.5F;
            float var12 = WildMainMenuScreen.process(var2);
            float var13 = WildMainMenuScreen.handle(var3, var9);
            float var14 = var4.handle("Src ready by SoftArax", var10);
            float var15 = var5 == null ? 0.0F : var5.handle("w", var11);
            float var16 = var15 > 0.0F ? var2 * 0.3F : 0.0F;
            float var17 = var15 + var16 + var13;
            float var18 = var2 * 1.1F;
            float var19 = var2 * 0.98F;
            float var20 = var19 + var10 * 0.9F;
            int var21 = (int)Math.ceil(Math.max(var17, var14));
            int var22 = var21 + var8 * 2;
            int var23 = (int)Math.ceil(var18 + var20) + var8 * 2;
            if (var22 > 2 && var23 > 2) {
               float var24 = var8 + var21 * 0.5F;
               float var25 = var8 + var18;
               float var26 = var24 - var17 * 0.5F;
               float var27 = var26 + var15 + var16;
               float var28 = var24 - var14 * 0.5F;
               this.sourceBuild.handle(var22, var23);
               if (!this.sourceBuild.apply()) {
                  return false;
               }

               this.sourceBuild.handle();
               GL11.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
               GL11.glClear(16384);
               GL11.glEnable(3042);
               GL14.glBlendFuncSeparate(1, 1, 1, 1);
               GL20.glBlendEquation(32776);
               this.moduleTick.handle();
               this.moduleTick.handle("uAtlas", 0);
               this.moduleTick.handle("uMaskRange", var6);
               GL11.glColorMask(true, false, false, false);
               if (var5 != null) {
                  float var29 = (var5.process("w", var11) + var5.compute("w", var11)) * 0.5F;
                  float var30 = var25 - var12 * 0.5F + var29 + var2 * 0.0188F;
                  this.handle(var5, "w", var11, var26, var30, var22, var23, var7);
               }

               this.handle(var3, "WILD", var9, 0.08F * var9, var27, var25, var22, var23, var7);
               GL11.glColorMask(false, true, false, false);
               this.handle(var4, "Src ready by SoftArax", var10, var28, var25 + var19, var22, var23, var7);
               GL11.glColorMask(true, true, true, true);
               GL20.glBlendEquation(32774);
               GL11.glDisable(3042);
               this.settingSchedule = var22;
               this.inputAcquire = var23;
               this.scaleParse = var28;
               this.sessionEncode = var25 + var19 - var10 * 0.78F;
               this.elementTick = var14;
               this.regionAlign = var10 * 1.02F;
               if (!this.handle(var22, var23, var6, var2)) {
                  return false;
               }

               this.listenerRun = var2;
               this.indexLoad = var12;
               this.blockRun = var6;
               this.layoutSave = var25;
               this.playerEvaluate = -var24;
               this.outputFetch = -var25;
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

   private void handle(SdfTextRenderer var1, String var2, float var3, float var4, float var5, float var6, int var7, int var8, float var9) {
      for (int var10 = 0; var10 < var2.length(); var10++) {
         String var11 = var2.substring(var10, var10 + 1);
         float var12 = var1.handle(var2.substring(0, var10), var3) + var4 * var10;
         this.handle(var1, var11, var3, var5 + var12, var6, var7, var8, var9);
      }
   }

   private boolean handle(int var1, int var2, float var3, float var4) {
      if (this.optionAdvance == null) {
         return false;
      }

      this.outputCollapse.handle(var1, var2);
      if (!this.outputCollapse.apply()) {
         return false;
      }

      this.outputCollapse.handle();
      GL11.glViewport(0, 0, var1, var2);
      GL11.glDisable(3042);
      GL11.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
      GL11.glClear(16384);
      this.optionAdvance.handle();
      this.handle(this.optionAdvance, var1, var2, 0.0F, 0.0F, var1, var2);
      this.optionAdvance.handle("uMask", 0);
      this.optionAdvance.handle("uMaskSize", var1, var2);
      this.optionAdvance.handle("uMaskRange", var3);
      this.optionAdvance.handle("uTightRadius", Math.max(3.0F, var4 * 0.17F));
      this.optionAdvance.handle("uWideRadius", Math.max(8.0F, var4 * 0.62F));
      this.handle(this.sourceBuild.compute());
      this.effectScan.handle();
      return true;
   }

   private void handle(SdfTextRenderer var1, String var2, float var3, float var4, float var5, int var6, int var7, float var8) {
      int var9 = var1.handle();

      for (SdfTextRenderer.CacheEntry var11 : var1.handle(var2, var3, var4, var5)) {
         float var12 = var11.context - var11.instance + var8 * 2.0F;
         float var13 = var11.config - var11.data + var8 * 2.0F;
         this.handle(this.moduleTick, var6, var7, var11.instance - var8, var11.data - var8, var12, var13);
         this.moduleTick.handle("uGlyphUv", var11.state, var11.cache, var11.output, var11.current);
         this.moduleTick.handle("uQuadSize", var12, var13);
         this.moduleTick.handle("uPadPx", var8);
         this.moduleTick.handle("uRangePx", var11.active);
         this.handle(var9);
         this.effectScan.handle();
      }
   }

   private void apply(WildMainMenuScreen.PrimaryNetworkState var1) {
      WildMainMenuScreen.NetworkState var2 = var1.prepare();
      if (!(var2.compute() <= 0.0F) && !(var2.resolve() <= 0.0F)) {
         if (this.update(var1) && this.sourceBuild.apply() && this.outputCollapse.apply()) {
            float var3 = this.listenerRun;
            float var4 = var2.handle() + var2.compute() * 0.5F;
            float var5 = var2.process() + var2.resolve() * 0.5F;
            float var6 = var5 + this.indexLoad * 0.5F;
            float var7 = var4 + this.playerEvaluate;
            float var8 = var6 + this.outputFetch;
            float var9 = 1.0F + 0.005F * (0.5F - 0.5F * (float)Math.cos(var1.render() * (float) (Math.PI * 2.0 / 3.0)));
            float var10 = var4 + var1.advancePosition() * -0.62F;
            float var11 = var5 + var1.checkFrame() * -0.62F;
            float var12 = var10 + (var7 - var4) * var9;
            float var13 = var11 + (var8 - var5) * var9;
            float var14 = this.settingSchedule * var9;
            float var15 = this.inputAcquire * var9;
            GL30.glBindFramebuffer(36160, var1.refresh());
            GL11.glViewport(0, 0, var1.onTick(), var1.select());
            GL11.glEnable(3042);
            GL14.glBlendFuncSeparate(770, 771, 1, 771);
            OffscreenRenderTarget var16 = this.resourceClamp && this.rendererScan.apply() ? this.rendererScan : this.windowConvert;
            OffscreenRenderTarget var17 = this.resourceClamp && this.presetWrite.apply() ? this.presetWrite : this.windowConvert;
            this.playerCollapse.handle();
            this.handle(this.playerCollapse, var1.onTick(), var1.select(), var12, var13, var14, var15);
            this.playerCollapse.handle("uMask", 0);
            this.playerCollapse.handle("uBlur", 1);
            this.playerCollapse.handle("uSharp", 2);
            this.playerCollapse.handle("uShadow", 3);
            this.playerCollapse.handle("uMaskSize", this.settingSchedule, this.inputAcquire);
            this.playerCollapse.handle("uBlurSize", var16.resolve(), var16.update());
            this.playerCollapse.handle("uSharpSize", var17.resolve(), var17.update());
            this.playerCollapse
               .handle(
                  "uSourceScale",
                  var16 == this.rendererScan ? this.cacheHandle : this.eventReceive,
                  var16 == this.rendererScan ? this.rangeRelease : this.screenSubmit
               );
            this.playerCollapse
               .handle(
                  "uSharpScale",
                  var17 == this.presetWrite ? this.packetRead : this.eventReceive,
                  var17 == this.presetWrite ? this.rendererCancel : this.screenSubmit
               );
            this.playerCollapse.handle("uMaskRange", this.blockRun);
            this.playerCollapse.handle("uPointer", (var1.drawAnimation() - var12) / var9, (var1.encodePoint() - var13) / var9);
            this.playerCollapse.handle("uLockupMetrics", var3, this.layoutSave);
            this.playerCollapse.handle("uAccentTop", var1.measure(), var1.blendMatrix(), var1.matchVector());
            this.playerCollapse.handle("uAccentBottom", var1.projectItem(), var1.computeResponse(), var1.fetchProvider());
            this.playerCollapse.handle("uTime", var1.render());
            this.playerCollapse.handle("uEntry", var1.writePreset());
            this.playerCollapse.handle("uPointerActive", var1.fetch());
            this.playerCollapse.handle("uSignature", var1.scheduleAnimation());
            this.playerCollapse.handle("uSignatureLead", var1.apply());
            this.playerCollapse.handle("uInkRect", this.scaleParse, this.sessionEncode, this.elementTick, this.regionAlign);
            this.playerCollapse.handle("uLightMode", var1.invokeProfile() ? 1.0F : 0.0F);
            this.compute(3, this.outputCollapse.compute());
            this.compute(1, var16.compute());
            this.compute(2, var17.compute());
            this.handle(this.sourceBuild.compute());
            this.effectScan.handle();
            this.compute(3, 0);
            this.compute(2, 0);
         }
      }
   }

   private void execute(WildMainMenuScreen.PrimaryNetworkState var1) {
      if (this.worldSend != null) {
         boolean var2 = this.resourceClamp && this.presetWrite.apply();
         OffscreenRenderTarget var3 = this.resourceClamp && this.rendererScan.apply() ? this.rendererScan : this.windowConvert;
         OffscreenRenderTarget var4 = var2 ? this.presetWrite : this.windowConvert;
         GL11.glEnable(3042);
         GL14.glBlendFuncSeparate(770, 771, 1, 771);
         this.worldSend.handle();
         this.worldSend.handle("uBackground", 0);
         this.worldSend.handle("uSharp", 1);
         this.worldSend.handle("uBackdrop", var2 ? 1.0F : 0.0F);
         this.worldSend.handle("uTextureSize", var3.resolve(), var3.update());
         this.worldSend.handle("uSharpSize", var4.resolve(), var4.update());
         this.worldSend
            .handle(
               "uSourceScale",
               var3 == this.rendererScan ? this.cacheHandle : this.eventReceive,
               var3 == this.rendererScan ? this.rangeRelease : this.screenSubmit
            );
         this.worldSend
            .handle(
               "uSharpScale",
               var4 == this.presetWrite ? this.packetRead : this.eventReceive,
               var4 == this.presetWrite ? this.rendererCancel : this.screenSubmit
            );
         this.worldSend.handle("uTime", var1.render());
         this.worldSend.handle("uAccentTop", var1.measure(), var1.blendMatrix(), var1.matchVector());
         this.worldSend.handle("uAccentBottom", var1.projectItem(), var1.computeResponse(), var1.fetchProvider());
         this.worldSend.handle("uLightMode", var1.invokeProfile() ? 1.0F : 0.0F);
         this.compute(1, var4.compute());
         this.handle(var3.compute());

         for (int var5 = 0; var5 < var1.handle(); var5++) {
            WildMainMenuScreen.PrimaryColorState var6 = var1.handle(var5);
            float var7 = var6.animate();
            float var8 = var6.execute() - var7;
            float var9 = var6.prepare() - var7;
            float var10 = var6.check() + var7 * 2.0F;
            float var11 = var6.onTick() + var7 * 2.0F;
            this.handle(this.worldSend, var1.onTick(), var1.select(), var8, var9, var10, var11);
            this.worldSend.handle("uButton", var7, var7, var6.check(), var6.onTick());
            float var12 = Math.max(var6.load(), 0.001F);
            this.worldSend.handle("uLocalMouse", var6.save(), var6.submit());
            this.worldSend.handle("uPointerLocal", var6.resolve() * var6.check() / var12, var6.update() * var6.onTick() / var12);
            this.worldSend.handle("uPointerValid", var6.compute() ? 1.0F : 0.0F);
            this.worldSend.handle("uRadius", var6.select());
            this.worldSend.handle("uHover", var6.refresh());
            this.worldSend.handle("uMagnet", var6.render());
            this.worldSend.handle("uPress", var6.tick());
            this.worldSend.handle("uEntry", var6.drawAnimation());
            this.worldSend.handle("uFlash", var6.encodePoint());
            this.worldSend.handle("uWave", var6.process());
            this.worldSend.handle("uScale", var6.load());
            this.worldSend.handle("uSeed", var5 * 0.7139F);
            this.effectScan.handle();
         }
      }
   }

   private boolean prepare(WildMainMenuScreen.PrimaryNetworkState var1) {
      if (this.indexBind != null && this.actionRead != null && this.effectScan != null) {
         int var2 = var1.onTick();
         int var3 = var1.select();
         this.profileInvoke.process(Math.max(2, var2 / 2), Math.max(2, var3 / 2));
         if (!this.profileInvoke.apply()) {
            this.windowProcess = false;
            return false;
         }

         this.dataValidate = this.profileInvoke.resolve();
         this.scaleRender = this.profileInvoke.update();
         if (this.animationExpand != null && var1.closeProvider() >= 0.6F) {
            this.sourceSchedule.process(Math.max(2, var2 / 4), Math.max(2, var3 / 4));
            this.clientRefresh = this.sourceSchedule.resolve();
            this.keyFilter = this.sourceSchedule.update();
            this.windowProcess = this.sourceSchedule.apply();
         } else {
            this.windowProcess = false;
         }

         return true;
      } else {
         this.windowProcess = false;
         return false;
      }
   }

   private boolean check(WildMainMenuScreen.PrimaryNetworkState var1) {
      int var2 = var1.handle();
      if (var2 <= 0) {
         return false;
      }

      if (this.configCollapse.length < var2) {
         this.configCollapse = new float[Math.max(var2, this.configCollapse.length * 2)];
      }

      int var3 = 0;
      float var4 = Float.MAX_VALUE;
      float var5 = Float.MAX_VALUE;
      float var6 = -Float.MAX_VALUE;
      float var7 = -Float.MAX_VALUE;

      for (int var8 = 0; var8 < var2; var8++) {
         WildMainMenuScreen.PrimaryColorState var9 = var1.handle(var8);
         float var10 = Math.max(process(var9.refresh(), 0.0F, 1.0F), process(var9.render(), 0.0F, 1.0F) * 0.85F);
         float var11 = process(var9.drawAnimation(), 0.0F, 1.0F);
         float var12 = handle(0.3F, 1.0F, var10) * var11;
         var12 = Math.max(var12, process(var9.handle(), 0.0F, 1.0F) * 0.52F * var11);
         var12 = Math.max(var12, process(var9.encodePoint(), 0.0F, 1.0F));
         if (var12 <= 0.004F) {
            this.configCollapse[var8] = 0.0F;
         } else {
            this.configCollapse[var8] = var12;
            var3++;
            float var13 = var9.animate();
            var4 = Math.min(var4, var9.execute() - var13);
            var5 = Math.min(var5, var9.prepare() - var13);
            var6 = Math.max(var6, var9.execute() + var9.check() + var13);
            var7 = Math.max(var7, var9.prepare() + var9.onTick() + var13);
         }
      }

      if (var3 == 0) {
         return false;
      }

      float var16 = var1.onTick();
      float var17 = var1.select();
      this.requestAdapt = Math.max(0.0F, var4 - 24.0F);
      this.timerMeasure = Math.max(0.0F, var5 - 24.0F);
      this.vectorEncode = Math.min(var16, var6 + 24.0F) - this.requestAdapt;
      this.requestReceive = Math.min(var17, var7 + 24.0F) - this.timerMeasure;
      if (!(this.vectorEncode <= 1.0F) && !(this.requestReceive <= 1.0F)) {
         float var18 = this.dataValidate / Math.max(1.0F, var16);
         float var19 = this.scaleRender / Math.max(1.0F, var17);
         this.handle(this.profileInvoke, this.dataValidate, this.scaleRender);
         GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
         GL11.glClear(16384);
         GL11.glEnable(3042);
         GL20.glBlendEquationSeparate(32774, 32776);
         GL14.glBlendFuncSeparate(1, 1, 1, 1);
         this.indexBind.handle();
         this.indexBind.handle("uTime", var1.render());
         this.indexBind.handle("uAccentTop", var1.measure(), var1.blendMatrix(), var1.matchVector());
         this.indexBind.handle("uAccentBottom", var1.projectItem(), var1.computeResponse(), var1.fetchProvider());
         this.indexBind.handle("uLightMode", var1.invokeProfile() ? 1.0F : 0.0F);

         for (int var22 = 0; var22 < var2; var22++) {
            float var23 = this.configCollapse[var22];
            if (!(var23 <= 0.0F)) {
               WildMainMenuScreen.PrimaryColorState var14 = var1.handle(var22);
               float var15 = var14.animate();
               this.handle(
                  this.indexBind,
                  this.dataValidate,
                  this.scaleRender,
                  (var14.execute() - var15) * var18,
                  (var14.prepare() - var15) * var19,
                  (var14.check() + var15 * 2.0F) * var18,
                  (var14.onTick() + var15 * 2.0F) * var19
               );
               this.indexBind.handle("uButton", var15 * var18, var15 * var19, var14.check() * var18, var14.onTick() * var19);
               this.indexBind.handle("uRadius", var14.select() * var18);
               this.indexBind.handle("uScale", var14.load());
               this.indexBind.handle("uDrive", var23);
               this.indexBind.handle("uPress", var14.tick());
               this.indexBind.handle("uFlash", var14.encodePoint());
               this.indexBind
                  .handle(
                     "uPointerLocal",
                     var14.resolve() * var14.check() * var18 / Math.max(var14.load(), 0.001F),
                     var14.update() * var14.onTick() * var19 / Math.max(var14.load(), 0.001F)
                  );
               this.indexBind.handle("uPointerValid", var14.compute() ? 1.0F : 0.0F);
               this.indexBind.handle("uLocalMouse", var14.save(), var14.submit());
               this.indexBind.handle("uSteady", process(var14.handle(), 0.0F, 1.0F) * (1.0F - process(var14.refresh(), 0.0F, 1.0F)));
               this.indexBind.handle("uSeed", var22 * 0.7139F);
               this.effectScan.handle();
            }
         }

         GL20.glBlendEquationSeparate(32774, 32774);
         GL11.glDisable(3042);
         return true;
      } else {
         return false;
      }
   }

   private void onTick(WildMainMenuScreen.PrimaryNetworkState var1) {
      GL11.glEnable(3042);
      GL14.glBlendFuncSeparate(1, 771, 0, 1);
      OffscreenRenderTarget var2 = this.windowProcess ? this.sourceSchedule : this.profileInvoke;
      this.actionRead.handle();
      this.handle(this.actionRead, var1.onTick(), var1.select(), this.requestAdapt, this.timerMeasure, this.vectorEncode, this.requestReceive);
      this.actionRead.handle("uGlow", 0);
      this.actionRead.handle("uBloom", 1);
      this.actionRead.handle("uGlowTexel", 1.0F / Math.max(1, this.profileInvoke.resolve()), 1.0F / Math.max(1, this.profileInvoke.update()));
      this.actionRead.handle("uBloomTexel", 1.0F / Math.max(1, var2.resolve()), 1.0F / Math.max(1, var2.update()));
      this.actionRead.handle("uSourceScale", 1.0F, 1.0F);
      this.actionRead.handle("uBloomAmount", this.windowProcess ? 1.0F : 0.0F);
      this.actionRead.handle("uLightMode", var1.invokeProfile() ? 1.0F : 0.0F);
      this.compute(1, var2.compute());
      this.handle(this.profileInvoke.compute());
      this.effectScan.handle();
      GL14.glBlendFuncSeparate(770, 771, 1, 771);
   }

   private static float handle(float var0, float var1, float var2) {
      float var3 = process((var2 - var0) / Math.max(var1 - var0, 1.0E-5F), 0.0F, 1.0F);
      return var3 * var3 * (3.0F - 2.0F * var3);
   }

   private void select(WildMainMenuScreen.PrimaryNetworkState var1) {
      if (this.targetWrite != null) {
         WildMainMenuScreen.SecondaryNetworkState var2 = var1.check();
         if (!(var2.resolve() <= 1.0F) && !(var2.update() <= 1.0F) && !(var2.check() <= 0.001F)) {
            OffscreenRenderTarget var3 = this.resourceClamp && this.rendererScan.apply() ? this.rendererScan : this.windowConvert;
            GL11.glEnable(3042);
            GL14.glBlendFuncSeparate(770, 771, 1, 771);
            this.targetWrite.handle();
            this.targetWrite.handle("uBackground", 0);
            this.targetWrite.handle("uTextureSize", var3.resolve(), var3.update());
            this.targetWrite
               .handle(
                  "uSourceScale",
                  var3 == this.rendererScan ? this.cacheHandle : this.eventReceive,
                  var3 == this.rendererScan ? this.rangeRelease : this.screenSubmit
               );
            this.targetWrite.handle("uTime", var1.render());
            this.targetWrite.handle("uAccentTop", var1.measure(), var1.blendMatrix(), var1.matchVector());
            this.targetWrite.handle("uAccentBottom", var1.projectItem(), var1.computeResponse(), var1.fetchProvider());
            this.targetWrite.handle("uLightMode", var1.invokeProfile() ? 1.0F : 0.0F);
            this.handle(var3.compute());
            this.targetWrite.handle("uLocalMouse", var2.onTick(), var2.select());
            float var4 = var2.load() > 0.0F ? var2.load() : 40.0F;
            this.handle(
               var1,
               var2.process(),
               var2.compute(),
               var2.resolve(),
               var2.update(),
               var2.apply(),
               var4,
               var2.execute(),
               var2.check(),
               var2.prepare(),
               var2.refresh(),
               var2.render(),
               var2.tick(),
               var2.drawAnimation(),
               var2.encodePoint(),
               var2.animate()
            );
         }
      }
   }

   private void refresh(WildMainMenuScreen.PrimaryNetworkState var1) {
      if (this.resultEncode != null && var1.process() > 0) {
         OffscreenRenderTarget var2 = this.resourceClamp && this.rendererScan.apply() ? this.rendererScan : this.windowConvert;
         boolean var3 = var2 == this.rendererScan;
         this.handle(var1, var2, var3 ? this.cacheHandle : this.eventReceive, var3 ? this.rangeRelease : this.screenSubmit, false);
      }
   }
   public boolean process(WildMainMenuScreen.PrimaryNetworkState var1) {
      if (this.keyCheck || !this.handlerRun || this.resultEncode == null || this.effectScan == null) {
         return false;
      }

      if (var1 != null && var1.compute() > 0) {
         int var2 = var1.onTick();
         int var3 = var1.select();
         if (var2 > 0 && var3 > 0) {
            OpenGlStateSnapshot.process(this.presetSave);
            boolean var12 = false /* VF: Semaphore variable */;

            boolean var16;
            label117: {
               boolean var17;
               label116: {
                  try {
                     var12 = true;
                     this.compute();
                     GL11.glDisable(3089);
                     GL11.glDisable(36281);
                     GL11.glDisable(2929);
                     GL11.glDisable(2884);
                     GL11.glColorMask(true, true, true, true);
                     boolean var4 = this.process(var1, var2, var3);
                     GL30.glBindFramebuffer(36160, var1.refresh());
                     if (GL30.glCheckFramebufferStatus(36009) != 36053) {
                        var16 = false;
                        var12 = false;
                        break label117;
                     }

                     GL11.glViewport(0, 0, var2, var3);
                     float var6;
                     float var7;
                     OffscreenRenderTarget var15;
                     if (var4) {
                        var15 = this.textureRun;
                        var6 = 1.0F;
                        var7 = 1.0F;
                     } else {
                        var15 = this.resourceClamp && this.rendererScan.apply() ? this.rendererScan : this.windowConvert;
                        var17 = var15 == this.rendererScan;
                        var6 = var17 ? this.cacheHandle : this.eventReceive;
                        var7 = var17 ? this.rangeRelease : this.screenSubmit;
                     }

                     this.handle(var1, var15, var6, var7, true);
                     var17 = true;
                     var12 = false;
                     break label116;
                  } catch (Throwable var13) {
                     ScreenRenderDiagnostics.handle("MainMenuRenderer", null, "overlay pass failed", var13);
                     var16 = false;
                     var12 = false;
                  } finally {
                     if (var12) {
                        this.compute(2, 0);
                        this.compute(1, 0);
                        this.handle(0);
                        GL20.glUseProgram(0);
                        OpenGlStateSnapshot.compute(this.presetSave);
                     }
                  }

                  this.compute(2, 0);
                  this.compute(1, 0);
                  this.handle(0);
                  GL20.glUseProgram(0);
                  OpenGlStateSnapshot.compute(this.presetSave);
                  return var16;
               }

               this.compute(2, 0);
               this.compute(1, 0);
               this.handle(0);
               GL20.glUseProgram(0);
               OpenGlStateSnapshot.compute(this.presetSave);
               return var17;
            }

            this.compute(2, 0);
            this.compute(1, 0);
            this.handle(0);
            GL20.glUseProgram(0);
            OpenGlStateSnapshot.compute(this.presetSave);
            return var16;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   private boolean process(WildMainMenuScreen.PrimaryNetworkState var1, int var2, int var3) {
      if (this.animationExpand != null && this.playerRun != null) {
         int var4 = Math.max(2, var2 / 2);
         int var5 = Math.max(2, var3 / 2);
         int var6 = Math.max(2, var2 / 4);
         int var7 = Math.max(2, var3 / 4);
         int var8 = Math.max(2, var2 / 8);
         int var9 = Math.max(2, var3 / 8);
         int var10 = Math.max(2, var2 / 16);
         int var11 = Math.max(2, var3 / 16);
         this.timerRender.handle(var4, var5);
         this.scaleSave.handle(var6, var7);
         this.colorCompute.handle(var8, var9);
         this.scaleAdapt.handle(var10, var11);
         this.textureRun.handle(var4, var5);
         if (this.timerRender.apply() && this.scaleSave.apply() && this.colorCompute.apply() && this.scaleAdapt.apply() && this.textureRun.apply()) {
            GL30.glBindFramebuffer(36008, var1.refresh());
            GL30.glBindFramebuffer(36009, this.timerRender.process());
            if (GL30.glCheckFramebufferStatus(36008) == 36053 && GL30.glCheckFramebufferStatus(36009) == 36053) {
               GL30.glBlitFramebuffer(0, 0, var2, var3, 0, 0, var4, var5, 16384, 9729);
               this.handle(this.animationExpand, this.timerRender, 1.0F, 1.0F, this.scaleSave, var6, var7);
               this.handle(this.animationExpand, this.scaleSave, 1.0F, 1.0F, this.colorCompute, var8, var9);
               this.handle(this.animationExpand, this.colorCompute, 1.0F, 1.0F, this.scaleAdapt, var10, var11);
               this.handle(this.playerRun, this.scaleAdapt, 1.0F, 1.0F, this.colorCompute, var8, var9);
               this.handle(this.playerRun, this.colorCompute, 1.0F, 1.0F, this.scaleSave, var6, var7);
               this.handle(this.playerRun, this.scaleSave, 1.0F, 1.0F, this.textureRun, var4, var5);
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

   private void render(WildMainMenuScreen.PrimaryNetworkState var1) {
      int var2 = var1.onTick();
      int var3 = var1.select();
      float var4 = Math.max(Math.min(var2, var3), 1) / 1080.0F;
      this.providerRead = Math.max(7.0F * var4, 1.5F);
      this.matrixBlend2 = Math.max(38.0F * var4, 4.0F);
      this.scalePerform = var4;
      this.contextExpand = Math.max(this.matrixBlend2 * 3.4F + 16.0F * var4, this.providerRead * 3.4F + 2.4F * var4) + 6.0F * var4;
      this.keyProcess = false;
   }

   private void handle(WildMainMenuScreen.PrimaryNetworkState var1, WildMainMenuScreen.FallbackNetworkState var2) {
      if (this.messageParse != null) {
         float var3 = var2.handle();
         if (!(var2.compute() > 0.004F) && !(var3 <= 0.004F)) {
            this.messageParse.handle();
            if (!this.keyProcess) {
               this.messageParse.handle("uLightMode", var1.invokeProfile() ? 1.0F : 0.0F);
               this.messageParse.handle("uContact", this.providerRead, 2.4F * this.scalePerform, 2.2F * this.scalePerform, 0.8F * this.scalePerform);
               this.messageParse.handle("uAmbient", this.matrixBlend2, 16.0F * this.scalePerform, 9.0F * this.scalePerform, 3.0F * this.scalePerform);
               this.messageParse.handle("uGains", 0.33F, 0.48F);
               this.keyProcess = true;
            }

            float var4 = this.contextExpand;
            this.handle(
               this.messageParse,
               var1.onTick(),
               var1.select(),
               var2.onTick() - var4,
               var2.select() - var4,
               var2.refresh() + var4 * 2.0F,
               var2.render() + var4 * 2.0F
            );
            this.messageParse.handle("uContent", var4, var4, var2.refresh(), var2.render());
            this.messageParse.handle("uRadius", var2.tick());
            this.messageParse.handle("uEntry", var2.encodePoint());
            this.messageParse.handle("uReveal", var2.save());
            this.messageParse.handle("uRevealDir", var2.submit());
            this.messageParse.handle("uOpacity", Math.min(var3, 1.0F));
            this.effectScan.handle();
         }
      }
   }

   private void handle(WildMainMenuScreen.PrimaryNetworkState var1, OffscreenRenderTarget var2, float var3, float var4, boolean var5) {
      int var6 = var5 ? var1.compute() : var1.process();
      if (var6 > 0 && var2 != null && var2.apply()) {
         this.render(var1);
         GL11.glEnable(3042);
         GL14.glBlendFuncSeparate(770, 771, 1, 771);
         this.resultEncode.handle();
         this.resultEncode.handle("uBackground", 0);
         this.resultEncode.handle("uTextureSize", var2.resolve(), var2.update());
         this.resultEncode.handle("uSourceScale", var3, var4);
         this.resultEncode.handle("uTime", var1.render());
         this.resultEncode.handle("uAccentTop", var1.measure(), var1.blendMatrix(), var1.matchVector());
         this.resultEncode.handle("uAccentBottom", var1.projectItem(), var1.computeResponse(), var1.fetchProvider());
         this.resultEncode.handle("uLightMode", var1.invokeProfile() ? 1.0F : 0.0F);
         this.handle(var2.compute());

         for (int var7 = 0; var7 < var6; var7++) {
            WildMainMenuScreen.FallbackNetworkState var8 = var5 ? var1.apply(var7) : var1.compute(var7);
            if (!(var8.refresh() <= 1.0F) && !(var8.render() <= 1.0F) && !(var8.encodePoint() <= 0.002F)) {
               this.handle(var1, var8);
               this.resultEncode.handle();
               float var9 = var8.drawAnimation() > 0.0F ? var8.drawAnimation() : 32.0F;
               this.handle(
                  this.resultEncode,
                  var1.onTick(),
                  var1.select(),
                  var8.onTick() - var9,
                  var8.select() - var9,
                  var8.refresh() + var9 * 2.0F,
                  var8.render() + var9 * 2.0F
               );
               this.resultEncode.handle("uContent", var9, var9, var8.refresh(), var8.render());
               this.resultEncode.handle("uRadius", var8.tick());
               this.resultEncode.handle("uEntry", var8.encodePoint());
               this.resultEncode.handle("uHover", var8.animate());
               this.resultEncode.handle("uGlow", var8.load());
               this.resultEncode.handle("uReveal", var8.save());
               this.resultEncode.handle("uRevealDir", var8.submit());
               this.resultEncode.handle("uPointerLocal", var8.unload(), var8.fetch());
               this.resultEncode.handle("uRow", var8.measure(), var8.blendMatrix(), var8.matchVector(), var8.projectItem());
               this.resultEncode.handle("uRowRadius", var8.computeResponse());
               this.resultEncode.handle("uRowGlow", var8.fetchProvider());
               this.resultEncode.handle("uChevron", var8.resolve(), var8.update(), var8.apply(), var8.execute());
               this.resultEncode.handle("uChevronDir", var8.prepare());
               this.resultEncode.handle("uChevronAlpha", var8.check());
               this.resultEncode.handle("uScrim", var8.compute());
               this.resultEncode.handle("uDensity", var8.process());
               this.effectScan.handle();
            }
         }
      }
   }

   private void tick(WildMainMenuScreen.PrimaryNetworkState var1) {
      WildMainMenuScreen.PrimaryScreenState var2 = var1.resolve();
      if (this.actionConvert != null && !(var2.resolve() <= 0.5F) && !(var2.execute() <= 0.004F) && !(var2.prepare() <= 0.004F)) {
         float var3 = Math.max(var2.update(), var2.resolve() * 1.6F);
         float var4 = var2.process() - var3;
         float var5 = var2.compute() - var3;
         float var6 = var3 * 2.0F;
         GL11.glEnable(3042);
         GL14.glBlendFuncSeparate(770, 771, 1, 771);
         this.actionConvert.handle();
         this.handle(this.actionConvert, var1.onTick(), var1.select(), var4, var5, var6, var6);
         this.actionConvert.handle("uBody", var3, var3, var2.resolve(), var3);
         this.actionConvert.handle("uAccentTop", var1.measure(), var1.blendMatrix(), var1.matchVector());
         this.actionConvert.handle("uAccentBottom", var1.projectItem(), var1.computeResponse(), var1.fetchProvider());
         this.actionConvert.handle("uPhase", var2.apply());
         this.actionConvert.handle("uTime", var1.render());
         this.actionConvert.handle("uEntry", var2.prepare());
         this.actionConvert.handle("uAlpha", var2.execute());
         this.actionConvert.handle("uSeed", var2.check());
         this.actionConvert.handle("uLightMode", var1.invokeProfile() ? 1.0F : 0.0F);
         this.actionConvert.handle("uPointer", var1.drawAnimation() - var4, var1.encodePoint() - var5);
         this.effectScan.handle();
      }
   }

   private void drawAnimation(WildMainMenuScreen.PrimaryNetworkState var1) {
      WildMainMenuScreen.ColorState var2 = var1.update();
      if (this.screenRead != null && !(var2.compute() <= 0.5F) && !(var2.apply() <= 0.004F) && !(var2.update() <= 0.004F)) {
         OffscreenRenderTarget var3 = this.resourceClamp && this.rendererScan.apply() ? this.rendererScan : this.windowConvert;
         if (var3.apply()) {
            boolean var4 = var3 == this.rendererScan;
            float var5 = Math.max(var2.resolve(), var2.compute() * 1.7F);
            float var6 = var2.handle() - var5;
            float var7 = var2.process() - var5;
            float var8 = var5 * 2.0F;
            GL11.glEnable(3042);
            GL14.glBlendFuncSeparate(770, 771, 1, 771);
            this.screenRead.handle();
            this.screenRead.handle("uBackground", 0);
            this.screenRead.handle("uTextureSize", var3.resolve(), var3.update());
            this.screenRead.handle("uSourceScale", var4 ? this.cacheHandle : this.eventReceive, var4 ? this.rangeRelease : this.screenSubmit);
            this.handle(this.screenRead, var1.onTick(), var1.select(), var6, var7, var8, var8);
            this.screenRead.handle("uGem", var5, var5, var2.compute(), var5);
            this.screenRead.handle("uAccentTop", var1.measure(), var1.blendMatrix(), var1.matchVector());
            this.screenRead.handle("uAccentBottom", var1.projectItem(), var1.computeResponse(), var1.fetchProvider());
            this.screenRead.handle("uRadius", var2.compute());
            this.screenRead.handle("uTime", var1.render());
            this.screenRead.handle("uEntry", var2.update());
            this.screenRead.handle("uAlpha", var2.apply());
            this.screenRead.handle("uSeed", var2.execute());
            this.screenRead.handle("uLightMode", var1.invokeProfile() ? 1.0F : 0.0F);
            this.screenRead.handle("uPointer", var2.prepare() - var6, var2.check() - var7);
            this.handle(var3.compute());
            this.effectScan.handle();
         }
      }
   }

   private void handle(
      WildMainMenuScreen.PrimaryNetworkState var1,
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
      float var13,
      float var14,
      float var15,
      float var16
   ) {
      this.handle(this.targetWrite, var1.onTick(), var1.select(), var2 - var7, var3 - var7, var4 + var7 * 2.0F, var5 + var7 * 2.0F);
      this.targetWrite.handle("uContent", var7, var7, var4, var5);
      this.targetWrite.handle("uRadius", var6);
      this.targetWrite.handle("uHover", var8);
      this.targetWrite.handle("uEntry", var9);
      this.targetWrite.handle("uFlash", var10);
      this.targetWrite.handle("uPill", var11, var12, var13, var14);
      this.targetWrite.handle("uPillRadius", var1.check().handle());
      this.targetWrite.handle("uPillGlow", var15);
      this.targetWrite.handle("uPillVelocity", var16);
      this.effectScan.handle();
   }

   private void handle(ShaderFailureCache.ShaderState var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      var1.handle("uViewport", var2, var3);
      var1.handle("uRect", var4, var5, var6, var7);
   }

   private void handle(ShaderFailureCache.ShaderState var1, WildMainMenuScreen.PrimaryNetworkState var2) {
      for (int var3 = 0; var3 < 14; var3++) {
         WildMainMenuScreen.SecondaryFileEntry var4 = var2.onTick(var3);
         var1.handle(context[var3], var4.handle(), var4.process(), var4.compute(), var4.resolve());
      }
   }

   private static float process(float var0, float var1, float var2) {
      return Math.max(var1, Math.min(var2, var0));
   }

   private void handle() {
      if (!this.handlerRun) {
         if (this.effectScan == null) {
            this.effectScan = new VertexArrayBuffer();
         }

         this.matrixFilter = this.moduleCollect
            .process("composite", "assets/wild/shaders/mainmenu/menu_quad.vert", "assets/wild/shaders/mainmenu/menu_composite.frag");
         this.layerSample = this.moduleCollect
            .process("particles", "assets/wild/shaders/mainmenu/menu_quad.vert", "assets/wild/shaders/mainmenu/menu_particles.frag");
         this.worldSend = this.moduleCollect.process("buttons", "assets/wild/shaders/mainmenu/menu_quad.vert", "assets/wild/shaders/mainmenu/menu_button.frag");
         this.targetWrite = this.moduleCollect
            .process("capsule", "assets/wild/shaders/mainmenu/menu_quad.vert", "assets/wild/shaders/mainmenu/menu_capsule.frag");
         this.resultEncode = this.moduleCollect.process("panel", "assets/wild/shaders/mainmenu/menu_quad.vert", "assets/wild/shaders/mainmenu/menu_panel.frag");
         this.messageParse = this.moduleCollect
            .process("panel_shadow", "assets/wild/shaders/mainmenu/menu_quad.vert", "assets/wild/shaders/mainmenu/menu_panel_shadow.frag");
         this.actionConvert = this.moduleCollect
            .process("celestial", "assets/wild/shaders/mainmenu/menu_quad.vert", "assets/wild/shaders/mainmenu/menu_celestial.frag");
         this.screenRead = this.moduleCollect.process("gem", "assets/wild/shaders/mainmenu/menu_quad.vert", "assets/wild/shaders/mainmenu/menu_gem.frag");
         this.animationExpand = this.moduleCollect
            .process("blur_down", "assets/wild/shaders/mainmenu/menu_quad.vert", "assets/wild/shaders/mainmenu/menu_blur_down.frag");
         this.playerRun = this.moduleCollect
            .process("blur_up", "assets/wild/shaders/mainmenu/menu_quad.vert", "assets/wild/shaders/mainmenu/menu_blur_up.frag");
         this.matrixRender = this.moduleCollect.process("blit", "assets/wild/shaders/mainmenu/menu_quad.vert", "assets/wild/shaders/mainmenu/menu_blit.frag");
         this.indexBind = this.moduleCollect
            .process("button_glow", "assets/wild/shaders/mainmenu/menu_quad.vert", "assets/wild/shaders/mainmenu/menu_button_glow.frag");
         this.actionRead = this.moduleCollect
            .process("glow_composite", "assets/wild/shaders/mainmenu/menu_quad.vert", "assets/wild/shaders/mainmenu/menu_glow_composite.frag");
         this.moduleTick = this.moduleCollect
            .process("sdf_bake", "assets/wild/shaders/mainmenu/menu_quad.vert", "assets/wild/shaders/mainmenu/menu_sdf_bake.frag");
         this.playerCollapse = this.moduleCollect
            .process("lockup", "assets/wild/shaders/mainmenu/menu_quad.vert", "assets/wild/shaders/mainmenu/menu_lockup.frag");
         this.optionAdvance = this.moduleCollect
            .process("lockup_shadow", "assets/wild/shaders/mainmenu/menu_quad.vert", "assets/wild/shaders/mainmenu/menu_lockup_shadow.frag");
         this.keyCheck = this.matrixFilter == null || this.worldSend == null;
         this.handlerRun = true;
      }
   }

   private boolean process(int var1, int var2) {
      if (this.animationExpand != null && this.playerRun != null && this.matrixRender != null) {
         this.presetWrite.handle(var1, var2);
         this.colorMeasure.handle(Math.max(2, var1 / 2), Math.max(2, var2 / 2));
         this.animationSchedule.handle(Math.max(2, var1 / 4), Math.max(2, var2 / 4));
         this.rendererScan.handle(Math.max(2, var1 / 2), Math.max(2, var2 / 2));
         return this.presetWrite.apply() && this.colorMeasure.apply() && this.animationSchedule.apply() && this.rendererScan.apply();
      } else {
         return false;
      }
   }

   private void handle(ShaderFailureCache.ShaderState var1, OffscreenRenderTarget var2, float var3, float var4, OffscreenRenderTarget var5, int var6, int var7) {
      this.handle(var5, var6, var7);
      GL11.glDisable(3042);
      var1.handle();
      this.handle(var1, var6, var7, 0.0F, 0.0F, var6, var7);
      var1.handle("uSource", 0);
      var1.handle("uSourceTexel", 1.0F / Math.max(1, var2.resolve()), 1.0F / Math.max(1, var2.update()));
      var1.handle("uSourceScale", var3, var4);
      this.handle(var2.compute());
      this.effectScan.handle();
   }

   private void process() {
      this.handle(this.animationExpand, this.presetWrite, this.packetRead, this.rendererCancel, this.colorMeasure, this.actionRender, this.playerApply);
      this.handle(this.animationExpand, this.colorMeasure, this.cacheHandle, this.rangeRelease, this.animationSchedule, this.bufferAdapt, this.playerUpdate);
      this.handle(this.playerRun, this.animationSchedule, this.indexSave, this.indexCheck, this.rendererScan, this.actionRender, this.playerApply);
   }

   private void encodePoint(WildMainMenuScreen.PrimaryNetworkState var1) {
      GL11.glDisable(3042);
      this.matrixRender.handle();
      this.handle(this.matrixRender, var1.onTick(), var1.select(), 0.0F, 0.0F, var1.onTick(), var1.select());
      this.matrixRender.handle("uSource", 0);
      this.matrixRender.handle("uSourceScale", this.packetRead, this.rendererCancel);
      this.matrixRender.handle("uSourceTexel", 1.0F / Math.max(1, this.presetWrite.resolve()), 1.0F / Math.max(1, this.presetWrite.update()));
      this.handle(this.presetWrite.compute());
      this.effectScan.handle();
   }

   private ShaderFailureCache.ShaderState animate(WildMainMenuScreen.PrimaryNetworkState var1) {
      if (var1.collapseOutput()) {
         if (this.stateApply == null) {
            this.stateApply = this.moduleCollect
               .process("midnight_azure", "assets/wild/shaders/mainmenu/menu_quad.vert", "assets/wild/shaders/mainmenu/midnight_azure.frag");
         }

         return this.stateApply;
      } else if (var1.buildSource()) {
         if (this.playerCollect == null) {
            this.playerCollect = this.moduleCollect
               .process("vernal_solstice", "assets/wild/shaders/mainmenu/menu_quad.vert", "assets/wild/shaders/mainmenu/vernal_solstice.frag");
         }

         return this.playerCollect;
      } else if (var1.scanRenderer()) {
         if (this.entryAnimate == null) {
            this.entryAnimate = this.moduleCollect
               .process("sakura_breeze", "assets/wild/shaders/mainmenu/menu_quad.vert", "assets/wild/shaders/mainmenu/sakura_breeze.frag");
         }

         return this.entryAnimate;
      } else {
         if (this.packetSave == null) {
            this.packetSave = this.moduleCollect
               .process("nebula", "assets/wild/shaders/mainmenu/menu_quad.vert", "assets/wild/shaders/mainmenu/menu_nebula.frag");
         }

         return this.packetSave;
      }
   }

   private void compute() {
      this.entityFilter = -1;
      this.layerSample2 = -1;
      this.moduleCollect.handle();
   }

   private void handle(int var1) {
      if (this.entityFilter != 33984) {
         GL13.glActiveTexture(33984);
         this.entityFilter = 33984;
         this.layerSample2 = -1;
      }

      if (this.layerSample2 != var1) {
         GL11.glBindTexture(3553, var1);
         this.layerSample2 = var1;
      }
   }

   private void compute(int var1, int var2) {
      GL13.glActiveTexture(33984 + var1);
      GL11.glBindTexture(3553, var2);
      this.entityFilter = -1;
      this.layerSample2 = -1;
   }

   public static long handle(MinecraftClient var0, int var1, int var2) {
      if (var0 != null && GLFW.glfwGetCurrentContext() != 0L) {
         Window var3 = var0.getWindow();
         if (var3 != null && !var3.hasZeroWidthOrHeight()) {
            int var4 = var3.getFramebufferWidth();
            int var5 = var3.getFramebufferHeight();
            if (var4 > 0 && var5 > 0 && var1 > 0 && var2 > 0) {
               Framebuffer var6 = var0.getFramebuffer();
               if (var6 != null && var6.textureWidth > 0 && var6.textureHeight > 0) {
                  if (var6.getColorAttachment() instanceof GlTexture var8) {
                     int var9 = var8.getGlId();
                     if (var9 > 0 && GL11.glIsTexture(var9)) {
                        var4 = Math.min(var4, var6.textureWidth);
                        var5 = Math.min(var5, var6.textureHeight);
                        return var4 > 0 && var5 > 0 ? (long)var4 << 32 | var5 & 4294967295L : -1L;
                     } else {
                        return -1L;
                     }
                  } else {
                     return -1L;
                  }
               } else {
                  return -1L;
               }
            } else {
               return -1L;
            }
         } else {
            return -1L;
         }
      } else {
         return -1L;
      }
   }

   public static int handle(long var0) {
      return (int)(var0 >>> 32);
   }

   public static int process(long var0) {
      return (int)var0;
   }

   public static MainMenuBackgroundRenderer.Bounds process(MinecraftClient var0, int var1, int var2) {
      long var3 = handle(var0, var1, var2);
      if (var3 < 0L) {
         return null;
      }

      int var6 = var0.getFramebuffer().getColorAttachment() instanceof GlTexture var7 ? var7.getGlId() : 0;
      return new MainMenuBackgroundRenderer.Bounds(handle(var3), process(var3), var6);
   }

   private static String[] resolve() {
      String[] var0 = new String[14];

      for (int var1 = 0; var1 < var0.length; var1++) {
         var0[var1] = "uTrail[" + var1 + "]";
      }

      return var0;
   }

   @Override
   public void close() {
      this.windowConvert.close();
      this.presetWrite.close();
      this.colorMeasure.close();
      this.animationSchedule.close();
      this.rendererScan.close();
      this.sourceBuild.close();
      this.outputCollapse.close();
      this.profileInvoke.close();
      this.sourceSchedule.close();
      this.timerRender.close();
      this.scaleSave.close();
      this.colorCompute.close();
      this.scaleAdapt.close();
      this.textureRun.close();
      this.windowProcess = false;
      this.listenerRun = -1.0F;
      this.layoutSave = 0.0F;
      this.resourceClamp = false;
      if (this.effectScan != null) {
         this.effectScan.close();
         this.effectScan = null;
      }

      this.moduleCollect.close();
      this.packetSave = null;
      this.entryAnimate = null;
      this.playerCollect = null;
      this.stateApply = null;
      this.matrixFilter = null;
      this.layerSample = null;
      this.worldSend = null;
      this.targetWrite = null;
      this.resultEncode = null;
      this.messageParse = null;
      this.actionConvert = null;
      this.animationExpand = null;
      this.playerRun = null;
      this.matrixRender = null;
      this.moduleTick = null;
      this.playerCollapse = null;
      this.optionAdvance = null;
      this.indexBind = null;
      this.actionRead = null;
      this.handlerRun = false;
      this.keyCheck = false;
      this.layerProject = false;
   }

   public void handle(int var1, int var2) {
      try {
         this.windowConvert.close();
         this.presetWrite.close();
         this.colorMeasure.close();
         this.animationSchedule.close();
         this.rendererScan.close();
         this.sourceBuild.close();
         this.outputCollapse.close();
         this.profileInvoke.close();
         this.sourceSchedule.close();
         this.timerRender.close();
         this.scaleSave.close();
         this.colorCompute.close();
         this.scaleAdapt.close();
         this.textureRun.close();
         this.windowProcess = false;
         this.listenerRun = -1.0F;
         this.layoutSave = 0.0F;
         this.resourceClamp = false;
      } catch (Throwable var4) {
      }
   }

   public record Bounds(int width, int height, int colorTexture) {
   }
}
