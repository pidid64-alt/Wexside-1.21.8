package ru.wild.render.shader;

import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.texture.GlTexture;
import org.lwjgl.opengl.ARBDrawInstanced;
import org.lwjgl.opengl.ARBInstancedArrays;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL31;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.GL43;
import org.lwjgl.opengl.GLCapabilities;
import org.lwjgl.opengl.GLDebugMessageCallback;
import org.lwjgl.opengl.KHRDebug;
import ru.wild.render.OpenGlStateSnapshot;
import ru.wild.render.VertexLayoutBinding;
import ru.wild.render.texture.DepthRenderTarget;
import ru.wild.util.io.ResourceReader;

public final class ShaderRenderer {
   private static final int instance = 4096;
   private static final int data = 16;
   private static final int context = 144;
   private static final int config = 0;
   private static final int state = 1;
   private static final int cache = 2;
   private static final int output = 3;
   private static final int current = 16;
   private static final int active = 32;
   private static final int mode = 64;
   private static final int selection = 128;
   private static final int enabled = 67108864;
   private static final int renderer = 134217728;
   private static final int handler = 268435456;
   private static final int animationDraw = 29;
   private static final int pointEncode = 7;
   private static final float[] animator = new float[]{1.0F, 0.0F, 0.0F, 0.0F, 1.0F, 0.0F, 0.0F, 0.0F, 1.0F};
   private final boolean source;
   private final boolean target;
   private final boolean pending;
   private final boolean previous;
   private final ShaderBuildReporter latest;
   private final int summary;
   private final int matrixBlend;
   private final int vectorMatch;
   private final ByteBuffer itemProject;
   private int responseCompute = 0;
   private OpenGlStateSnapshot.NetworkState providerFetch;
   private int profileDraw;
   private int vectorPerform;
   private int eventAttach = -1;
   private int serverRead = -1;
   private boolean positionAdvance = false;
   private int frameCheck = 0;
   private int moduleCollect = 0;
   private int providerClose = Integer.MAX_VALUE;
   private int presetSave = Integer.MAX_VALUE;
   private float windowConvert = 0.0F;
   private float presetWrite = 0.0F;
   private float colorMeasure = 0.0F;
   private float animationSchedule = 0.0F;
   private final Int2IntOpenHashMap rendererScan = new Int2IntOpenHashMap(16);
   private final int[] sourceBuild = new int[16];
   private final int[] outputCollapse = new int[16];
   private int profileInvoke = 0;
   private int sourceSchedule = -1;
   private boolean timerRender = false;
   private int scaleSave = 0;
   private int colorCompute = 0;
   private int scaleAdapt = 0;
   private int textureRun = 0;
   private int indexBind = 0;
   private int actionRead = 0;
   private int configCollapse = 0;
   private int dataValidate = 0;
   private float scaleRender = 0.5F;
   private float clientRefresh = 0.5F;
   private int keyFilter = 0;
   private int requestAdapt = 0;
   private int timerMeasure = 0;
   private int vectorEncode = 0;
   private final DepthRenderTarget requestReceive = new DepthRenderTarget();
   private int windowProcess = 0;
   private int packetSave = 0;
   private int entryAnimate = 0;
   private ShaderBuildReporter playerCollect;
   private int stateApply = -1;
   private final ShaderRenderer.ColorState matrixFilter = new ShaderRenderer.ColorState();
   private int layerSample = 0;
   private int worldSend = 0;
   private ShaderBuildReporter targetWrite;
   private int resultEncode = -1;
   private int messageParse = -1;
   private int providerRead = -1;
   private int matrixBlend2 = -1;
   private int scalePerform = -1;
   private int contextExpand = -1;
   private int keyProcess = -1;
   private int actionConvert = -1;
   private int screenRead = -1;
   private int animationExpand = -1;
   private int playerRun = -1;
   private int matrixRender = -1;
   private int moduleTick = -1;
   private int playerCollapse = -1;
   private final float[] optionAdvance = new float[24];
   private final List<ShaderRenderer.ColorState> effectScan = new ArrayList<>();
   private int optionParse = 0;
   private int pointSubmit = 0;
   private int listenerPerform = 0;
   private ShaderBuildReporter configMatch;
   private int actionRender = -1;
   private int playerApply = -1;
   private int bufferAdapt = -1;
   private int playerUpdate = -1;
   private int packetRead = -1;
   private int rendererCancel = -1;
   private int eventReceive = -1;
   private int screenSubmit = -1;
   private int cacheHandle = -1;
   private int rangeRelease = -1;
   private int indexSave = -1;
   private int indexCheck = -1;
   private int settingSchedule = -1;
   private int inputAcquire = -1;
   private final float[] listenerRun = new float[24];
   private ShaderBuildReporter indexLoad;
   private int layoutSave = -1;
   private int blockRun = -1;
   private int playerEvaluate = -1;
   private int outputFetch = -1;
   private int scaleParse = -1;
   private int sessionEncode = -1;
   private int elementTick = -1;
   private int regionAlign = -1;
   private int resourceClamp = -1;
   private int handlerRun = -1;
   private int keyCheck = -1;
   private int layerProject = -1;
   private int entityFilter = -1;
   private int layerSample2 = -1;
   private int sourceCancel = -1;
   private int eventSend = -1;
   private int providerOffset = -1;
   private int messageParse2 = -1;
   private ShaderBuildReporter shaderProject;
   private int inputInvoke = -1;
   private int optionFetch = -1;
   private int eventCollapse = -1;
   private int stateAttach = -1;
   private int worldEvaluate = -1;
   private int playerProject = -1;
   private int playerMatch = -1;
   private int cacheClose = -1;
   private int scaleSetup = -1;
   private int indexSynchronize = -1;
   private int taskInterpolate = -1;
   private int sourceRefresh = -1;
   private int playerSave = -1;
   private final OpenGlStateSnapshot.NetworkState requestRun = new OpenGlStateSnapshot.NetworkState();
   private GLDebugMessageCallback frameProject;
   private final BlurUpsampleShader dataRelease = new BlurUpsampleShader(32856, 5121);
   private final BlurUpsampleShader vectorRun = new BlurUpsampleShader(32856, 5121);
   private int providerSynchronize = 0;
   private int pathProcess = 0;
   private int positionReset = 0;
   private float effectApply = 1.0F;
   private float cacheHandle2 = 1.0F;
   private int sessionAdvance = 0;
   private int keySample = 0;
   private int playerPerform = 0;
   private int taskLoad = 0;
   private int pointSend = 0;
   private boolean clientSubmit = false;
   private boolean pointSample = false;
   private static final ConcurrentHashMap<Integer, Long> regionRefresh = new ConcurrentHashMap<>();
   private static final AtomicLong pathCheck = new AtomicLong();
   private static final AtomicInteger contextParse = new AtomicInteger();
   private static final long optionStop = 5000L;
   private static final long mousePrepare = 1000L;
   private static final int sessionAdapt = 8;

   private static int handle(int var0) {
      int var1 = var0 >> 16 & 0xFF;
      int var2 = var0 >> 8 & 0xFF;
      int var3 = var0 & 0xFF;
      int var4 = var0 >>> 24 & 0xFF;
      return var4 << 24 | var3 << 16 | var2 << 8 | var1;
   }

   private void apply() {
      this.process(1);
   }

   private void process(int var1) {
      if (var1 > 0) {
         if (var1 > 4096) {
            throw new IllegalArgumentException("additionalInstances must be between 1 and 4096");
         }

         if (this.responseCompute + var1 > 4096) {
            this.process();
            this.responseCompute = 0;
            this.itemProject.clear();
            this.execute();
         }
      }
   }

   private void execute() {
      this.rendererScan.clear();
      this.profileInvoke = 0;
   }

   public ShaderRenderer() {
      this.rendererScan.defaultReturnValue(-1);
      GLCapabilities var1 = GL.getCapabilities();
      this.source = var1.OpenGL43;
      this.target = var1.OpenGL43 || var1.GL_KHR_debug;
      boolean var2 = var1.glVertexAttribDivisor != 0L;
      boolean var3 = var1.glVertexAttribDivisorARB != 0L;
      boolean var4 = var1.glDrawArraysInstanced != 0L;
      boolean var5 = var1.glDrawArraysInstancedARB != 0L;
      boolean var6 = var2 || var3;
      boolean var7 = var4 || var5;
      this.pending = !var2 && var3;
      this.previous = !var4 && var5;
      if (this.source || var6 && var7) {
         String var8 = this.source ? "assets/wild/shaders/shape.vert" : "assets/wild/shaders/shape_compat.vert";
         String var9 = ResourceReader.handle(var8);
         String var10 = ResourceReader.handle("assets/wild/shaders/shape.frag");
         this.latest = new ShaderBuildReporter(var9, var10);
         this.summary = GL30.glGenVertexArrays();
         int var11 = GL15.glGenBuffers();
         GL30.glBindVertexArray(this.summary);
         GL15.glBindBuffer(34962, var11);
         float[] var12 = new float[]{-0.02F, -0.02F, 2.04F, -0.02F, -0.02F, 2.04F};
         GL15.glBufferData(34962, var12, 35044);
         GL20.glEnableVertexAttribArray(0);
         GL20.glVertexAttribPointer(0, 2, 5126, false, 0, 0L);
         int var13 = 0;
         if (!this.source) {
            var13 = GL15.glGenBuffers();
            GL15.glBindBuffer(34962, var13);
            GL15.glBufferData(34962, 589824L, 35040);
            short var14 = 144;
            long var15 = 0L;
            GL20.glEnableVertexAttribArray(1);
            GL20.glVertexAttribPointer(1, 4, 5126, false, var14, var15);
            this.apply(1, 1);
            var15 += 16L;
            GL20.glEnableVertexAttribArray(2);
            GL20.glVertexAttribPointer(2, 4, 5126, false, var14, var15);
            this.apply(2, 1);
            var15 += 16L;
            GL20.glEnableVertexAttribArray(3);
            GL30.glVertexAttribIPointer(3, 4, 5124, var14, var15);
            this.apply(3, 1);
            var15 += 16L;
            GL20.glEnableVertexAttribArray(4);
            GL20.glVertexAttribPointer(4, 4, 5126, false, var14, var15);
            this.apply(4, 1);
            var15 += 16L;
            GL20.glEnableVertexAttribArray(5);
            GL20.glVertexAttribPointer(5, 4, 5126, false, var14, var15);
            this.apply(5, 1);
            var15 += 16L;
            GL20.glEnableVertexAttribArray(6);
            GL30.glVertexAttribIPointer(6, 4, 5125, var14, var15);
            this.apply(6, 1);
            var15 += 16L;
            GL20.glEnableVertexAttribArray(7);
            GL20.glVertexAttribPointer(7, 4, 5126, false, var14, var15);
            this.apply(7, 1);
            var15 += 16L;
            GL20.glEnableVertexAttribArray(8);
            GL20.glVertexAttribPointer(8, 4, 5126, false, var14, var15);
            this.apply(8, 1);
            var15 += 16L;
            GL20.glEnableVertexAttribArray(9);
            GL30.glVertexAttribIPointer(9, 1, 5124, var14, var15);
            this.apply(9, 1);
            var15 += 4L;
            GL20.glEnableVertexAttribArray(10);
            GL30.glVertexAttribIPointer(10, 1, 5124, var14, var15);
            this.apply(10, 1);
            GL15.glBindBuffer(34962, 0);
         }

         GL15.glBindBuffer(34962, 0);
         GL30.glBindVertexArray(0);
         this.vectorMatch = var13;
         this.itemProject = ByteBuffer.allocateDirect(589824).order(ByteOrder.nativeOrder());
         if (this.source) {
            this.matrixBlend = GL15.glGenBuffers();
            GL15.glBindBuffer(37074, this.matrixBlend);
            GL15.glBufferData(37074, 589824L, 35040);
            GL15.glBindBuffer(37074, 0);
         } else {
            this.matrixBlend = 0;
         }

         if (this.target) {
            this.handle(var1);
         }
      } else {
         throw new IllegalStateException("OpenGL instanced rendering is required when shader storage buffers are unavailable");
      }
   }

   private void prepare() {
      if (this.packetSave == 0) {
         this.packetSave = GL30.glGenVertexArrays();
         this.entryAnimate = GL15.glGenBuffers();
         GL30.glBindVertexArray(this.packetSave);
         GL15.glBindBuffer(34962, this.entryAnimate);
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
      }
   }

   private ShaderBuildReporter check() {
      if (this.playerCollect != null) {
         return this.playerCollect;
      }

      String var1 = ResourceReader.handle("assets/wild/shaders/blur/blur_fullscreen.vert");
      String var2 = "#version 330 core\nlayout(location = 0) out vec4 fragColor;\nin vec2 vUv;\nuniform sampler2D uSource;\nvoid main() {\n    fragColor = texture(uSource, vUv);\n}";
      this.playerCollect = new ShaderBuildReporter(var1, var2);
      this.stateApply = this.playerCollect.handle("uSource");
      return this.playerCollect;
   }

   private void onTick() {
      if (this.layerSample == 0) {
         this.layerSample = GL30.glGenVertexArrays();
         this.worldSend = GL15.glGenBuffers();
         GL30.glBindVertexArray(this.layerSample);
         GL15.glBindBuffer(34962, this.worldSend);
         GL15.glBufferData(34962, 96L, 35040);
         byte var1 = 16;
         GL20.glEnableVertexAttribArray(0);
         GL20.glVertexAttribPointer(0, 2, 5126, false, var1, 0L);
         GL20.glEnableVertexAttribArray(1);
         GL20.glVertexAttribPointer(1, 2, 5126, false, var1, 8L);
         GL15.glBindBuffer(34962, 0);
         GL30.glBindVertexArray(0);
      }

      if (this.targetWrite == null) {
         this.targetWrite = ShaderBuildReporter.handle("assets/wild/shaders/postfx/scroll_layer.vert", "assets/wild/shaders/postfx/scroll_layer.frag");
         this.resultEncode = this.targetWrite.handle("uSource");
         this.messageParse = this.targetWrite.handle("uViewport");
         this.providerRead = this.targetWrite.handle("uSize");
         this.matrixBlend2 = this.targetWrite.handle("uTextureSize");
         this.scalePerform = this.targetWrite.handle("uRadii");
         this.contextExpand = this.targetWrite.handle("uClipRect");
         this.keyProcess = this.targetWrite.handle("uClipRadii");
         this.actionConvert = this.targetWrite.handle("uFadePx");
         this.screenRead = this.targetWrite.handle("uEdgeBlurPx");
         this.animationExpand = this.targetWrite.handle("uMotionBlurPx");
         this.playerRun = this.targetWrite.handle("uMotionStrength");
         this.matrixRender = this.targetWrite.handle("uFocusStrength");
         this.moduleTick = this.targetWrite.handle("uDirection");
         this.playerCollapse = this.targetWrite.handle("uAlpha");
      }
   }

   private void select() {
      if (this.pointSubmit == 0) {
         this.pointSubmit = GL30.glGenVertexArrays();
         this.listenerPerform = GL15.glGenBuffers();
         GL30.glBindVertexArray(this.pointSubmit);
         GL15.glBindBuffer(34962, this.listenerPerform);
         GL15.glBufferData(34962, 96L, 35040);
         byte var1 = 16;
         GL20.glEnableVertexAttribArray(0);
         GL20.glVertexAttribPointer(0, 2, 5126, false, var1, 0L);
         GL20.glEnableVertexAttribArray(1);
         GL20.glVertexAttribPointer(1, 2, 5126, false, var1, 8L);
         GL15.glBindBuffer(34962, 0);
         GL30.glBindVertexArray(0);
      }

      if (this.configMatch == null) {
         this.configMatch = ShaderBuildReporter.handle("assets/wild/shaders/card_transition.vert", "assets/wild/shaders/card_transition.frag");
         this.actionRender = this.configMatch.handle("u_texture");
         this.playerApply = this.configMatch.handle("u_viewport");
         this.bufferAdapt = this.configMatch.handle("u_resolution");
         this.playerUpdate = this.configMatch.handle("u_time");
         this.packetRead = this.configMatch.handle("u_progress");
         this.rendererCancel = this.configMatch.handle("u_color");
         this.eventReceive = this.configMatch.handle("u_borderColor");
         this.screenSubmit = this.configMatch.handle("u_emissiveColor");
         this.cacheHandle = this.configMatch.handle("u_emissiveColor2");
         this.rangeRelease = this.configMatch.handle("u_radius");
         this.indexSave = this.configMatch.handle("u_alpha");
         this.indexCheck = this.configMatch.handle("u_clipRect");
         this.settingSchedule = this.configMatch.handle("u_clipRadii");
         this.inputAcquire = this.configMatch.handle("u_textureScale");
      }
   }

   private void refresh() {
      this.select();
      if (this.indexLoad == null) {
         this.indexLoad = ShaderBuildReporter.handle("assets/wild/shaders/card_transition.vert", "assets/wild/shaders/entity/nametag_plasma.frag");
         this.layoutSave = this.indexLoad.handle("u_texture");
         this.blockRun = this.indexLoad.handle("u_viewport");
         this.playerEvaluate = this.indexLoad.handle("u_resolution");
         this.outputFetch = this.indexLoad.handle("u_time");
         this.scaleParse = this.indexLoad.handle("u_progress");
         this.sessionEncode = this.indexLoad.handle("u_contentReveal");
         this.elementTick = this.indexLoad.handle("u_focus");
         this.regionAlign = this.indexLoad.handle("u_threat");
         this.resourceClamp = this.indexLoad.handle("u_exposure");
         this.handlerRun = this.indexLoad.handle("u_color");
         this.keyCheck = this.indexLoad.handle("u_borderColor");
         this.layerProject = this.indexLoad.handle("u_emissiveColor");
         this.entityFilter = this.indexLoad.handle("u_emissiveColor2");
         this.layerSample2 = this.indexLoad.handle("u_radius");
         this.sourceCancel = this.indexLoad.handle("u_alpha");
         this.eventSend = this.indexLoad.handle("u_clipRect");
         this.providerOffset = this.indexLoad.handle("u_clipRadii");
         this.messageParse2 = this.indexLoad.handle("u_textureScale");
      }
   }

   private void render() {
      this.select();
      if (this.shaderProject == null) {
         this.shaderProject = ShaderBuildReporter.handle("assets/wild/shaders/card_transition.vert", "assets/wild/shaders/fbo_mask.frag");
         this.inputInvoke = this.shaderProject.handle("u_texture");
         this.optionFetch = this.shaderProject.handle("u_viewport");
         this.eventCollapse = this.shaderProject.handle("u_resolution");
         this.stateAttach = this.shaderProject.handle("u_time");
         this.worldEvaluate = this.shaderProject.handle("u_progress");
         this.playerProject = this.shaderProject.handle("u_color");
         this.playerMatch = this.shaderProject.handle("u_borderColor");
         this.cacheClose = this.shaderProject.handle("u_emissiveColor");
         this.scaleSetup = this.shaderProject.handle("u_radius");
         this.indexSynchronize = this.shaderProject.handle("u_alpha");
         this.taskInterpolate = this.shaderProject.handle("u_clipRect");
         this.sourceRefresh = this.shaderProject.handle("u_clipRadii");
         this.playerSave = this.shaderProject.handle("u_textureScale");
      }
   }

   public ShaderRenderer.PrimaryColorState handle(int var1, int var2) {
      return this.handle(this.matrixFilter, var1, var2, false);
   }

   public ShaderRenderer.PrimaryColorState process(int var1, int var2) {
      if (var1 > 0 && var2 > 0 && this.profileDraw > 0 && this.vectorPerform > 0) {
         int var3 = this.optionParse;
         ShaderRenderer.ColorState var4 = this.compute(var3);
         this.optionParse++;

         try {
            ShaderRenderer.PrimaryColorState var5 = this.handle(var4, var1, var2, true);
            if (var5 == null) {
               this.optionParse = var3;
            }

            return var5;
         } catch (RuntimeException | Error var6) {
            this.optionParse = var3;
            throw var6;
         }
      } else {
         return null;
      }
   }

   private ShaderRenderer.ColorState compute(int var1) {
      while (this.effectScan.size() <= var1) {
         this.effectScan.add(new ShaderRenderer.ColorState());
      }

      return this.effectScan.get(var1);
   }

   private ShaderRenderer.PrimaryColorState handle(ShaderRenderer.ColorState var1, int var2, int var3, boolean var4) {
      this.process();
      if (var2 > 0 && var3 > 0 && this.profileDraw > 0 && this.vectorPerform > 0) {
         int var5 = var2;
         int var6 = var3;
         int var7 = this.profileDraw;
         int var8 = this.vectorPerform;
         boolean var9 = this.positionAdvance;
         int var10 = this.frameCheck;
         int var11 = this.moduleCollect;
         int var12 = this.providerClose;
         int var13 = this.presetSave;
         float var14 = this.windowConvert;
         float var15 = this.presetWrite;
         float var16 = this.colorMeasure;
         float var17 = this.animationSchedule;
         boolean var18 = this.clientSubmit;
         OpenGlStateSnapshot.NetworkState var19 = OpenGlStateSnapshot.process(var1.state);

         try {
            this.process(var1, var5, var6, var4);
            ShaderRenderer.PrimaryColorState var20 = var1.cache
               .handle(
                  var1.data,
                  var5,
                  var6,
                  var1.context,
                  var1.config,
                  var19,
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
                  var17,
                  var18,
                  var4
               );
            GL30.glBindFramebuffer(36160, var1.instance);
            GL11.glDrawBuffer(36064);
            GL11.glViewport(0, 0, var5, var6);
            GL11.glEnable(3089);
            GL11.glScissor(0, 0, var5, var6);
            GL11.glDisable(2929);
            GL11.glDisable(2884);
            GL11.glDisable(36281);
            GL11.glColorMask(true, true, true, true);
            GL11.glDepthMask(false);
            GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            GL11.glClear(16384);
            GL11.glDisable(3089);
            this.profileDraw = var5;
            this.vectorPerform = var6;
            this.positionAdvance = false;
            this.frameCheck = 0;
            this.moduleCollect = 0;
            this.providerClose = var5;
            this.presetSave = var6;
            this.windowConvert = 0.0F;
            this.presetWrite = 0.0F;
            this.colorMeasure = 0.0F;
            this.animationSchedule = 0.0F;
            this.clientSubmit = false;
            this.latest.handle();
            GL30.glBindVertexArray(this.summary);
            if (this.sourceSchedule == -1) {
               this.sourceSchedule = this.latest.handle("uViewport");
            }

            GL20.glUniform2f(this.sourceSchedule, var5, var6);
            this.tick();
            this.execute();
            return var20;
         } catch (RuntimeException | Error var21) {
            this.profileDraw = var7;
            this.vectorPerform = var8;
            this.positionAdvance = var9;
            this.frameCheck = var10;
            this.moduleCollect = var11;
            this.providerClose = var12;
            this.presetSave = var13;
            this.windowConvert = var14;
            this.presetWrite = var15;
            this.colorMeasure = var16;
            this.animationSchedule = var17;
            this.clientSubmit = var18;
            OpenGlStateSnapshot.compute(var19);
            this.latest.handle();
            GL30.glBindVertexArray(this.summary);
            if (this.sourceSchedule == -1) {
               this.sourceSchedule = this.latest.handle("uViewport");
            }

            GL20.glUniform2f(this.sourceSchedule, this.profileDraw, this.vectorPerform);
            this.tick();
            throw var21;
         }
      } else {
         return null;
      }
   }

   public void handle(ShaderRenderer.PrimaryColorState var1) {
      this.process();
      if (var1 != null) {
         this.profileDraw = var1.execute();
         this.vectorPerform = var1.prepare();
         this.positionAdvance = var1.check();
         this.frameCheck = var1.onTick();
         this.moduleCollect = var1.select();
         this.providerClose = var1.refresh();
         this.presetSave = var1.render();
         this.windowConvert = var1.tick();
         this.presetWrite = var1.drawAnimation();
         this.colorMeasure = var1.encodePoint();
         this.animationSchedule = var1.animate();
         this.clientSubmit = var1.load();
         OpenGlStateSnapshot.compute(var1.apply());
         this.latest.handle();
         GL30.glBindVertexArray(this.summary);
         if (this.sourceSchedule == -1) {
            this.sourceSchedule = this.latest.handle("uViewport");
         }

         GL20.glUniform2f(this.sourceSchedule, this.profileDraw, this.vectorPerform);
         this.tick();
         this.execute();
         if (var1.save()) {
            this.optionParse = Math.max(0, this.optionParse - 1);
         }
      }
   }
   public void handle(
      int var1,
      int var2,
      int var3,
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
      float var16,
      float var17,
      float var18,
      float[] var19,
      int var20,
      int var21,
      int var22,
      int var23,
      float var24,
      float var25,
      float var26,
      float var27
   ) {
      if (var1 > 0 && var2 > 0 && var3 > 0 && !(var6 <= 0.0F) && !(var7 <= 0.0F)) {
         this.process();
         float[] var28 = var19 != null && var19.length >= 6 ? var19 : animator;
         float var29 = var4;
         float var30 = var5;
         float var31 = var4 + var6;
         float var32 = var5 + var7;
         float var33 = handle(var28, var29, var30);
         float var34 = process(var28, var29, var30);
         float var35 = handle(var28, var31, var30);
         float var36 = process(var28, var31, var30);
         float var37 = handle(var28, var31, var32);
         float var38 = process(var28, var31, var32);
         float var39 = handle(var28, var29, var32);
         float var40 = process(var28, var29, var32);
         handle(this.optionAdvance, var33, var34, var35, var36, var37, var38, var39, var40);
         OpenGlStateSnapshot.NetworkState var41 = OpenGlStateSnapshot.process(this.requestRun);
         boolean var44 = false /* VF: Semaphore variable */;

         try {
            var44 = true;
            this.onTick();
            GL11.glDisable(3089);
            GL11.glDisable(2929);
            GL11.glDisable(2884);
            GL11.glDisable(36281);
            GL11.glEnable(3042);
            if (this.clientSubmit) {
               GL14.glBlendFuncSeparate(1, 1, 1, 771);
            } else {
               GL14.glBlendFuncSeparate(1, 771, 1, 771);
            }

            this.targetWrite.handle();
            if (this.resultEncode >= 0) {
               GL20.glUniform1i(this.resultEncode, 0);
            }

            if (this.messageParse >= 0) {
               GL20.glUniform2f(this.messageParse, this.profileDraw, this.vectorPerform);
            }

            if (this.providerRead >= 0) {
               GL20.glUniform2f(this.providerRead, var6, var7);
            }

            if (this.matrixBlend2 >= 0) {
               GL20.glUniform2f(this.matrixBlend2, var2, var3);
            }

            if (this.scalePerform >= 0) {
               GL20.glUniform4f(this.scalePerform, var8, var9, var10, var11);
            }

            if (this.contextExpand >= 0) {
               GL20.glUniform4f(this.contextExpand, var20, var21, var22, var23);
            }

            if (this.keyProcess >= 0) {
               GL20.glUniform4f(this.keyProcess, var24, var25, var26, var27);
            }

            if (this.actionConvert >= 0) {
               GL20.glUniform1f(this.actionConvert, Math.max(0.0F, var12));
            }

            if (this.screenRead >= 0) {
               GL20.glUniform1f(this.screenRead, Math.max(0.0F, var13));
            }

            if (this.animationExpand >= 0) {
               GL20.glUniform1f(this.animationExpand, Math.max(0.0F, var14));
            }

            if (this.playerRun >= 0) {
               GL20.glUniform1f(this.playerRun, Math.max(0.0F, Math.min(1.0F, var15)));
            }

            if (this.matrixRender >= 0) {
               GL20.glUniform1f(this.matrixRender, Math.max(0.0F, Math.min(1.0F, var16)));
            }

            if (this.moduleTick >= 0) {
               GL20.glUniform1f(this.moduleTick, var17 < 0.0F ? -1.0F : 1.0F);
            }

            if (this.playerCollapse >= 0) {
               GL20.glUniform1f(this.playerCollapse, Math.max(0.0F, Math.min(1.0F, var18)));
            }

            GL13.glActiveTexture(33984);
            GL11.glBindTexture(3553, var1);
            GL30.glBindVertexArray(this.layerSample);
            GL15.glBindBuffer(34962, this.worldSend);
            GL15.glBufferSubData(34962, 0L, this.optionAdvance);
            ShaderViewportTracker.handle().handle(2);
            GL11.glDrawArrays(4, 0, 6);
            var44 = false;
         } finally {
            if (var44) {
               GL30.glBindVertexArray(0);
               GL15.glBindBuffer(34962, 0);
               GL20.glUseProgram(0);
               OpenGlStateSnapshot.compute(var41);
               this.latest.handle();
               GL30.glBindVertexArray(this.summary);
               if (this.sourceSchedule == -1) {
                  this.sourceSchedule = this.latest.handle("uViewport");
               }

               GL20.glUniform2f(this.sourceSchedule, this.profileDraw, this.vectorPerform);
               this.tick();
            }
         }

         GL30.glBindVertexArray(0);
         GL15.glBindBuffer(34962, 0);
         GL20.glUseProgram(0);
         OpenGlStateSnapshot.compute(var41);
         this.latest.handle();
         GL30.glBindVertexArray(this.summary);
         if (this.sourceSchedule == -1) {
            this.sourceSchedule = this.latest.handle("uViewport");
         }

         GL20.glUniform2f(this.sourceSchedule, this.profileDraw, this.vectorPerform);
         this.tick();
      }
   }
   public void handle(
      int var1,
      int var2,
      int var3,
      int var4,
      int var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      int var11,
      int var12,
      int var13,
      int var14,
      float var15,
      float var16,
      float var17,
      float[] var18,
      int var19,
      int var20,
      int var21,
      int var22,
      float var23,
      float var24,
      float var25,
      float var26
   ) {
      if (var1 > 0 && var2 > 0 && var3 > 0 && var4 > 0 && var5 > 0 && !(var8 <= 0.0F) && !(var9 <= 0.0F)) {
         this.process();
         float[] var27 = var18 != null && var18.length >= 6 ? var18 : animator;
         float var28 = var6;
         float var29 = var7;
         float var30 = var6 + var8;
         float var31 = var7 + var9;
         float var32 = handle(var27, var28, var29);
         float var33 = process(var27, var28, var29);
         float var34 = handle(var27, var30, var29);
         float var35 = process(var27, var30, var29);
         float var36 = handle(var27, var30, var31);
         float var37 = process(var27, var30, var31);
         float var38 = handle(var27, var28, var31);
         float var39 = process(var27, var28, var31);
         handle(this.listenerRun, var32, var33, var34, var35, var36, var37, var38, var39);
         OpenGlStateSnapshot.NetworkState var40 = OpenGlStateSnapshot.process(this.requestRun);
         boolean var43 = false /* VF: Semaphore variable */;

         try {
            var43 = true;
            this.select();
            GL11.glDisable(3089);
            GL11.glDisable(2929);
            GL11.glDisable(2884);
            GL11.glDisable(36281);
            GL11.glEnable(3042);
            if (this.clientSubmit) {
               GL14.glBlendFuncSeparate(1, 1, 1, 771);
            } else {
               GL14.glBlendFuncSeparate(1, 771, 1, 771);
            }

            this.configMatch.handle();
            if (this.actionRender >= 0) {
               GL20.glUniform1i(this.actionRender, 0);
            }

            if (this.playerApply >= 0) {
               GL20.glUniform2f(this.playerApply, this.profileDraw, this.vectorPerform);
            }

            if (this.bufferAdapt >= 0) {
               GL20.glUniform2f(this.bufferAdapt, var8, var9);
            }

            if (this.playerUpdate >= 0) {
               GL20.glUniform1f(this.playerUpdate, var16);
            }

            if (this.packetRead >= 0) {
               GL20.glUniform1f(this.packetRead, Math.max(0.0F, Math.min(1.0F, var15)));
            }

            if (this.rendererCancel >= 0) {
               execute(this.rendererCancel, var11);
            }

            if (this.eventReceive >= 0) {
               execute(this.eventReceive, var12);
            }

            if (this.screenSubmit >= 0) {
               execute(this.screenSubmit, var13);
            }

            if (this.cacheHandle >= 0) {
               execute(this.cacheHandle, var14);
            }

            if (this.rangeRelease >= 0) {
               GL20.glUniform1f(this.rangeRelease, Math.max(0.0F, var10));
            }

            if (this.indexSave >= 0) {
               GL20.glUniform1f(this.indexSave, Math.max(0.0F, Math.min(1.0F, var17)));
            }

            if (this.indexCheck >= 0) {
               GL20.glUniform4f(this.indexCheck, var19, var20, var21, var22);
            }

            if (this.settingSchedule >= 0) {
               GL20.glUniform4f(this.settingSchedule, var23, var24, var25, var26);
            }

            if (this.inputAcquire >= 0) {
               GL20.glUniform2f(this.inputAcquire, (float)var4 / var2, (float)var5 / var3);
            }

            GL13.glActiveTexture(33984);
            GL11.glBindTexture(3553, var1);
            GL30.glBindVertexArray(this.pointSubmit);
            GL15.glBindBuffer(34962, this.listenerPerform);
            GL15.glBufferSubData(34962, 0L, this.listenerRun);
            ShaderViewportTracker.handle().handle(2);
            GL11.glDrawArrays(4, 0, 6);
            var43 = false;
         } finally {
            if (var43) {
               GL30.glBindVertexArray(0);
               GL15.glBindBuffer(34962, 0);
               GL20.glUseProgram(0);
               OpenGlStateSnapshot.compute(var40);
               this.latest.handle();
               GL30.glBindVertexArray(this.summary);
               if (this.sourceSchedule == -1) {
                  this.sourceSchedule = this.latest.handle("uViewport");
               }

               GL20.glUniform2f(this.sourceSchedule, this.profileDraw, this.vectorPerform);
               this.tick();
            }
         }

         GL30.glBindVertexArray(0);
         GL15.glBindBuffer(34962, 0);
         GL20.glUseProgram(0);
         OpenGlStateSnapshot.compute(var40);
         this.latest.handle();
         GL30.glBindVertexArray(this.summary);
         if (this.sourceSchedule == -1) {
            this.sourceSchedule = this.latest.handle("uViewport");
         }

         GL20.glUniform2f(this.sourceSchedule, this.profileDraw, this.vectorPerform);
         this.tick();
      }
   }
   public void handle(
      int var1,
      int var2,
      int var3,
      int var4,
      int var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      int var11,
      int var12,
      int var13,
      int var14,
      float var15,
      float var16,
      float var17,
      float var18,
      float var19,
      float var20,
      float var21,
      float[] var22,
      int var23,
      int var24,
      int var25,
      int var26,
      float var27,
      float var28,
      float var29,
      float var30
   ) {
      if (var1 > 0 && var2 > 0 && var3 > 0 && var4 > 0 && var5 > 0 && !(var8 <= 0.0F) && !(var9 <= 0.0F)) {
         this.process();
         float[] var31 = var22 != null && var22.length >= 6 ? var22 : animator;
         float var32 = var6;
         float var33 = var7;
         float var34 = var6 + var8;
         float var35 = var7 + var9;
         float var36 = handle(var31, var32, var33);
         float var37 = process(var31, var32, var33);
         float var38 = handle(var31, var34, var33);
         float var39 = process(var31, var34, var33);
         float var40 = handle(var31, var34, var35);
         float var41 = process(var31, var34, var35);
         float var42 = handle(var31, var32, var35);
         float var43 = process(var31, var32, var35);
         handle(this.listenerRun, var36, var37, var38, var39, var40, var41, var42, var43);
         OpenGlStateSnapshot.NetworkState var44 = OpenGlStateSnapshot.process(this.requestRun);
         boolean var47 = false /* VF: Semaphore variable */;

         try {
            var47 = true;
            this.refresh();
            GL11.glDisable(3089);
            GL11.glDisable(2929);
            GL11.glDisable(2884);
            GL11.glDisable(36281);
            GL11.glEnable(3042);
            if (this.clientSubmit) {
               GL14.glBlendFuncSeparate(1, 1, 1, 771);
            } else {
               GL14.glBlendFuncSeparate(1, 771, 1, 771);
            }

            this.indexLoad.handle();
            if (this.layoutSave >= 0) {
               GL20.glUniform1i(this.layoutSave, 0);
            }

            if (this.blockRun >= 0) {
               GL20.glUniform2f(this.blockRun, this.profileDraw, this.vectorPerform);
            }

            if (this.playerEvaluate >= 0) {
               GL20.glUniform2f(this.playerEvaluate, var8, var9);
            }

            if (this.outputFetch >= 0) {
               GL20.glUniform1f(this.outputFetch, var17);
            }

            if (this.scaleParse >= 0) {
               GL20.glUniform1f(this.scaleParse, Math.max(0.0F, Math.min(1.0F, var15)));
            }

            if (this.sessionEncode >= 0) {
               GL20.glUniform1f(this.sessionEncode, Math.max(0.0F, Math.min(1.0F, var16)));
            }

            if (this.elementTick >= 0) {
               GL20.glUniform1f(this.elementTick, Math.max(0.0F, Math.min(1.0F, var18)));
            }

            if (this.regionAlign >= 0) {
               GL20.glUniform1f(this.regionAlign, Math.max(0.0F, Math.min(1.0F, var19)));
            }

            if (this.resourceClamp >= 0) {
               GL20.glUniform1f(this.resourceClamp, Math.max(0.0F, Math.min(1.0F, var20)));
            }

            if (this.handlerRun >= 0) {
               execute(this.handlerRun, var11);
            }

            if (this.keyCheck >= 0) {
               execute(this.keyCheck, var12);
            }

            if (this.layerProject >= 0) {
               execute(this.layerProject, var13);
            }

            if (this.entityFilter >= 0) {
               execute(this.entityFilter, var14);
            }

            if (this.layerSample2 >= 0) {
               GL20.glUniform1f(this.layerSample2, Math.max(0.0F, var10));
            }

            if (this.sourceCancel >= 0) {
               GL20.glUniform1f(this.sourceCancel, Math.max(0.0F, Math.min(1.0F, var21)));
            }

            if (this.eventSend >= 0) {
               GL20.glUniform4f(this.eventSend, var23, var24, var25, var26);
            }

            if (this.providerOffset >= 0) {
               GL20.glUniform4f(this.providerOffset, var27, var28, var29, var30);
            }

            if (this.messageParse2 >= 0) {
               GL20.glUniform2f(this.messageParse2, (float)var4 / var2, (float)var5 / var3);
            }

            GL13.glActiveTexture(33984);
            GL11.glBindTexture(3553, var1);
            GL30.glBindVertexArray(this.pointSubmit);
            GL15.glBindBuffer(34962, this.listenerPerform);
            GL15.glBufferSubData(34962, 0L, this.listenerRun);
            ShaderViewportTracker.handle().handle(2);
            GL11.glDrawArrays(4, 0, 6);
            var47 = false;
         } finally {
            if (var47) {
               GL30.glBindVertexArray(0);
               GL15.glBindBuffer(34962, 0);
               GL20.glUseProgram(0);
               OpenGlStateSnapshot.compute(var44);
               this.latest.handle();
               GL30.glBindVertexArray(this.summary);
               if (this.sourceSchedule == -1) {
                  this.sourceSchedule = this.latest.handle("uViewport");
               }

               GL20.glUniform2f(this.sourceSchedule, this.profileDraw, this.vectorPerform);
               this.tick();
            }
         }

         GL30.glBindVertexArray(0);
         GL15.glBindBuffer(34962, 0);
         GL20.glUseProgram(0);
         OpenGlStateSnapshot.compute(var44);
         this.latest.handle();
         GL30.glBindVertexArray(this.summary);
         if (this.sourceSchedule == -1) {
            this.sourceSchedule = this.latest.handle("uViewport");
         }

         GL20.glUniform2f(this.sourceSchedule, this.profileDraw, this.vectorPerform);
         this.tick();
      }
   }
   public void handle(
      int var1,
      int var2,
      int var3,
      int var4,
      int var5,
      float var6,
      float var7,
      float var8,
      float var9,
      float var10,
      int var11,
      int var12,
      int var13,
      float var14,
      float var15,
      float var16,
      float[] var17,
      int var18,
      int var19,
      int var20,
      int var21,
      float var22,
      float var23,
      float var24,
      float var25
   ) {
      if (var1 > 0 && var2 > 0 && var3 > 0 && var4 > 0 && var5 > 0 && !(var8 <= 0.0F) && !(var9 <= 0.0F)) {
         this.process();
         float[] var26 = var17 != null && var17.length >= 6 ? var17 : animator;
         float var27 = var6;
         float var28 = var7;
         float var29 = var6 + var8;
         float var30 = var7 + var9;
         float var31 = handle(var26, var27, var28);
         float var32 = process(var26, var27, var28);
         float var33 = handle(var26, var29, var28);
         float var34 = process(var26, var29, var28);
         float var35 = handle(var26, var29, var30);
         float var36 = process(var26, var29, var30);
         float var37 = handle(var26, var27, var30);
         float var38 = process(var26, var27, var30);
         handle(this.listenerRun, var31, var32, var33, var34, var35, var36, var37, var38);
         OpenGlStateSnapshot.NetworkState var39 = OpenGlStateSnapshot.process(this.requestRun);
         boolean var42 = false /* VF: Semaphore variable */;

         try {
            var42 = true;
            this.render();
            GL11.glDisable(3089);
            GL11.glDisable(2929);
            GL11.glDisable(2884);
            GL11.glDisable(36281);
            GL11.glEnable(3042);
            if (this.clientSubmit) {
               GL14.glBlendFuncSeparate(1, 1, 1, 771);
            } else {
               GL14.glBlendFuncSeparate(1, 771, 1, 771);
            }

            this.shaderProject.handle();
            if (this.inputInvoke >= 0) {
               GL20.glUniform1i(this.inputInvoke, 0);
            }

            if (this.optionFetch >= 0) {
               GL20.glUniform2f(this.optionFetch, this.profileDraw, this.vectorPerform);
            }

            if (this.eventCollapse >= 0) {
               GL20.glUniform2f(this.eventCollapse, var8, var9);
            }

            if (this.stateAttach >= 0) {
               GL20.glUniform1f(this.stateAttach, var15);
            }

            if (this.worldEvaluate >= 0) {
               GL20.glUniform1f(this.worldEvaluate, Math.max(0.0F, Math.min(1.0F, var14)));
            }

            if (this.playerProject >= 0) {
               execute(this.playerProject, var11);
            }

            if (this.playerMatch >= 0) {
               execute(this.playerMatch, var12);
            }

            if (this.cacheClose >= 0) {
               execute(this.cacheClose, var13);
            }

            if (this.scaleSetup >= 0) {
               GL20.glUniform1f(this.scaleSetup, Math.max(0.0F, var10));
            }

            if (this.indexSynchronize >= 0) {
               GL20.glUniform1f(this.indexSynchronize, Math.max(0.0F, Math.min(1.0F, var16)));
            }

            if (this.taskInterpolate >= 0) {
               GL20.glUniform4f(this.taskInterpolate, var18, var19, var20, var21);
            }

            if (this.sourceRefresh >= 0) {
               GL20.glUniform4f(this.sourceRefresh, var22, var23, var24, var25);
            }

            if (this.playerSave >= 0) {
               GL20.glUniform2f(this.playerSave, (float)var4 / var2, (float)var5 / var3);
            }

            GL13.glActiveTexture(33984);
            GL11.glBindTexture(3553, var1);
            GL30.glBindVertexArray(this.pointSubmit);
            GL15.glBindBuffer(34962, this.listenerPerform);
            GL15.glBufferSubData(34962, 0L, this.listenerRun);
            ShaderViewportTracker.handle().handle(2);
            GL11.glDrawArrays(4, 0, 6);
            var42 = false;
         } finally {
            if (var42) {
               GL30.glBindVertexArray(0);
               GL15.glBindBuffer(34962, 0);
               GL20.glUseProgram(0);
               OpenGlStateSnapshot.compute(var39);
               this.latest.handle();
               GL30.glBindVertexArray(this.summary);
               if (this.sourceSchedule == -1) {
                  this.sourceSchedule = this.latest.handle("uViewport");
               }

               GL20.glUniform2f(this.sourceSchedule, this.profileDraw, this.vectorPerform);
               this.tick();
            }
         }

         GL30.glBindVertexArray(0);
         GL15.glBindBuffer(34962, 0);
         GL20.glUseProgram(0);
         OpenGlStateSnapshot.compute(var39);
         this.latest.handle();
         GL30.glBindVertexArray(this.summary);
         if (this.sourceSchedule == -1) {
            this.sourceSchedule = this.latest.handle("uViewport");
         }

         GL20.glUniform2f(this.sourceSchedule, this.profileDraw, this.vectorPerform);
         this.tick();
      }
   }

   public void compute(int var1, int var2) {
      if (var1 > 0 && var2 > 0) {
         this.providerFetch = OpenGlStateSnapshot.handle();
         this.profileDraw = var1;
         this.vectorPerform = var2;
         this.responseCompute = 0;
         this.itemProject.clear();
         this.execute();
         this.latest.handle();
         if (this.sourceSchedule == -1) {
            this.sourceSchedule = this.latest.handle("uViewport");
         }

         GL30.glBindVertexArray(this.summary);
         GL20.glUniform2f(this.sourceSchedule, var1, var2);
         this.sessionAdvance = 0;
         this.taskLoad = 0;
         this.pointSend = 0;
         this.keySample = 0;
         this.playerPerform = 0;
         this.clientSubmit = false;
         GL11.glDisable(2929);
         GL11.glDisable(2884);
         GL11.glDisable(3089);
         this.tick();
         GL11.glViewport(0, 0, var1, var2);
         GL11.glColorMask(true, true, true, true);
         if (!this.timerRender) {
            for (int var3 = 0; var3 < 16; var3++) {
               int var4 = this.latest.handle("uTextures[" + var3 + "]");
               if (var4 != -1) {
                  GL20.glUniform1i(var4, var3);
               }
            }

            this.timerRender = true;
         }
      } else {
         this.profileDraw = 0;
         this.vectorPerform = 0;
         this.responseCompute = 0;
         this.itemProject.clear();
         this.execute();
         this.load();
      }
   }

   public void handle() {
      this.process();
      GL30.glBindVertexArray(0);
      GL20.glUseProgram(0);
      if (this.providerFetch != null) {
         GL20.glUseProgram(this.providerFetch.responseCompute);
         GL30.glBindVertexArray(this.providerFetch.providerFetch);
         GL15.glBindBuffer(34962, this.providerFetch.profileDraw);
         GL15.glBindBuffer(34963, this.providerFetch.vectorPerform);
         GL13.glActiveTexture(this.providerFetch.eventAttach);
         GL11.glBindTexture(3553, this.providerFetch.serverRead);
         GL11.glPixelStorei(3317, this.providerFetch.frameCheck);
         handle(3089, this.providerFetch.cache);
         handle(2929, this.providerFetch.current);
         handle(2884, this.providerFetch.active);
         handle(3042, this.providerFetch.mode);
         handle(36281, this.providerFetch.selection);
         GL14.glBlendFuncSeparate(this.providerFetch.enabled, this.providerFetch.renderer, this.providerFetch.handler, this.providerFetch.animationDraw);
         GL11.glColorMask(this.providerFetch.pointEncode, this.providerFetch.animator, this.providerFetch.source, this.providerFetch.target);
         GL11.glDepthMask(this.providerFetch.pending);
         GL11.glViewport(this.providerFetch.state[0], this.providerFetch.state[1], this.providerFetch.state[2], this.providerFetch.state[3]);
         GL11.glScissor(this.providerFetch.output[0], this.providerFetch.output[1], this.providerFetch.output[2], this.providerFetch.output[3]);
      }

      this.providerFetch = null;
      this.responseCompute = 0;
      this.itemProject.clear();
   }

   private void apply(int var1, int var2) {
      if (this.pending) {
         ARBInstancedArrays.glVertexAttribDivisorARB(var1, var2);
      } else {
         GL33.glVertexAttribDivisor(var1, var2);
      }
   }

   private static void handle(int var0, boolean var1) {
      if (var1) {
         GL11.glEnable(var0);
      } else {
         GL11.glDisable(var0);
      }
   }

   private static void execute(int var0, int var1) {
      float var2 = (var1 >>> 24 & 0xFF) / 255.0F;
      float var3 = (var1 >>> 16 & 0xFF) / 255.0F;
      float var4 = (var1 >>> 8 & 0xFF) / 255.0F;
      float var5 = (var1 & 0xFF) / 255.0F;
      GL20.glUniform4f(var0, var3, var4, var5, var2);
   }

   public void process() {
      if (this.responseCompute > 0) {
         if (this.profileDraw > 0 && this.vectorPerform > 0) {
            this.itemProject.limit(this.responseCompute * 144);
            this.itemProject.position(0);
            int var1 = GL11.glGetInteger(34229);
            int var2 = GL11.glGetInteger(35725);
            GL30.glBindVertexArray(this.summary);
            this.latest.handle();
            GL20.glUniform2f(this.sourceSchedule, this.profileDraw, this.vectorPerform);
            GL11.glViewport(0, 0, this.profileDraw, this.vectorPerform);
            this.tick();
            GL11.glDisable(2929);
            GL11.glDisable(2884);
            GL11.glColorMask(true, true, true, true);
            if (this.source) {
               GL15.glBindBuffer(37074, this.matrixBlend);
               GL15.glBufferSubData(37074, 0L, this.itemProject);
               GL43.glBindBufferBase(37074, 0, this.matrixBlend);
            } else {
               GL15.glBindBuffer(34962, this.vectorMatch);
               GL15.glBufferSubData(34962, 0L, this.itemProject);
               GL15.glBindBuffer(34962, 0);
            }

            int var3 = GL11.glGetInteger(34016);
            int var4 = this.profileInvoke;

            for (int var5 = 0; var5 < var4; var5++) {
               GL13.glActiveTexture(33984 + var5);
               this.outputCollapse[var5] = GL11.glGetInteger(32873);
               int var6 = this.sourceBuild[var5];
               GL11.glBindTexture(3553, var6);
            }

            int var7 = Math.max(0, this.responseCompute);
            if (var7 > 0) {
               ShaderViewportTracker.handle().handle(var7);
            }

            if (this.source) {
               GL11.glDrawArrays(4, 0, this.responseCompute * 3);
            } else if (this.previous) {
               ARBDrawInstanced.glDrawArraysInstancedARB(4, 0, 3, this.responseCompute);
            } else {
               GL31.glDrawArraysInstanced(4, 0, 3, this.responseCompute);
            }

            for (int var8 = 0; var8 < var4; var8++) {
               GL13.glActiveTexture(33984 + var8);
               GL11.glBindTexture(3553, this.outputCollapse[var8]);
            }

            GL13.glActiveTexture(var3);
            GL30.glBindVertexArray(var1);
            GL20.glUseProgram(var2);
            this.responseCompute = 0;
            this.itemProject.clear();
            this.execute();
         } else {
            this.responseCompute = 0;
            this.itemProject.clear();
            this.execute();
         }
      }
   }

   public void handle(boolean var1) {
      this.clientSubmit = var1;
   }

   public void compute() {
      this.tick();
   }

   private void tick() {
      GL11.glEnable(3042);
      if (this.clientSubmit) {
         GL14.glBlendFuncSeparate(1, 1, 1, 771);
      } else {
         GL14.glBlendFuncSeparate(1, 771, 1, 771);
      }
   }

   public void process(boolean var1) {
      this.positionAdvance = var1;
      if (!var1) {
         this.windowConvert = 0.0F;
         this.presetWrite = 0.0F;
         this.colorMeasure = 0.0F;
         this.animationSchedule = 0.0F;
      }
   }

   public void handle(int var1, int var2, int var3, int var4, float var5, float var6, float var7, float var8) {
      this.frameCheck = var1;
      this.moduleCollect = var2;
      this.providerClose = var3;
      this.presetSave = var4;
      this.windowConvert = var5;
      this.presetWrite = var6;
      this.colorMeasure = var7;
      this.animationSchedule = var8;
   }

   public void handle(float[] var1) {
   }

   public void handle(float var1, float var2) {
      if (!Float.isFinite(var1) || !Float.isFinite(var2)) {
         throw new IllegalArgumentException("Blur capture scale must be finite");
      }

      if (!(var1 <= 0.0F) && !(var2 <= 0.0F)) {
         this.scaleRender = var1;
         this.clientRefresh = var2;
      } else {
         throw new IllegalArgumentException("Blur capture scale must be positive");
      }
   }

   private void handle(
      int var1,
      float var2,
      float var3,
      float var4,
      float var5,
      int var6,
      int var7,
      int var8,
      int var9,
      float var10,
      float var11,
      float var12,
      float var13,
      float var14,
      float[] var15,
      float var16,
      float var17,
      float var18,
      float var19,
      int var20,
      float var21,
      float var22,
      int var23
   ) {
      if (this.responseCompute >= 4096) {
         throw new IllegalStateException("Instance capacity exceeded without prior ensureInstanceCapacity call");
      }

      int var24 = this.responseCompute * 144;
      this.itemProject.position(var24);
      handle(this.itemProject, var15, var2, var3, var4, var5);
      int var25 = this.positionAdvance ? this.frameCheck : 0;
      int var26 = this.positionAdvance ? this.moduleCollect : 0;
      int var27 = this.positionAdvance ? this.providerClose : this.profileDraw;
      int var28 = this.positionAdvance ? this.presetSave : this.vectorPerform;
      float var29 = this.positionAdvance ? this.windowConvert : 0.0F;
      float var30 = this.positionAdvance ? this.presetWrite : 0.0F;
      float var31 = this.positionAdvance ? this.colorMeasure : 0.0F;
      float var32 = this.positionAdvance ? this.animationSchedule : 0.0F;
      this.itemProject.putInt(var25);
      this.itemProject.putInt(var26);
      this.itemProject.putInt(var27);
      this.itemProject.putInt(var28);
      this.itemProject.putFloat(var29);
      this.itemProject.putFloat(var30);
      this.itemProject.putFloat(var31);
      this.itemProject.putFloat(var32);
      this.itemProject.putFloat(var2);
      this.itemProject.putFloat(var3);
      this.itemProject.putFloat(var4);
      this.itemProject.putFloat(var5);
      this.itemProject.putInt(handle(var6));
      this.itemProject.putInt(handle(var7));
      this.itemProject.putInt(handle(var8));
      this.itemProject.putInt(handle(var9));
      float var33 = handle(var10);
      float var34 = handle(var11);
      float var35 = handle(var12);
      float var36 = handle(var13);
      this.itemProject.putFloat(var33);
      this.itemProject.putFloat(var34);
      this.itemProject.putFloat(var35);
      this.itemProject.putFloat(var36);
      this.itemProject.putFloat(var16);
      this.itemProject.putFloat(var17);
      this.itemProject.putFloat(var18);
      this.itemProject.putFloat(var19);
      int var37 = var1;
      if (var1 == 1 || var1 == 2) {
         int var38 = Math.max(0, Math.min(255, Math.round(var14)));
         var37 |= var38 << 2;
      }

      if (var1 == 2) {
         float var44 = var21;
         var44 %= 360.0F;
         if (var44 < 0.0F) {
            var44 += 360.0F;
         }

         int var39 = Math.max(0, Math.min(255, Math.round(var44 / 360.0F * 255.0F)));
         float var40 = Math.max(0.0F, Math.min(1.0F, var22));
         int var41 = Math.max(0, Math.min(255, Math.round(var40 * 255.0F)));
         var37 |= var39 << 10;
         var37 |= var41 << 18;
      }

      if (var1 == 3 && var14 > 0.0F) {
         var37 |= 4;
      }

      var37 |= var23;
      this.itemProject.putInt(var37);
      this.itemProject.putInt(var20);
      this.itemProject.putInt(0);
      this.itemProject.putInt(0);
      this.responseCompute++;
   }

   private static void handle(ByteBuffer var0, float[] var1, float var2, float var3, float var4, float var5) {
      float[] var6 = var1 != null && var1.length >= 6 ? var1 : animator;
      float var7 = var2;
      float var8 = var3;
      float var9 = var2 + var4;
      float var10 = var3 + var5;
      handle(var0, var6, var7, var8);
      handle(var0, var6, var9, var8);
      handle(var0, var6, var9, var10);
      handle(var0, var6, var7, var10);
   }

   private static void handle(ByteBuffer var0, float[] var1, float var2, float var3) {
      float var4 = var1[0] * var2 + var1[1] * var3 + var1[2];
      float var5 = var1[3] * var2 + var1[4] * var3 + var1[5];
      var0.putFloat(var4);
      var0.putFloat(var5);
   }

   private static void handle(float[] var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      var0[0] = var1;
      var0[1] = var2;
      var0[2] = 0.0F;
      var0[3] = 1.0F;
      var0[4] = var3;
      var0[5] = var4;
      var0[6] = 1.0F;
      var0[7] = 1.0F;
      var0[8] = var5;
      var0[9] = var6;
      var0[10] = 1.0F;
      var0[11] = 0.0F;
      var0[12] = var1;
      var0[13] = var2;
      var0[14] = 0.0F;
      var0[15] = 1.0F;
      var0[16] = var5;
      var0[17] = var6;
      var0[18] = 1.0F;
      var0[19] = 0.0F;
      var0[20] = var7;
      var0[21] = var8;
      var0[22] = 0.0F;
      var0[23] = 0.0F;
   }

   private static float handle(float[] var0, float var1, float var2) {
      return var0[0] * var1 + var0[1] * var2 + var0[2];
   }

   private static float process(float[] var0, float var1, float var2) {
      return var0[3] * var1 + var0[4] * var2 + var0[5];
   }

   private static float handle(float var0) {
      if (!Float.isFinite(var0)) {
         return 0.0F;
      } else {
         return var0 <= 0.0F ? 0.0F : var0;
      }
   }

   private static float process(float var0) {
      return Math.max(0.0F, Math.min(1.0F, var0));
   }

   private void handle(
      int var1,
      float var2,
      float var3,
      float var4,
      float var5,
      int var6,
      float var7,
      float var8,
      float[] var9,
      float var10,
      float var11,
      float var12,
      float var13,
      int var14,
      float var15,
      float var16
   ) {
      this.handle(var1, var2, var3, var4, var5, var6, var6, var6, var6, var7, var7, var7, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16, 0);
   }

   public void handle(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, int var9, float[] var10) {
      this.apply();
      this.handle(0, var1, var2, var3, var4, var9, var9, var9, var9, var5, var6, var7, var8, 0.0F, var10, 0.0F, 0.0F, 1.0F, 1.0F, -1, 0.0F, 1.0F, 0);
   }

   public void handle(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, int var9, float var10, float[] var11) {
      this.apply();
      this.handle(1, var1, var2, var3, var4, var9, var9, var9, var9, var5, var6, var7, var8, var10, var11, 0.0F, 0.0F, 1.0F, 1.0F, -1, 0.0F, 1.0F, 0);
   }

   public void handle(
      float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, int var9, int var10, int var11, int var12, float[] var13
   ) {
      this.apply();
      this.handle(0, var1, var2, var3, var4, var9, var10, var11, var12, var5, var6, var7, var8, 0.0F, var13, 0.0F, 0.0F, 1.0F, 1.0F, -1, 0.0F, 1.0F, 0);
   }

   public void handle(
      float var1,
      float var2,
      float var3,
      float var4,
      float var5,
      int var6,
      int var7,
      int var8,
      int var9,
      float var10,
      float var11,
      float var12,
      float var13,
      boolean var14,
      int var15,
      float[] var16
   ) {
      this.handle(var1, var2, var3, var4, var5, var5, var5, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16);
   }

   public void handle(
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
      int var11,
      int var12,
      float var13,
      float var14,
      float var15,
      float var16,
      boolean var17,
      int var18,
      float[] var19
   ) {
      this.apply();
      int var20 = Math.max(0, Math.min(7, var18));
      int var21 = 134217728 | (var17 ? 268435456 : 0) | var20 << 29;
      this.handle(0, var1, var2, var3, var4, var9, var10, var12, var11, var5, var6, var7, var8, 0.0F, var19, var13, var14, var15, var16, -1, 0.0F, 1.0F, var21);
   }

   public void handle(float var1, float var2, float var3, float var4, float var5, int var6, float[] var7) {
      this.handle(var1, var2, var3, var4, var5, 0.0F, var6, var7);
   }

   public void handle(float var1, float var2, float var3, float var4, float var5, float var6, int var7, float[] var8) {
      float var9 = var3 * 2.0F;
      this.apply();
      this.handle(2, var1 - var3, var2 - var3, var9, var9, var7, 0.0F, var6, var8, 0.0F, 0.0F, 1.0F, 1.0F, -1, var4, var5);
   }

   public void handle(
      float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, int var11, float[] var12
   ) {
      if (!(var3 <= 0.0F) && !(var4 <= 0.0F)) {
         float var13 = var9 > 0.0F ? var9 : 0.0F;
         float var14 = var10 > 0.0F ? var10 : 0.0F;
         float var15 = var14 + var13 * 3.0F;
         float var16 = var1 - var15;
         float var17 = var2 - var15;
         float var18 = var3 + var15 * 2.0F;
         float var19 = var4 + var15 * 2.0F;
         if (!(var18 <= 0.0F) && !(var19 <= 0.0F)) {
            this.apply();
            this.handle(
               0,
               var16,
               var17,
               var18,
               var19,
               var11,
               var11,
               var11,
               var11,
               var5,
               var6,
               var7,
               var8,
               0.0F,
               var12,
               var3,
               var4,
               Math.max(var13, 0.001F),
               var14,
               0,
               0.0F,
               1.0F,
               67108864
            );
         }
      }
   }

   public void handle(int var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, int var10, float[] var11) {
      this.apply();
      int var12 = this.resolve(var1);
      this.handle(3, var2, var3, var4, var5, var10, 0.0F, 0.0F, var11, var6, var7, var8, var9, var12, 0.0F, 1.0F);
   }

   public void handle(
      int var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, int var11, float[] var12
   ) {
      this.apply();
      int var13 = this.resolve(var1);
      this.handle(3, var2, var3, var4, var5, var11, var10, 0.0F, var12, var6, var7, var8, var9, var13, 0.0F, 1.0F);
   }

   public void process(int var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, int var10, float[] var11) {
      this.handle(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, false);
   }

   public void handle(
      int var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, int var10, float[] var11, boolean var12
   ) {
      this.apply();
      int var13 = this.resolve(var1);
      int var14 = var12 ? 64 : 0;
      this.handle(3, var2, var3, var4, var5, var10, var10, var10, var10, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, var11, var6, var7, var8, var9, var13, 0.0F, 1.0F, var14);
   }

   public void process(
      int var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, int var11, float[] var12
   ) {
      this.handle(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, false);
   }

   public void handle(
      int var1,
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
      float[] var12,
      boolean var13
   ) {
      this.apply();
      int var14 = this.resolve(var1);
      int var15 = var13 ? 64 : 0;
      this.handle(
         3, var2, var3, var4, var5, var11, var11, var11, var11, var10, var10, var10, var10, 1.0F, var12, var6, var7, var8, var9, var14, 0.0F, 1.0F, var15
      );
   }

   public void compute(
      int var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, int var11, float[] var12
   ) {
      this.apply();
      this.process(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, false);
   }

   public void process(
      int var1,
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
      float[] var12,
      boolean var13
   ) {
      this.handle(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var10, var10, var10, var11, var12, var13);
   }

   public void handle(
      int var1,
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
      int var14,
      float[] var15,
      boolean var16
   ) {
      this.apply();
      int var17 = this.resolve(var1);
      byte var18 = 8;
      if (var16) {
         var18 |= 32;
      }

      this.handle(
         3, var2, var3, var4, var5, var14, var14, var14, var14, var10, var11, var12, var13, 1.0F, var15, var6, var7, var8, var9, var17, 0.0F, 1.0F, var18
      );
   }

   public void compute(int var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, int var10, float[] var11) {
      this.apply();
      int var12 = this.resolve(var1);
      byte var13 = 8;
      this.handle(3, var2, var3, var4, var5, var10, var10, var10, var10, 0.0F, 0.0F, 0.0F, 0.0F, 1.0F, var11, var6, var7, var8, var9, var12, 0.0F, 1.0F, var13);
   }

   public void resolve(
      int var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, int var11, float[] var12
   ) {
      if (var1 > 0) {
         this.apply();
         int var13 = this.resolve(var1);
         float var14 = var2 > 0.0F ? var2 : 0.001F;
         this.handle(
            3, var3, var4, var5, var6, var11, var11, var11, var11, var14, var14, var14, var14, 0.0F, var12, var7, var8, var9, var10, var13, 0.0F, 1.0F, 16
         );
      }
   }

   private int resolve(int var1) {
      int var2 = this.rendererScan.get(var1);
      if (var2 >= 0) {
         return var2;
      }

      if (this.profileInvoke >= 16) {
         this.process();
         this.execute();
      }

      int var3 = this.profileInvoke++;
      this.sourceBuild[var3] = var1;
      this.rendererScan.put(var1, var3);
      return var3;
   }

   public void handle(ByteBuffer var1, int var2) {
   }

   public int handle(int var1, int var2, ByteBuffer var3) {
      if (var1 <= 0 || var2 <= 0) {
         throw new IllegalArgumentException("Invalid MSDF texture dimensions: " + var1 + "x" + var2);
      }

      if (var3 == null) {
         throw new IllegalArgumentException("data");
      }

      int var4 = GL11.glGetInteger(34016);
      int var5 = GL11.glGetInteger(32873);
      int var6 = GL11.glGetInteger(3317);
      int var7 = GL11.glGetInteger(3314);
      int var8 = GL11.glGenTextures();

      try {
         GL13.glActiveTexture(33984);
         GL11.glBindTexture(3553, var8);
         GL11.glTexParameteri(3553, 10241, 9729);
         GL11.glTexParameteri(3553, 10240, 9729);
         GL12.glTexParameteri(3553, 33084, 0);
         GL12.glTexParameteri(3553, 33085, 0);
         GL11.glTexParameteri(3553, 10242, 33071);
         GL11.glTexParameteri(3553, 10243, 33071);
         GL11.glPixelStorei(3317, 1);
         GL12.glPixelStorei(3314, 0);
         var3.rewind();
         GL11.glTexImage2D(3553, 0, 32856, var1, var2, 0, 6408, 5121, var3);
         return var8;
      } finally {
         GL12.glPixelStorei(3314, var7);
         GL11.glPixelStorei(3317, var6);
         GL11.glBindTexture(3553, var5);
         GL13.glActiveTexture(var4);
      }
   }
   public int resolve(int var1, int var2) {
      int var3 = GL11.glGetInteger(34016);
      int var4 = GL11.glGetInteger(32873);
      int var5 = GL11.glGetInteger(3317);
      int var6 = GL11.glGetInteger(3314);
      int var7 = GL11.glGenTextures();
      boolean var11 = false /* VF: Semaphore variable */;

      int var8;
      try {
         var11 = true;
         GL13.glActiveTexture(33984);
         GL11.glBindTexture(3553, var7);
         GL11.glTexParameteri(3553, 10241, 9729);
         GL11.glTexParameteri(3553, 10240, 9729);
         GL12.glTexParameteri(3553, 33084, 0);
         GL12.glTexParameteri(3553, 33085, 0);
         GL11.glTexParameteri(3553, 10242, 33071);
         GL11.glTexParameteri(3553, 10243, 33071);
         GL11.glTexParameteri(3553, 36418, 6403);
         GL11.glTexParameteri(3553, 36419, 6403);
         GL11.glTexParameteri(3553, 36420, 6403);
         GL11.glTexParameteri(3553, 36421, 6403);
         GL11.glPixelStorei(3317, 1);
         GL12.glPixelStorei(3314, 0);
         VertexLayoutBinding.handle(33321, var1, var2, 6403, 5121);
         var8 = var7;
         var11 = false;
      } finally {
         if (var11) {
            GL12.glPixelStorei(3314, var6);
            GL11.glPixelStorei(3317, var5);
            GL11.glBindTexture(3553, var4);
            GL13.glActiveTexture(var3);
         }
      }

      GL12.glPixelStorei(3314, var6);
      GL11.glPixelStorei(3317, var5);
      GL11.glBindTexture(3553, var4);
      GL13.glActiveTexture(var3);
      return var8;
   }
   public void handle(int var1, int var2, int var3, int var4, int var5, ByteBuffer var6) {
      int var7 = GL11.glGetInteger(34016);
      int var8 = GL11.glGetInteger(32873);
      int var9 = GL11.glGetInteger(3317);
      int var10 = GL11.glGetInteger(3314);
      boolean var13 = false /* VF: Semaphore variable */;

      try {
         var13 = true;
         var6.order(ByteOrder.nativeOrder());
         GL13.glActiveTexture(33984);
         GL11.glBindTexture(3553, var1);
         GL11.glPixelStorei(3317, 1);
         GL12.glPixelStorei(3314, 0);
         GL11.glTexSubImage2D(3553, 0, var2, var3, var4, var5, 6403, 5121, var6);
         var13 = false;
      } finally {
         if (var13) {
            GL12.glPixelStorei(3314, var10);
            GL11.glPixelStorei(3317, var9);
            GL11.glBindTexture(3553, var8);
            GL13.glActiveTexture(var7);
         }
      }

      GL12.glPixelStorei(3314, var10);
      GL11.glPixelStorei(3317, var9);
      GL11.glBindTexture(3553, var8);
      GL13.glActiveTexture(var7);
   }
   public void handle(int var1, int var2, int var3, int var4, int var5, ByteBuffer var6, int var7) {
      int var8 = GL11.glGetInteger(34016);
      int var9 = GL11.glGetInteger(32873);
      int var10 = GL11.glGetInteger(3317);
      int var11 = GL11.glGetInteger(3314);
      boolean var14 = false /* VF: Semaphore variable */;

      try {
         var14 = true;
         var6.order(ByteOrder.nativeOrder());
         GL13.glActiveTexture(33984);
         GL11.glBindTexture(3553, var1);
         GL11.glPixelStorei(3317, 1);
         GL12.glPixelStorei(3314, var7);
         GL11.glTexSubImage2D(3553, 0, var2, var3, var4, var5, 6403, 5121, var6);
         var14 = false;
      } finally {
         if (var14) {
            GL12.glPixelStorei(3314, var11);
            GL11.glPixelStorei(3317, var10);
            GL11.glBindTexture(3553, var9);
            GL13.glActiveTexture(var8);
         }
      }

      GL12.glPixelStorei(3314, var11);
      GL11.glPixelStorei(3317, var10);
      GL11.glBindTexture(3553, var9);
      GL13.glActiveTexture(var8);
   }
   private void handle(int var1, int var2, boolean var3) {
      if (var1 > 0 && var2 > 0) {
         int var4 = var3 ? this.scaleSave : this.keyFilter;
         int var5 = var3 ? this.textureRun : this.vectorEncode;
         int var6 = var3 ? this.colorCompute : this.requestAdapt;
         int var7 = var3 ? this.scaleAdapt : this.timerMeasure;
         if (var4 == 0 || var5 == 0 || var1 != var6 || var2 != var7 || !GL11.glIsTexture(var4) || !GL30.glIsFramebuffer(var5)) {
            int var8 = 0;
            int var9 = 0;
            OpenGlStateSnapshot.NetworkState var10 = OpenGlStateSnapshot.handle();
            boolean var15 = false /* VF: Semaphore variable */;

            try {
               var15 = true;
               var8 = GL11.glGenTextures();
               GL11.glBindTexture(3553, var8);
               GL11.glTexParameteri(3553, 10241, 9729);
               GL11.glTexParameteri(3553, 10240, 9729);
               GL11.glTexParameteri(3553, 10242, 33071);
               GL11.glTexParameteri(3553, 10243, 33071);
               VertexLayoutBinding.handle(32856, var1, var2, 6408, 5121);
               var9 = GL30.glGenFramebuffers();
               GL30.glBindFramebuffer(36160, var9);
               GL30.glFramebufferTexture2D(36160, 36064, 3553, var8, 0);
               GL11.glDrawBuffer(36064);
               int var11 = GL30.glCheckFramebufferStatus(36160);
               if (var11 != 36053) {
                  throw new IllegalStateException("Capture FBO incomplete: status=" + var11);
               }

               var15 = false;
            } catch (RuntimeException | Error var16) {
               if (var9 != 0) {
                  GL30.glDeleteFramebuffers(var9);
               }

               if (var8 != 0) {
                  GL11.glDeleteTextures(var8);
               }

               throw var16;
            } finally {
               if (var15) {
                  OpenGlStateSnapshot.compute(var10);
               }
            }

            OpenGlStateSnapshot.compute(var10);
            if (var5 != 0) {
               GL30.glDeleteFramebuffers(var5);
            }

            if (var4 != 0) {
               GL11.glDeleteTextures(var4);
            }

            if (var3) {
               this.scaleSave = var8;
               this.textureRun = var9;
               this.colorCompute = var1;
               this.scaleAdapt = var2;
            } else {
               this.keyFilter = var8;
               this.vectorEncode = var9;
               this.requestAdapt = var1;
               this.timerMeasure = var2;
            }
         }
      } else {
         if (var3) {
            this.compute(true);
         } else {
            this.compute(false);
         }
      }
   }

   private void process(ShaderRenderer.ColorState var1, int var2, int var3, boolean var4) {
      if (var1 != null) {
         if (var2 > 0 && var3 > 0) {
            boolean var5 = var1.data != 0 && var1.instance != 0;
            boolean var6 = var1.context >= var2 && var1.config >= var3;
            boolean var7 = var1.context == var2 && var1.config == var3;
            if (!var5 || (var4 ? !var6 : !var7)) {
               int var8 = var4 ? ShaderColorPacking.handle(var5 ? var1.context : 0, var2) : var2;
               int var9 = var4 ? ShaderColorPacking.handle(var5 ? var1.config : 0, var3) : var3;
               int var10 = 0;
               int var11 = 0;
               OpenGlStateSnapshot.NetworkState var12 = OpenGlStateSnapshot.handle();

               try {
                  var10 = GL11.glGenTextures();
                  GL11.glBindTexture(3553, var10);
                  GL11.glTexParameteri(3553, 10241, 9729);
                  GL11.glTexParameteri(3553, 10240, 9729);
                  GL11.glTexParameteri(3553, 10242, 33071);
                  GL11.glTexParameteri(3553, 10243, 33071);
                  VertexLayoutBinding.handle(32856, var8, var9, 6408, 5121);
                  var11 = GL30.glGenFramebuffers();
                  GL30.glBindFramebuffer(36160, var11);
                  GL30.glFramebufferTexture2D(36160, 36064, 3553, var10, 0);
                  GL11.glDrawBuffer(36064);
                  GL11.glReadBuffer(36064);
                  int var13 = GL30.glCheckFramebufferStatus(36160);
                  if (var13 != 36053) {
                     throw new IllegalStateException("Layer framebuffer incomplete: status=" + var13);
                  }
               } catch (RuntimeException | Error var17) {
                  if (var11 != 0) {
                     GL30.glDeleteFramebuffers(var11);
                  }

                  if (var10 != 0) {
                     GL11.glDeleteTextures(var10);
                  }

                  throw var17;
               } finally {
                  OpenGlStateSnapshot.compute(var12);
               }

               this.handle(var1);
               var1.data = var10;
               var1.instance = var11;
               var1.context = var8;
               var1.config = var9;
            }
         } else {
            this.handle(var1);
         }
      }
   }

   private void handle(ShaderRenderer.ColorState var1) {
      if (var1 != null) {
         if (var1.instance != 0) {
            GL30.glDeleteFramebuffers(var1.instance);
            var1.instance = 0;
         }

         if (var1.data != 0) {
            GL11.glDeleteTextures(var1.data);
            var1.data = 0;
         }

         var1.context = 0;
         var1.config = 0;
      }
   }

   private void drawAnimation() {
      for (ShaderRenderer.ColorState var2 : this.effectScan) {
         this.handle(var2);
      }

      this.effectScan.clear();
      this.optionParse = 0;
   }

   private void prepare(int var1, int var2) {
      this.handle(var1, var2, this.scaleRender, this.clientRefresh);
   }

   private void handle(int var1, int var2, float var3, float var4) {
      if (var1 <= 0 || var2 <= 0) {
         this.save();
      } else {
         if (!Float.isFinite(var3) || !Float.isFinite(var4)) {
            throw new IllegalArgumentException("Blur capture scale must be finite");
         }

         if (!(var3 <= 0.0F) && !(var4 <= 0.0F)) {
            int var5 = Math.max(1, var1);
            int var6 = Math.max(1, var2);
            int var7 = Math.max(1, Math.round(var5 * var3));
            int var8 = Math.max(1, Math.round(var6 * var4));
            if (this.indexBind == 0
               || this.dataValidate == 0
               || var7 != this.actionRead
               || var8 != this.configCollapse
               || !GL11.glIsTexture(this.indexBind)
               || !GL30.glIsFramebuffer(this.dataValidate)) {
               int var9 = 0;
               int var10 = 0;
               OpenGlStateSnapshot.NetworkState var11 = OpenGlStateSnapshot.handle();

               try {
                  var9 = GL11.glGenTextures();
                  GL11.glBindTexture(3553, var9);
                  GL11.glTexParameteri(3553, 10241, 9729);
                  GL11.glTexParameteri(3553, 10240, 9729);
                  GL11.glTexParameteri(3553, 10242, 33071);
                  GL11.glTexParameteri(3553, 10243, 33071);
                  VertexLayoutBinding.handle(32856, var7, var8, 6408, 5121);
                  var10 = GL30.glGenFramebuffers();
                  GL30.glBindFramebuffer(36160, var10);
                  GL30.glFramebufferTexture2D(36160, 36064, 3553, var9, 0);
                  GL11.glDrawBuffer(36064);
                  int var12 = GL30.glCheckFramebufferStatus(36160);
                  if (var12 != 36053) {
                     throw new IllegalStateException("Downscaled capture FBO incomplete: status=" + var12);
                  }
               } catch (RuntimeException | Error var16) {
                  if (var10 != 0) {
                     GL30.glDeleteFramebuffers(var10);
                  }

                  if (var9 != 0) {
                     GL11.glDeleteTextures(var9);
                  }

                  throw var16;
               } finally {
                  OpenGlStateSnapshot.compute(var11);
               }

               if (this.dataValidate != 0) {
                  GL30.glDeleteFramebuffers(this.dataValidate);
               }

               if (this.indexBind != 0) {
                  GL11.glDeleteTextures(this.indexBind);
               }

               this.dataValidate = var10;
               this.indexBind = var9;
               this.actionRead = var7;
               this.configCollapse = var8;
            }
         } else {
            throw new IllegalArgumentException("Blur capture scale must be positive");
         }
      }
   }

   public int handle(int var1, int var2, int var3, int var4) {
      return this.handle(var1, var2, var3, var4, true);
   }

   public int handle(int var1, int var2, int var3, int var4, boolean var5) {
      if (var3 > 0 && var4 > 0 && this.profileDraw > 0 && this.vectorPerform > 0) {
         this.handle(var3, var4, var5);
         int var6 = var5 ? this.textureRun : this.vectorEncode;
         int var7 = var5 ? this.scaleSave : this.keyFilter;
         if (var6 != 0 && var7 != 0) {
            int var8 = GL11.glGetInteger(36006);
            int var9 = Math.max(0, Math.min(var1, this.profileDraw));
            int var10 = Math.max(0, Math.min(this.vectorPerform, this.vectorPerform - var2 - var4));
            int var11 = Math.min(var3, this.profileDraw - var9);
            int var12 = Math.min(var4, this.vectorPerform - var10);
            if (var11 > 0 && var12 > 0) {
               OpenGlStateSnapshot.NetworkState var13 = OpenGlStateSnapshot.handle();

               try {
                  boolean var14 = GL11.glIsEnabled(3089);
                  boolean var15 = GL11.glIsEnabled(36281);
                  if (var14) {
                     GL11.glDisable(3089);
                  }

                  if (var15) {
                     GL11.glDisable(36281);
                  }

                  GL30.glBindFramebuffer(36008, var8);
                  GL11.glReadBuffer(var8 == 0 ? 1029 : 36064);
                  GL30.glBindFramebuffer(36009, var6);
                  GL11.glDrawBuffer(36064);
                  GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
                  GL11.glClear(16384);
                  encodePoint();
                  GL30.glBlitFramebuffer(var9, var10, var9 + var11, var10 + var12, 0, 0, var3, var4, 16384, 9728);
                  if (GL11.glGetError() != 0) {
                     return 0;
                  }

                  if (var14) {
                     GL11.glEnable(3089);
                  }

                  if (var15) {
                     GL11.glEnable(36281);
                  }
               } finally {
                  OpenGlStateSnapshot.compute(var13);
               }

               return var7;
            } else {
               return 0;
            }
         } else {
            return 0;
         }
      } else {
         return 0;
      }
   }
   public boolean handle(int var1, int var2, float var3) {
      if (var1 > 0 && var2 > 0) {
         float var4 = 1.0F;
         float var5 = 1.0F;
         float var6 = this.scaleRender;
         float var7 = this.clientRefresh;
         float var8 = this.dataRelease.process();
         if (var3 > var8) {
            float var9 = this.dataRelease.handle();
            float var10 = Math.max(var3, var9);
            float var11 = var8 / var10;
            var11 = Math.max(var11, 0.2F);
            var4 = Math.min(var4, var11);
            var5 = Math.min(var5, var11);
         }

         var4 = Math.max(var4, var6);
         var5 = Math.max(var5, var7);
         this.handle(var1, var2, var4, var5);
         if (this.indexBind != 0 && this.dataValidate != 0) {
            float var26 = (float)this.actionRead / Math.max(1, var1);
            float var27 = (float)this.configCollapse / Math.max(1, var2);
            OpenGlStateSnapshot.NetworkState var29 = OpenGlStateSnapshot.handle();
            boolean var22 = false /* VF: Semaphore variable */;

            int var34;
            label145: {
               boolean var35;
               label157: {
                  try {
                     var22 = true;
                     boolean var12 = GL11.glIsEnabled(3089);
                     boolean var13 = GL11.glIsEnabled(36281);
                     if (var12) {
                        GL11.glDisable(3089);
                     }

                     if (var13) {
                        GL11.glDisable(36281);
                     }

                     boolean var14 = false;
                     MinecraftClient var15 = MinecraftClient.getInstance();
                     if (var15 != null && var15.getWindow() != null && !var15.getWindow().hasZeroWidthOrHeight()) {
                        Framebuffer var16 = var15.getFramebuffer();
                        if (var16 != null && var16.getColorAttachment() instanceof GlTexture var18) {
                           int var19 = var18.getGlId();
                           if (var19 > 0) {
                              if (this.windowProcess == 0) {
                                 this.windowProcess = GL30.glGenFramebuffers();
                              }

                              GL30.glBindFramebuffer(36008, this.windowProcess);
                              GL30.glFramebufferTexture2D(36008, 36064, 3553, var19, 0);
                              GL11.glReadBuffer(36064);
                              var14 = GL30.glCheckFramebufferStatus(36008) == 36053;
                           }
                        }
                     }

                     if (!var14) {
                        this.providerSynchronize = 0;
                        this.pathProcess = 0;
                        this.positionReset = 0;
                        this.effectApply = 1.0F;
                        this.cacheHandle2 = 1.0F;
                        var34 = 0;
                        var22 = false;
                        break label145;
                     }

                     GL30.glBindFramebuffer(36009, this.dataValidate);
                     GL11.glDrawBuffer(36064);
                     encodePoint();
                     GL30.glBlitFramebuffer(0, 0, var1, var2, 0, 0, this.actionRead, this.configCollapse, 16384, 9729);
                     var34 = GL11.glGetError();
                     if (var34 != 0) {
                        encodePoint();
                        GL30.glBlitFramebuffer(0, 0, var1, var2, 0, 0, this.actionRead, this.configCollapse, 16384, 9728);
                        var34 = GL11.glGetError();
                     }

                     if (var34 != 0) {
                        this.providerSynchronize = 0;
                        this.pathProcess = 0;
                        this.positionReset = 0;
                        this.effectApply = 1.0F;
                        this.cacheHandle2 = 1.0F;
                        var35 = false;
                        var22 = false;
                        break label157;
                     }

                     if (var12) {
                        GL11.glEnable(3089);
                     }

                     if (var13) {
                        GL11.glEnable(36281);
                        var22 = false;
                     } else {
                        var22 = false;
                     }
                  } finally {
                     if (var22) {
                        OpenGlStateSnapshot.compute(var29);
                     }
                  }

                  OpenGlStateSnapshot.compute(var29);
                  float var30 = (float)Math.sqrt(Math.max(0.0F, var26) * Math.max(0.0F, var27));
                  float var31 = Math.max(0.0F, var3) * var30;
                  int var32 = this.dataRelease.handle(this.indexBind, this.actionRead, this.configCollapse, var31);
                  if (var32 == 0) {
                     this.providerSynchronize = 0;
                     this.pathProcess = 0;
                     this.positionReset = 0;
                     this.effectApply = 1.0F;
                     this.cacheHandle2 = 1.0F;
                     return false;
                  }

                  this.providerSynchronize = var32;
                  this.pathProcess = this.actionRead;
                  this.positionReset = this.configCollapse;
                  this.effectApply = var26;
                  this.cacheHandle2 = var27;
                  return true;
               }

               OpenGlStateSnapshot.compute(var29);
               return var35;
            }

            OpenGlStateSnapshot.compute(var29);
            return var34 != 0;
         } else {
            this.providerSynchronize = 0;
            this.pathProcess = 0;
            this.positionReset = 0;
            this.effectApply = 1.0F;
            this.cacheHandle2 = 1.0F;
            return false;
         }
      } else {
         this.providerSynchronize = 0;
         this.pathProcess = 0;
         this.positionReset = 0;
         this.effectApply = 1.0F;
         this.cacheHandle2 = 1.0F;
         return false;
      }
   }

   private static void encodePoint() {
      while (GL11.glGetError() != 0) {
      }
   }

   public boolean handle(int var1, int var2, int var3, int var4, float var5) {
      if (var3 > 0 && var4 > 0) {
         int var6 = this.handle(var1, var2, var3, var4, false);
         if (var6 <= 0) {
            this.sessionAdvance = 0;
            this.taskLoad = 0;
            this.pointSend = 0;
            this.keySample = 0;
            this.playerPerform = 0;
            return false;
         } else {
            int var7 = this.vectorRun.handle(var6, var3, var4, var5);
            this.sessionAdvance = var7;
            this.taskLoad = var3;
            this.pointSend = var4;
            this.keySample = var1;
            this.playerPerform = var2;
            return var7 != 0;
         }
      } else {
         this.sessionAdvance = 0;
         this.taskLoad = 0;
         this.pointSend = 0;
         this.keySample = 0;
         this.playerPerform = 0;
         return false;
      }
   }

   public void handle(float var1, float var2, float var3, float var4, float var5, float var6, float[] var7) {
      this.handle(var1, var2, var3, var4, var5, var5, var5, var5, var6, var7);
   }

   public void handle(float var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, float[] var10) {
      if (this.providerSynchronize != 0) {
         this.apply();
         int var11 = (int)(Math.max(0.0F, Math.min(1.0F, var9)) * 255.0F) << 24 | 16777215;
         float var12 = this.pathProcess > 0 ? this.effectApply / this.pathProcess : 0.0F;
         float var13 = this.positionReset > 0 ? -this.cacheHandle2 / this.positionReset : 0.0F;
         float var14 = 0.0F;
         float var15 = this.positionReset > 0 ? 1.0F : 0.0F;
         this.handle(this.providerSynchronize, var1, var2, var3, var4, var12, var13, var14, var15, var5, var6, var7, var8, var11, var10, true);
      }
   }

   public boolean handle(
      float var1,
      float var2,
      float var3,
      float var4,
      float var5,
      float var6,
      float var7,
      float var8,
      int var9,
      float var10,
      int var11,
      float var12,
      float[] var13
   ) {
      if (this.providerSynchronize == 0) {
         return false;
      }

      this.apply();
      int var14 = this.resolve(this.providerSynchronize);
      int var15 = Math.round(process(var10) * 255.0F) << 24 | var9 & 16777215;
      int var16 = Math.round(process(var12) * 255.0F) << 24 | var9 & 16777215;
      float var17 = this.pathProcess > 0 ? this.effectApply / this.pathProcess : 0.0F;
      float var18 = this.positionReset > 0 ? -this.cacheHandle2 / this.positionReset : 0.0F;
      float var19 = this.positionReset > 0 ? 1.0F : 0.0F;
      short var20 = 168;
      this.handle(
         3, var1, var2, var3, var4, var9, var11, var16, var15, var5, var6, var7, var8, 1.0F, var13, var17, var18, 0.0F, var19, var14, 0.0F, 1.0F, var20
      );
      return true;
   }

   public void handle(float var1, float var2, float var3, float var4, float var5, float var6, float[] var7, int var8, int var9, int var10, int var11) {
      if (this.sessionAdvance != 0) {
         if (var10 > 0 && var11 > 0) {
            if (this.taskLoad == var10 && this.pointSend == var11 && this.keySample == var8 && this.playerPerform == var9) {
               this.apply();
               int var12 = (int)(Math.max(0.0F, Math.min(1.0F, var6)) * 255.0F) << 24 | 16777215;
               float var13 = 0.0F;
               float var14 = 1.0F;
               float var15 = 1.0F;
               float var16 = 0.0F;
               this.process(this.sessionAdvance, var1, var2, var3, var4, var13, var14, var15, var16, var5, var12, var7, false);
            }
         }
      }
   }
   public ShaderRenderer.Bounds resolve() {
      MinecraftClient var1 = MinecraftClient.getInstance();
      if (var1 != null && var1.getWindow() != null && !var1.getWindow().hasZeroWidthOrHeight()) {
         Framebuffer var2 = var1.getFramebuffer();
         if (var2 == null) {
            return new ShaderRenderer.Bounds(0, 0, 0, 0);
         }

         if (var2.getColorAttachment() instanceof GlTexture var4) {
            int var5 = var4.getGlId();
            if (var5 <= 0) {
               return new ShaderRenderer.Bounds(0, 0, 0, 0);
            }

            int var6 = var1.getWindow().getFramebufferWidth();
            int var7 = var1.getWindow().getFramebufferHeight();
            if (var6 > 0 && var7 > 0 && var2.textureWidth > 0 && var2.textureHeight > 0) {
               this.requestReceive.handle(var6, var7);
               if (this.requestReceive.instance != 0 && this.requestReceive.data != 0 && this.requestReceive.context != 0) {
                  this.prepare();
                  ShaderBuildReporter var8 = this.check();
                  OpenGlStateSnapshot.NetworkState var9 = OpenGlStateSnapshot.handle();
                  boolean var13 = false /* VF: Semaphore variable */;

                  ShaderRenderer.Bounds var15;
                  label99: {
                     label98: {
                        try {
                           var13 = true;
                           GL30.glBindFramebuffer(36160, this.requestReceive.instance);
                           GL11.glDrawBuffer(36064);
                           if (GL30.glCheckFramebufferStatus(36160) != 36053) {
                              var15 = new ShaderRenderer.Bounds(0, 0, 0, 0);
                              var13 = false;
                              break label99;
                           }

                           GL11.glViewport(0, 0, var6, var7);
                           GL11.glDisable(3089);
                           GL11.glDisable(2884);
                           GL11.glDisable(3042);
                           GL11.glDisable(2929);
                           GL11.glDisable(36281);
                           GL11.glColorMask(true, true, true, true);
                           GL11.glDepthMask(false);
                           encodePoint();
                           var8.handle();
                           if (this.stateApply >= 0) {
                              GL20.glUniform1i(this.stateApply, 0);
                           }

                           GL13.glActiveTexture(33984);
                           GL11.glBindTexture(3553, var5);
                           GL30.glBindVertexArray(this.packetSave);
                           ShaderViewportTracker.handle().handle(2);
                           GL11.glDrawArrays(4, 0, 6);
                           GL30.glBindVertexArray(0);
                           if (GL11.glGetError() != 0) {
                              var15 = new ShaderRenderer.Bounds(0, 0, 0, 0);
                              var13 = false;
                              break label98;
                           }

                           var13 = false;
                        } finally {
                           if (var13) {
                              GL13.glActiveTexture(33984);
                              GL11.glBindTexture(3553, 0);
                              GL20.glUseProgram(0);
                              OpenGlStateSnapshot.compute(var9);
                           }
                        }

                        GL13.glActiveTexture(33984);
                        GL11.glBindTexture(3553, 0);
                        GL20.glUseProgram(0);
                        OpenGlStateSnapshot.compute(var9);
                        return new ShaderRenderer.Bounds(this.requestReceive.data, 0, var6, var7);
                     }

                     GL13.glActiveTexture(33984);
                     GL11.glBindTexture(3553, 0);
                     GL20.glUseProgram(0);
                     OpenGlStateSnapshot.compute(var9);
                     return var15;
                  }

                  GL13.glActiveTexture(33984);
                  GL11.glBindTexture(3553, 0);
                  GL20.glUseProgram(0);
                  OpenGlStateSnapshot.compute(var9);
                  return var15;
               } else {
                  return new ShaderRenderer.Bounds(0, 0, 0, 0);
               }
            } else {
               return new ShaderRenderer.Bounds(0, 0, 0, 0);
            }
         } else {
            return new ShaderRenderer.Bounds(0, 0, 0, 0);
         }
      } else {
         return new ShaderRenderer.Bounds(0, 0, 0, 0);
      }
   }
   public void handle(int var1, int var2, int var3) {
      if (var1 > 0 && var2 > 0 && var3 > 0) {
         this.prepare();
         ShaderBuildReporter var4 = this.check();
         OpenGlStateSnapshot.NetworkState var5 = OpenGlStateSnapshot.handle();
         boolean var8 = false /* VF: Semaphore variable */;

         try {
            var8 = true;
            GL30.glBindFramebuffer(36160, 0);
            GL11.glViewport(0, 0, var2, var3);
            GL11.glDisable(3089);
            GL11.glDisable(2884);
            GL11.glDisable(2929);
            GL11.glDisable(3042);
            GL11.glDisable(36281);
            var4.handle();
            if (this.stateApply >= 0) {
               GL20.glUniform1i(this.stateApply, 0);
            }

            GL13.glActiveTexture(33984);
            GL11.glBindTexture(3553, var1);
            GL30.glBindVertexArray(this.packetSave);
            ShaderViewportTracker.handle().handle(2);
            GL11.glDrawArrays(4, 0, 6);
            GL30.glBindVertexArray(0);
            var8 = false;
         } finally {
            if (var8) {
               GL13.glActiveTexture(33984);
               GL11.glBindTexture(3553, 0);
               GL20.glUseProgram(0);
               OpenGlStateSnapshot.compute(var5);
            }
         }

         GL13.glActiveTexture(33984);
         GL11.glBindTexture(3553, 0);
         GL20.glUseProgram(0);
         OpenGlStateSnapshot.compute(var5);
      }
   }

   public void handle(int var1, int var2, int var3, ShaderBuildReporter var4, Runnable var5, boolean var6) {
      if (var1 > 0 && var2 > 0 && var3 > 0 && var4 != null) {
         this.prepare();
         OpenGlStateSnapshot.NetworkState var7 = OpenGlStateSnapshot.handle();

         try {
            int var8 = GL11.glGetInteger(36006);
            GL30.glBindFramebuffer(36009, var8);
            GL11.glViewport(0, 0, var2, var3);
            GL11.glDisable(3089);
            GL11.glDisable(2884);
            GL11.glDisable(2929);
            if (var6) {
               GL11.glEnable(3042);
               GL14.glBlendFuncSeparate(770, 771, 1, 771);
            } else {
               GL11.glDisable(3042);
            }

            GL11.glDisable(36281);
            var4.handle();
            if (var5 != null) {
               var5.run();
            }

            GL13.glActiveTexture(33984);
            GL11.glBindTexture(3553, var1);
            GL30.glBindVertexArray(this.packetSave);
            ShaderViewportTracker.handle().handle(2);
            GL11.glDrawArrays(4, 0, 6);
            GL30.glBindVertexArray(0);
         } finally {
            GL13.glActiveTexture(33984);
            GL11.glBindTexture(3553, 0);
            GL20.glUseProgram(0);
            OpenGlStateSnapshot.compute(var7);
         }
      }
   }

   public void update(int var1, int var2) {
      if (!this.pointSample) {
         if (var1 > 0 && var2 > 0) {
            if (var1 != this.eventAttach || var2 != this.serverRead) {
               this.eventAttach = var1;
               this.serverRead = var2;
               this.animate();
            }
         } else {
            this.profileDraw = 0;
            this.vectorPerform = 0;
            this.eventAttach = -1;
            this.serverRead = -1;
            this.animate();
         }
      }
   }

   private void animate() {
      this.load();
      this.compute(true);
      this.compute(false);
      this.save();
      this.handle(this.matrixFilter);
      this.drawAnimation();
      this.requestReceive.handle();
      this.dataRelease.resolve();
      this.vectorRun.resolve();
      if (this.windowProcess != 0) {
         GL30.glDeleteFramebuffers(this.windowProcess);
         this.windowProcess = 0;
      }
   }

   private void load() {
      this.providerSynchronize = 0;
      this.pathProcess = 0;
      this.positionReset = 0;
      this.effectApply = 1.0F;
      this.cacheHandle2 = 1.0F;
      this.sessionAdvance = 0;
      this.keySample = 0;
      this.playerPerform = 0;
      this.taskLoad = 0;
      this.pointSend = 0;
   }

   private void compute(boolean var1) {
      if (var1) {
         if (this.textureRun != 0) {
            GL30.glDeleteFramebuffers(this.textureRun);
            this.textureRun = 0;
         }

         if (this.scaleSave != 0) {
            GL11.glDeleteTextures(this.scaleSave);
            this.scaleSave = 0;
         }

         this.colorCompute = 0;
         this.scaleAdapt = 0;
      } else {
         if (this.vectorEncode != 0) {
            GL30.glDeleteFramebuffers(this.vectorEncode);
            this.vectorEncode = 0;
         }

         if (this.keyFilter != 0) {
            GL11.glDeleteTextures(this.keyFilter);
            this.keyFilter = 0;
         }

         this.requestAdapt = 0;
         this.timerMeasure = 0;
      }
   }

   private void save() {
      if (this.dataValidate != 0) {
         GL30.glDeleteFramebuffers(this.dataValidate);
         this.dataValidate = 0;
      }

      if (this.indexBind != 0) {
         GL11.glDeleteTextures(this.indexBind);
         this.indexBind = 0;
      }

      this.actionRead = 0;
      this.configCollapse = 0;
   }

   public void update() {
      if (!this.pointSample) {
         this.pointSample = true;
         this.dataRelease.compute();
         this.vectorRun.compute();
         this.requestReceive.handle();
         if (this.windowProcess != 0) {
            GL30.glDeleteFramebuffers(this.windowProcess);
            this.windowProcess = 0;
         }

         if (this.packetSave != 0) {
            GL30.glDeleteVertexArrays(this.packetSave);
            this.packetSave = 0;
         }

         if (this.entryAnimate != 0) {
            GL15.glDeleteBuffers(this.entryAnimate);
            this.entryAnimate = 0;
         }

         if (this.layerSample != 0) {
            GL30.glDeleteVertexArrays(this.layerSample);
            this.layerSample = 0;
         }

         if (this.worldSend != 0) {
            GL15.glDeleteBuffers(this.worldSend);
            this.worldSend = 0;
         }

         if (this.pointSubmit != 0) {
            GL30.glDeleteVertexArrays(this.pointSubmit);
            this.pointSubmit = 0;
         }

         if (this.listenerPerform != 0) {
            GL15.glDeleteBuffers(this.listenerPerform);
            this.listenerPerform = 0;
         }

         this.handle(this.matrixFilter);
         this.drawAnimation();
         if (this.textureRun != 0) {
            GL30.glDeleteFramebuffers(this.textureRun);
            this.textureRun = 0;
         }

         if (this.scaleSave != 0) {
            GL11.glDeleteTextures(this.scaleSave);
            this.scaleSave = 0;
         }

         this.colorCompute = 0;
         this.scaleAdapt = 0;
         if (this.dataValidate != 0) {
            GL30.glDeleteFramebuffers(this.dataValidate);
            this.dataValidate = 0;
         }

         if (this.indexBind != 0) {
            GL11.glDeleteTextures(this.indexBind);
            this.indexBind = 0;
         }

         this.actionRead = 0;
         this.configCollapse = 0;
         if (this.vectorEncode != 0) {
            GL30.glDeleteFramebuffers(this.vectorEncode);
            this.vectorEncode = 0;
         }

         if (this.keyFilter != 0) {
            GL11.glDeleteTextures(this.keyFilter);
            this.keyFilter = 0;
         }

         this.requestAdapt = 0;
         this.timerMeasure = 0;
         this.providerSynchronize = 0;
         this.pathProcess = 0;
         this.positionReset = 0;
         this.effectApply = 1.0F;
         this.cacheHandle2 = 1.0F;
         this.sessionAdvance = 0;
         this.taskLoad = 0;
         this.pointSend = 0;
         this.keySample = 0;
         this.playerPerform = 0;
         this.execute();
         GL30.glBindVertexArray(0);
         GL20.glUseProgram(0);
         if (this.summary != 0) {
            GL30.glDeleteVertexArrays(this.summary);
         }

         if (this.matrixBlend != 0) {
            GL15.glDeleteBuffers(this.matrixBlend);
         }

         if (this.vectorMatch != 0) {
            GL15.glDeleteBuffers(this.vectorMatch);
         }

         this.latest.process();
         if (this.playerCollect != null) {
            this.playerCollect.process();
            this.playerCollect = null;
         }

         if (this.targetWrite != null) {
            this.targetWrite.process();
            this.targetWrite = null;
         }

         if (this.configMatch != null) {
            this.configMatch.process();
            this.configMatch = null;
         }

         if (this.indexLoad != null) {
            this.indexLoad.process();
            this.indexLoad = null;
         }

         if (this.shaderProject != null) {
            this.shaderProject.process();
            this.shaderProject = null;
         }

         if (this.frameProject != null) {
            this.frameProject.free();
            this.frameProject = null;
         }
      }
   }

   private void handle(GLCapabilities var1) {
      if (this.frameProject == null) {
         this.frameProject = GLDebugMessageCallback.create((var0, var1x, var2, var3, var4, var5, var7) -> {
            if (var3 != 33387 && var3 != 37192) {
               long var9 = System.currentTimeMillis();
               Long var11 = regionRefresh.get(var2);
               if (var11 == null || var9 - var11 >= 5000L) {
                  regionRefresh.put(var2, var9);
                  long var12 = pathCheck.get();
                  if (var9 - var12 > 1000L) {
                     pathCheck.set(var9);
                     contextParse.set(0);
                  }

                  if (contextParse.incrementAndGet() <= 8) {
                     String var14 = GLDebugMessageCallback.getMessage(var4, var5);
                     System.err.println("[OpenGL] " + var14 + " (severity=" + update(var3) + ")");
                  }
               }
            }
         });
         if (var1.OpenGL43) {
            GL11.glEnable(37600);
            GL43.glDebugMessageCallback(this.frameProject, 0L);
            GL43.glDebugMessageControl(4352, 4352, 33387, (int[])null, false);
            GL43.glDebugMessageControl(4352, 4352, 37192, (int[])null, false);
         } else {
            GL11.glEnable(37600);
            KHRDebug.glDebugMessageCallback(this.frameProject, 0L);
            KHRDebug.glDebugMessageControl(4352, 4352, 33387, (int[])null, false);
            KHRDebug.glDebugMessageControl(4352, 4352, 37192, (int[])null, false);
         }
      }
   }

   private static String update(int var0) {
      return switch (var0) {
         case 33387 -> "NOTIFICATION";
         case 37190 -> "HIGH";
         case 37191 -> "MEDIUM";
         case 37192 -> "LOW";
         default -> Integer.toString(var0);
      };
   }

   public record Bounds(int colorTexture, int depthTexture, int width, int height) {
   }

   static final class ColorState {
      int instance;
      int data;
      int context;
      int config;
      final OpenGlStateSnapshot.NetworkState state = new OpenGlStateSnapshot.NetworkState();
      final ShaderRenderer.PrimaryColorState cache = new ShaderRenderer.PrimaryColorState();
   }

   public static final class PrimaryColorState {
      private int instance;
      private int data;
      private int context;
      private int config;
      private int state;
      private OpenGlStateSnapshot.NetworkState cache;
      private int output;
      private int current;
      private boolean active;
      private int mode;
      private int selection;
      private int enabled;
      private int renderer;
      private float handler;
      private float animationDraw;
      private float pointEncode;
      private float animator;
      private boolean source;
      private boolean target;

      ShaderRenderer.PrimaryColorState handle(
         int var1,
         int var2,
         int var3,
         int var4,
         int var5,
         OpenGlStateSnapshot.NetworkState var6,
         int var7,
         int var8,
         boolean var9,
         int var10,
         int var11,
         int var12,
         int var13,
         float var14,
         float var15,
         float var16,
         float var17,
         boolean var18,
         boolean var19
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
         return this;
      }

      public int handle() {
         return this.instance;
      }

      public int process() {
         return this.data;
      }

      public int compute() {
         return this.context;
      }

      public int resolve() {
         return this.config;
      }

      public int update() {
         return this.state;
      }

      public OpenGlStateSnapshot.NetworkState apply() {
         return this.cache;
      }

      public int execute() {
         return this.output;
      }

      public int prepare() {
         return this.current;
      }

      public boolean check() {
         return this.active;
      }

      public int onTick() {
         return this.mode;
      }

      public int select() {
         return this.selection;
      }

      public int refresh() {
         return this.enabled;
      }

      public int render() {
         return this.renderer;
      }

      public float tick() {
         return this.handler;
      }

      public float drawAnimation() {
         return this.animationDraw;
      }

      public float encodePoint() {
         return this.pointEncode;
      }

      public float animate() {
         return this.animator;
      }

      public boolean load() {
         return this.source;
      }

      public boolean save() {
         return this.target;
      }
   }
}
